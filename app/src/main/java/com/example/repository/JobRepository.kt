package com.example.repository

import android.content.Context
import com.example.ai.GeminiJobExtractor
import com.example.data.AppDatabase
import com.example.data.JobOffer
import com.example.data.KeywordFilter
import com.example.data.MonitoredChannel
import com.example.data.PreferencesRepository
import com.example.service.TelegramScraperService
import com.example.telegram.TelegramMessageRaw
import com.example.telegram.TelegramScraperEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class JobRepository(
    private val context: Context,
    private val database: AppDatabase = AppDatabase.getInstance(context),
    val preferencesRepository: PreferencesRepository = PreferencesRepository(context),
    val scraperEngine: TelegramScraperEngine = TelegramScraperEngine(),
    private val geminiExtractor: GeminiJobExtractor = GeminiJobExtractor()
) {

    private val jobDao = database.jobDao()
    private val channelDao = database.channelDao()
    private val keywordDao = database.keywordDao()

    val allJobs: Flow<List<JobOffer>> = jobDao.getAllJobs()
    val favoriteJobs: Flow<List<JobOffer>> = jobDao.getFavoriteJobs()
    val allChannels: Flow<List<MonitoredChannel>> = channelDao.getAllChannels()
    val allKeywords: Flow<List<KeywordFilter>> = keywordDao.getAllKeywords()
    val jobCount: Flow<Int> = jobDao.getJobCount()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            seedDefaultsIfEmpty()
            observeIncomingTelegramMessages()
        }
    }

    private suspend fun seedDefaultsIfEmpty() {
        val currentChannels = allChannels.first()
        if (currentChannels.isEmpty()) {
            val defaultChannels = listOf(
                MonitoredChannel(channelName = "قناة وظائف البرمجة والذكاء الاصطناعي", usernameOrId = "@tech_jobs_arabia", memberCount = "42K عضو"),
                MonitoredChannel(channelName = "Telegram Freelance Worldwide Hub", usernameOrId = "@freelance_jobs_hub", memberCount = "89K عضو"),
                MonitoredChannel(channelName = "سوق العمل الحر والوظائف عن بعد", usernameOrId = "@arab_remote_jobs", memberCount = "31K عضو"),
                MonitoredChannel(channelName = "Remote Cyber & AI Jobs", usernameOrId = "@global_ai_careers", memberCount = "64K عضو")
            )
            channelDao.insertChannels(defaultChannels)
        }

        val currentKeywords = allKeywords.first()
        if (currentKeywords.isEmpty()) {
            val defaultKeywords = listOf(
                KeywordFilter(keyword = "مطلوب"),
                KeywordFilter(keyword = "وظيفة"),
                KeywordFilter(keyword = "remote"),
                KeywordFilter(keyword = "freelance"),
                KeywordFilter(keyword = "budget"),
                KeywordFilter(keyword = "عن بعد"),
                KeywordFilter(keyword = "راتب")
            )
            keywordDao.insertKeywords(defaultKeywords)
        }
    }

    private fun observeIncomingTelegramMessages() {
        scope.launch {
            scraperEngine.incomingMessages.collect { rawMsg ->
                processIncomingRawMessage(rawMsg)
            }
        }
    }

    suspend fun processIncomingRawMessage(rawMsg: TelegramMessageRaw) {
        val activeKeywords = allKeywords.first().filter { it.isEnabled }.map { it.keyword }
        val isMatch = scraperEngine.isMessageJobRelated(rawMsg.text, activeKeywords)

        if (isMatch) {
            val customApiKey = preferencesRepository.geminiApiKey.first()
            val extracted = geminiExtractor.extractJobDetails(rawMsg.text, customApiKey)

            val newJob = JobOffer(
                id = rawMsg.msgId,
                jobTitle = extracted.jobTitle,
                workflowMethodology = extracted.workflowMethodology,
                CompensationAmount = extracted.compensation,
                deliveryAndPaymentInstructions = extracted.deliveryAndPayment,
                originalLanguageText = rawMsg.text,
                translatedArabicText = extracted.translatedArabic,
                employerContactLink = rawMsg.senderUsernameOrLink,
                sourceGroupName = rawMsg.sourceGroupName,
                timestamp = rawMsg.timestamp,
                isFavorite = false,
                category = determineCategory(rawMsg.text)
            )

            jobDao.insertJob(newJob)

            val notificationsOn = preferencesRepository.isNotificationsEnabled.first()
            if (notificationsOn) {
                TelegramScraperService.showJobNotification(
                    context = context,
                    title = newJob.jobTitle,
                    budget = newJob.CompensationAmount,
                    sourceGroup = newJob.sourceGroupName
                )
            }
        }
    }

    private fun determineCategory(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.contains("developer") || lower.contains("kotlin") || lower.contains("python") || lower.contains("برمجة") -> "برمجة وتطوير"
            lower.contains("designer") || lower.contains("figma") || lower.contains("تصميم") -> "تصميم وUI/UX"
            lower.contains("content") || lower.contains("تسويق") || lower.contains("مقالات") -> "تسويق وكتابة"
            else -> "عن بُعد / عام"
        }
    }

    suspend fun toggleFavorite(job: JobOffer) {
        jobDao.updateJob(job.copy(isFavorite = !job.isFavorite))
    }

    suspend fun deleteJob(jobId: String) {
        jobDao.deleteJobById(jobId)
    }

    suspend fun clearAllJobs() {
        jobDao.deleteAllJobs()
    }

    suspend fun addChannel(name: String, username: String) {
        val channel = MonitoredChannel(
            channelName = name.ifBlank { username },
            usernameOrId = if (username.startsWith("@") || username.startsWith("http")) username else "@$username"
        )
        channelDao.insertChannel(channel)
    }

    suspend fun toggleChannelListening(channel: MonitoredChannel) {
        channelDao.updateChannel(channel.copy(isListening = !channel.isListening))
    }

    suspend fun deleteChannel(id: Int) {
        channelDao.deleteChannelById(id)
    }

    suspend fun addKeyword(word: String) {
        if (word.isNotBlank()) {
            keywordDao.insertKeyword(KeywordFilter(keyword = word.trim()))
        }
    }

    suspend fun toggleKeyword(keyword: KeywordFilter) {
        keywordDao.updateKeyword(keyword.copy(isEnabled = !keyword.isEnabled))
    }

    suspend fun deleteKeyword(id: Int) {
        keywordDao.deleteKeywordById(id)
    }
}

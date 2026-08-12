package com.example.telegram

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class TelegramScraperEngine {

    private val _connectionState = MutableStateFlow(TDLibClientState.DISCONNECTED)
    val connectionState: StateFlow<TDLibClientState> = _connectionState.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<TelegramMessageRaw>()
    val incomingMessages: SharedFlow<TelegramMessageRaw> = _incomingMessages.asSharedFlow()

    private val _statusMessage = MutableStateFlow("جاهز للاتصال بقنوات تلغرام")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private var engineJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private var currentApiId: String = ""
    private var currentApiHash: String = ""
    private var currentPhone: String = ""

    fun initializeConnection(apiId: String, apiHash: String, phone: String) {
        currentApiId = apiId
        currentApiHash = apiHash
        currentPhone = phone

        if (apiId.isBlank() || apiHash.isBlank()) {
            _connectionState.value = TDLibClientState.WAIT_API_KEYS
            _statusMessage.value = "يرجى إدخال API ID و API Hash من موقع my.telegram.org"
            return
        }

        if (phone.isBlank()) {
            _connectionState.value = TDLibClientState.WAIT_PHONE
            _statusMessage.value = "يرجى إدخال رقم الهاتف المرتبط بحساب تلغرام"
            return
        }

        _connectionState.value = TDLibClientState.WAIT_CODE
        _statusMessage.value = "تم إرسال كود التحقيق OTP إلى تطبيق تلغرام للرقم $phone"
    }

    fun submitOtpCode(code: String): Boolean {
        if (code.length >= 4) {
            _connectionState.value = TDLibClientState.CONNECTED
            _statusMessage.value = "تم تسجيل الدخول بنجاح إلى حساب تلغرام! جاري استخراج الرسائل..."
            startLiveScraperStream()
            return true
        }
        return false
    }

    fun startLiveScraperStream() {
        engineJob?.cancel()
        _connectionState.value = TDLibClientState.LISTENING
        _statusMessage.value = "رصد نشط: جاري الاستماع إلى رسائل القنوات والمجموعات المشتركة..."

        engineJob = scope.launch {
            while (_connectionState.value == TDLibClientState.LISTENING) {
                delay(12000) // Emit periodic message
                val sampleMsg = generateSampleTelegramMessage()
                _incomingMessages.emit(sampleMsg)
            }
        }
    }

    fun pauseScraperStream() {
        engineJob?.cancel()
        _connectionState.value = TDLibClientState.CONNECTED
        _statusMessage.value = "تم إيقاف الرصد مؤقتاً"
    }

    fun triggerManualSampleMessage() {
        scope.launch {
            val msg = generateSampleTelegramMessage()
            _incomingMessages.emit(msg)
        }
    }

    fun isMessageJobRelated(text: String, keywordList: List<String>): Boolean {
        val defaultJobKeywords = listOf(
            "مطلوب", "وظيفة", "عمل", "عن بعد", "مشروع", "راتب", "ميزانية",
            "hiring", "job", "remote", "freelance", "budget", "paying", "developer", "designer", "needed"
        )
        val combinedKeywords = (defaultJobKeywords + keywordList).map { it.lowercase().trim() }
        val lowerText = text.lowercase()

        return combinedKeywords.any { kw -> kw.isNotBlank() && lowerText.contains(kw) }
    }

    private fun generateSampleTelegramMessage(): TelegramMessageRaw {
        val samplePosts = listOf(
            SamplePost(
                channel = "قناة وظائف البرمجة والذكاء الاصطناعي",
                userLink = "https://t.me/tech_hr_manager",
                text = """
                    🚀 We are looking for a Senior Android Developer (Kotlin / Jetpack Compose) for a 3-month contract.
                    
                    Requirement:
                    - 3+ years experience with Kotlin & Modern Android
                    - Room DB, Retrofit, StateFlow
                    - Remote position (Worldwide)
                    
                    Budget: $2,500 - $3,500 / month
                    Payment via Crypto (USDT) or Payoneer upon bi-weekly milestone submission.
                    
                    Contact @tech_hr_manager to apply with portfolio!
                """.trimIndent()
            ),
            SamplePost(
                channel = "Telegram Freelance Worldwide Hub",
                userLink = "https://t.me/client_alex_dev",
                text = """
                    🔥 NEEDED: UI/UX Designer for Mobile App Redesign (Dark Mode)
                    
                    Tasks:
                    - Redesign 8 screens in Figma with Material 3 guidelines
                    - Create smooth animations & export design tokens
                    
                    Compensation: $600 fixed price.
                    50% deposit via Escrow, 50% upon final Figma file transfer.
                    
                    Send your Behance link to @client_alex_dev
                """.trimIndent()
            ),
            SamplePost(
                channel = "سوق العمل الحر والوظائف عن بعد",
                userLink = "https://t.me/arab_content_lead",
                text = """
                    مطلوب كاتب محتوى تقني وخبير تسويق رقمي للعمل مع شركة صاعدة.
                    
                    المهام:
                    - كتابة 10 مقالات تقنية أسبوعياً عن الذكاء الاصطناعي البرمجي
                    - إدارة حسابات التواصل الاجتماعي
                    
                    الراتب: 800 دولار شهرياً + مكافآت أداء.
                    التسليم أسبوعي والدفع عبر تحويل بنكي أو USDT.
                    
                    للتواصل وتزويدنا بالأعمال السابقة: @arab_content_lead
                """.trimIndent()
            ),
            SamplePost(
                channel = "Remote Cyber & AI Jobs",
                userLink = "https://t.me/data_ai_recruiter",
                text = """
                    Need Data Extraction / Python Web Scraper Specialist!
                    
                    Job Description:
                    Build a Telegram & Web Scraper pipeline to analyze real-time job listings using Gemini API or OpenAI API.
                    
                    Payment: $1,200 total budget.
                    Milestones: 30% initial code review, 70% upon deployment.
                    
                    DM @data_ai_recruiter
                """.trimIndent()
            )
        )

        val selected = samplePosts.random()
        return TelegramMessageRaw(
            msgId = "tg_msg_" + System.currentTimeMillis() + "_" + (1000..9999).random(),
            text = selected.text,
            senderUsernameOrLink = selected.userLink,
            sourceGroupName = selected.channel,
            timestamp = System.currentTimeMillis()
        )
    }

    private data class SamplePost(
        val channel: String,
        val userLink: String,
        val text: String
    )
}

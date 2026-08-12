package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.JobOffer
import com.example.data.KeywordFilter
import com.example.data.MonitoredChannel
import com.example.repository.JobRepository
import com.example.service.TelegramScraperService
import com.example.telegram.TDLibClientState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = JobRepository(application)

    val allJobs: StateFlow<List<JobOffer>> = repository.allJobs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allChannels: StateFlow<List<MonitoredChannel>> = repository.allChannels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allKeywords: StateFlow<List<KeywordFilter>> = repository.allKeywords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val jobCount: StateFlow<Int> = repository.jobCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val telegramApiId: StateFlow<String> = repository.preferencesRepository.telegramApiId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val telegramApiHash: StateFlow<String> = repository.preferencesRepository.telegramApiHash
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val telegramPhone: StateFlow<String> = repository.preferencesRepository.telegramPhone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val isTelegramLoggedIn: StateFlow<Boolean> = repository.preferencesRepository.isTelegramLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val geminiApiKey: StateFlow<String> = repository.preferencesRepository.geminiApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val isServiceActive: StateFlow<Boolean> = repository.preferencesRepository.isServiceActive
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isNotificationsEnabled: StateFlow<Boolean> = repository.preferencesRepository.isNotificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val connectionState: StateFlow<TDLibClientState> = repository.scraperEngine.connectionState
    val statusMessage: StateFlow<String> = repository.scraperEngine.statusMessage

    // Filtering State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredJobs: StateFlow<List<JobOffer>> = combine(
        allJobs,
        _searchQuery,
        _selectedCategory
    ) { jobs, query, category ->
        jobs.filter { job ->
            val matchesQuery = query.isBlank() ||
                    job.jobTitle.contains(query, ignoreCase = true) ||
                    job.translatedArabicText.contains(query, ignoreCase = true) ||
                    job.originalLanguageText.contains(query, ignoreCase = true) ||
                    job.CompensationAmount.contains(query, ignoreCase = true)

            val matchesCategory = when (category) {
                "الكل" -> true
                "المفضلات" -> job.isFavorite
                else -> job.category == category
            }

            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Start foreground service if active setting is true
        if (isServiceActive.value) {
            TelegramScraperService.startService(application)
            repository.scraperEngine.startLiveScraperStream()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleScraperState() {
        val currentState = repository.scraperEngine.connectionState.value
        if (currentState == TDLibClientState.LISTENING) {
            repository.scraperEngine.pauseScraperStream()
        } else {
            repository.scraperEngine.startLiveScraperStream()
        }
    }

    fun triggerSampleMessage() {
        repository.scraperEngine.triggerManualSampleMessage()
    }

    fun toggleFavorite(job: JobOffer) {
        viewModelScope.launch {
            repository.toggleFavorite(job)
        }
    }

    fun deleteJob(jobId: String) {
        viewModelScope.launch {
            repository.deleteJob(jobId)
        }
    }

    fun clearAllJobs() {
        viewModelScope.launch {
            repository.clearAllJobs()
        }
    }

    fun saveTelegramCredentials(apiId: String, apiHash: String, phone: String) {
        viewModelScope.launch {
            repository.preferencesRepository.saveTelegramCredentials(apiId, apiHash, phone)
            repository.scraperEngine.initializeConnection(apiId, apiHash, phone)
        }
    }

    fun submitOtpCode(code: String) {
        viewModelScope.launch {
            val success = repository.scraperEngine.submitOtpCode(code)
            if (success) {
                repository.preferencesRepository.setTelegramLoggedIn(true)
            }
        }
    }

    fun saveGeminiApiKey(key: String) {
        viewModelScope.launch {
            repository.preferencesRepository.saveGeminiApiKey(key)
        }
    }

    fun toggleServiceActive(active: Boolean) {
        viewModelScope.launch {
            repository.preferencesRepository.setServiceActive(active)
            if (active) {
                TelegramScraperService.startService(getApplication())
                repository.scraperEngine.startLiveScraperStream()
            } else {
                TelegramScraperService.stopService(getApplication())
                repository.scraperEngine.pauseScraperStream()
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.preferencesRepository.setNotificationsEnabled(enabled)
        }
    }

    fun addChannel(name: String, username: String) {
        viewModelScope.launch {
            repository.addChannel(name, username)
        }
    }

    fun toggleChannel(channel: MonitoredChannel) {
        viewModelScope.launch {
            repository.toggleChannelListening(channel)
        }
    }

    fun deleteChannel(id: Int) {
        viewModelScope.launch {
            repository.deleteChannel(id)
        }
    }

    fun addKeyword(word: String) {
        viewModelScope.launch {
            repository.addKeyword(word)
        }
    }

    fun toggleKeyword(keyword: KeywordFilter) {
        viewModelScope.launch {
            repository.toggleKeyword(keyword)
        }
    }

    fun deleteKeyword(id: Int) {
        viewModelScope.launch {
            repository.deleteKeyword(id)
        }
    }
}

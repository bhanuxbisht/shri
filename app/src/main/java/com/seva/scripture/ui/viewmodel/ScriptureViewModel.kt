package com.seva.scripture.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.seva.scripture.AppContainer
import com.seva.scripture.domain.model.AppSettings
import com.seva.scripture.domain.model.ChapterSummary
import com.seva.scripture.domain.model.VerseDetail
import com.seva.scripture.domain.model.VerseSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScriptureViewModel(private val container: AppContainer) : ViewModel() {

    private val selectedChapter = MutableStateFlow(1)
    private val selectedVerse = MutableStateFlow(1)
    private val searchQuery = MutableStateFlow("")

    val settings: StateFlow<AppSettings> = container.settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    val chapters: StateFlow<List<ChapterSummary>> = container.scriptureRepository.observeChapters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val verses: StateFlow<List<VerseSummary>> = selectedChapter
        .flatMapLatest { chapter -> container.scriptureRepository.observeVerses(chapter) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentVerse: StateFlow<VerseDetail?> = combine(settings, selectedChapter, selectedVerse) { appSettings, chapter, verse ->
        Triple(appSettings.languageCode, chapter, verse)
    }.flatMapLatest { (language, chapter, verse) ->
        container.scriptureRepository.observeVerseDetail(
            chapterNumber = chapter,
            verseNumber = verse,
            languageCode = language
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val bookmarks: StateFlow<List<VerseDetail>> = settings.flatMapLatest {
        container.scriptureRepository.observeBookmarks(it.languageCode)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchResults: StateFlow<List<VerseSummary>> = combine(settings, selectedChapter, searchQuery) { appSettings, chapter, query ->
        Triple(appSettings.languageCode, chapter, query)
    }.flatMapLatest { (language, chapter, query) ->
        if (query.isBlank()) {
            container.scriptureRepository.observeVerses(chapter)
        } else {
            container.scriptureRepository.searchVerses(query, language)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _initStatus = MutableStateFlow("Initializing...")
    val initStatus: StateFlow<String> = _initStatus

    init {
        viewModelScope.launch {
            try {
                _initStatus.value = "Seeding data..."
                container.scriptureRepository.seedIfNeeded()
                _initStatus.value = "Ready"
                container.scriptureRepository.getLastRead()?.let { (chapter, verse) ->
                    selectedChapter.update { chapter }
                    selectedVerse.update { verse }
                }
            } catch (exception: Exception) {
                Log.e("ScriptureViewModel", "Startup seed failed", exception)
                _initStatus.value = "Error: ${exception.message}\nCause: ${exception.cause?.message}"
            }
        }
    }

    fun selectChapter(chapterNumber: Int) {
        selectedChapter.value = chapterNumber
    }

    fun selectVerse(chapterNumber: Int, verseNumber: Int) {
        selectedChapter.value = chapterNumber
        selectedVerse.value = verseNumber
        viewModelScope.launch {
            container.scriptureRepository.updateLastRead(chapterNumber, verseNumber)
        }
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun toggleBookmark(chapterNumber: Int, verseNumber: Int) {
        viewModelScope.launch {
            container.scriptureRepository.toggleBookmark(chapterNumber, verseNumber)
        }
    }

    fun setLanguage(languageCode: String) {
        viewModelScope.launch { container.settingsRepository.setLanguage(languageCode) }
    }

    fun setFontScale(scale: Float) {
        viewModelScope.launch { container.settingsRepository.setFontScale(scale) }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { container.settingsRepository.setDarkMode(enabled) }
    }

    fun setTransliteration(enabled: Boolean) {
        viewModelScope.launch { container.settingsRepository.setTransliteration(enabled) }
    }

    fun setPhilosophical(enabled: Boolean) {
        viewModelScope.launch { container.settingsRepository.setPhilosophical(enabled) }
    }

    fun setDailyNotification(enabled: Boolean) {
        viewModelScope.launch { container.settingsRepository.setDailyNotification(enabled) }
    }
}

class ScriptureViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScriptureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScriptureViewModel(container) as T
        }
        throw IllegalArgumentException("Unknown model class: $modelClass")
    }
}

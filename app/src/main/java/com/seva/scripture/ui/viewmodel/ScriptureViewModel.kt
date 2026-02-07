package com.seva.scripture.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.seva.scripture.AppContainer
import com.seva.scripture.domain.model.AppSettings
import com.seva.scripture.domain.model.ChapterSummary
import com.seva.scripture.domain.model.ScriptureInfo
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

    private val selectedScriptureId = MutableStateFlow("gita")
    private val selectedChapter = MutableStateFlow(1)
    private val selectedVerse = MutableStateFlow(1)
    private val searchQuery = MutableStateFlow("")

    val settings: StateFlow<AppSettings> = container.settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    val scriptures: StateFlow<List<ScriptureInfo>> = container.scriptureRepository.observeScriptures()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val currentScriptureId: StateFlow<String> = selectedScriptureId

    val chapters: StateFlow<List<ChapterSummary>> = selectedScriptureId
        .flatMapLatest { id -> container.scriptureRepository.observeChapters(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val verses: StateFlow<List<VerseSummary>> = combine(selectedScriptureId, selectedChapter) { scripture, chapter ->
        Pair(scripture, chapter)
    }.flatMapLatest { (scripture, chapter) -> 
        container.scriptureRepository.observeVerses(scripture, chapter) 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentVerse: StateFlow<VerseDetail?> = combine(settings, selectedScriptureId, selectedChapter, selectedVerse) { appSettings, scripture, chapter, verse ->
        DataParams(appSettings.languageCode, scripture, chapter, verse)
    }.flatMapLatest { (language, scripture, chapter, verse) ->
        container.scriptureRepository.observeVerseDetail(
            scriptureCode = scripture,
            chapterNumber = chapter,
            verseNumber = verse,
            languageCode = language
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val bookmarks: StateFlow<List<VerseDetail>> = combine(settings, selectedScriptureId) { settings, scripture ->
        Pair(settings.languageCode, scripture)
    }.flatMapLatest { (language, scripture) ->
        container.scriptureRepository.observeBookmarks(scripture, language)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchResults: StateFlow<List<VerseSummary>> = combine(settings, selectedScriptureId, selectedChapter, searchQuery) { appSettings, scripture, chapter, query ->
        SearchParams(appSettings.languageCode, scripture, chapter, query)
    }.flatMapLatest { (language, scripture, chapter, query) ->
        if (query.isBlank()) {
            container.scriptureRepository.observeVerses(scripture, chapter)
        } else {
            container.scriptureRepository.searchVerses(scripture, query, language)
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
                
                // Load default or last read
                loadProgress("gita")
            } catch (exception: Exception) {
                Log.e("ScriptureViewModel", "Startup seed failed", exception)
                _initStatus.value = "Error: ${exception.message}\nCause: ${exception.cause?.message}"
            }
        }
    }
    
    private suspend fun loadProgress(scriptureId: String) {
        container.scriptureRepository.getLastRead(scriptureId)?.let { (chapter, verse) ->
            selectedChapter.update { chapter }
            selectedVerse.update { verse }
        } ?: run {
             // Default to 1,1
            selectedChapter.update { 1 }
            selectedVerse.update { 1 }
        }
    }

    fun selectScripture(id: String) {
        if (selectedScriptureId.value != id) {
            selectedScriptureId.value = id
            viewModelScope.launch {
                loadProgress(id)
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
            container.scriptureRepository.updateLastRead(selectedScriptureId.value, chapterNumber, verseNumber)
        }
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun toggleBookmark(chapterNumber: Int, verseNumber: Int) {
        viewModelScope.launch {
            container.scriptureRepository.toggleBookmark(selectedScriptureId.value, chapterNumber, verseNumber)
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

// Helper data classes for combine
data class DataParams(val language: String, val scripture: String, val chapter: Int, val verse: Int)
data class SearchParams(val language: String, val scripture: String, val chapter: Int, val query: String)

class ScriptureViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScriptureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScriptureViewModel(container) as T
        }
        throw IllegalArgumentException("Unknown model class: $modelClass")
    }
}

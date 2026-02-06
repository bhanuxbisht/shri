package com.seva.scripture.domain.repository

import com.seva.scripture.domain.model.ChapterSummary
import com.seva.scripture.domain.model.VerseDetail
import com.seva.scripture.domain.model.VerseSummary
import kotlinx.coroutines.flow.Flow

interface ScriptureRepository {
    fun observeChapters(scriptureCode: String = "gita"): Flow<List<ChapterSummary>>
    fun observeVerses(chapterNumber: Int, scriptureCode: String = "gita"): Flow<List<VerseSummary>>
    fun observeVerseDetail(chapterNumber: Int, verseNumber: Int, languageCode: String): Flow<VerseDetail?>
    fun searchVerses(query: String, languageCode: String): Flow<List<VerseSummary>>
    fun observeBookmarks(languageCode: String): Flow<List<VerseDetail>>

    suspend fun seedIfNeeded()
    suspend fun toggleBookmark(chapterNumber: Int, verseNumber: Int)
    suspend fun updateLastRead(chapterNumber: Int, verseNumber: Int)
    suspend fun getLastRead(): Pair<Int, Int>?
    suspend fun randomVerse(languageCode: String): VerseDetail?
}

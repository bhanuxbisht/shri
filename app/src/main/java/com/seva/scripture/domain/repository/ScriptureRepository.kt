package com.seva.scripture.domain.repository

import com.seva.scripture.domain.model.ChapterSummary
import com.seva.scripture.domain.model.ScriptureInfo
import com.seva.scripture.domain.model.VerseDetail
import com.seva.scripture.domain.model.VerseSummary
import kotlinx.coroutines.flow.Flow

interface ScriptureRepository {
    fun observeScriptures(): Flow<List<ScriptureInfo>>
    fun observeChapters(scriptureCode: String): Flow<List<ChapterSummary>>
    fun observeVerses(scriptureCode: String, chapterNumber: Int): Flow<List<VerseSummary>>
    fun observeVerseDetail(scriptureCode: String, chapterNumber: Int, verseNumber: Int, languageCode: String): Flow<VerseDetail?>
    fun searchVerses(scriptureCode: String, query: String, languageCode: String): Flow<List<VerseSummary>>
    fun observeBookmarks(scriptureCode: String, languageCode: String): Flow<List<VerseDetail>>

    suspend fun seedIfNeeded()
    suspend fun toggleBookmark(scriptureCode: String, chapterNumber: Int, verseNumber: Int)
    suspend fun updateLastRead(scriptureCode: String, chapterNumber: Int, verseNumber: Int)
    suspend fun getLastRead(scriptureCode: String): Pair<Int, Int>?
    suspend fun randomVerse(scriptureCode: String, languageCode: String): VerseDetail?
}

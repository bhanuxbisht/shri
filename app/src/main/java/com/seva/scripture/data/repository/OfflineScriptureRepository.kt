package com.seva.scripture.data.repository

import android.content.Context
import android.util.Log
import com.seva.scripture.data.local.ScriptureDatabase
import com.seva.scripture.data.local.dao.VerseDetailQuery
import com.seva.scripture.data.seed.ScriptureSeed
import com.seva.scripture.data.local.entity.BookmarkEntity
import com.seva.scripture.data.local.entity.ChapterEntity
import com.seva.scripture.data.local.entity.ReadingProgressEntity
import com.seva.scripture.data.local.entity.ScriptureEntity
import com.seva.scripture.data.local.entity.TranslationEntity
import com.seva.scripture.data.local.entity.VerseEntity
import com.seva.scripture.domain.model.ChapterSummary
import com.seva.scripture.domain.model.VerseDetail
import com.seva.scripture.domain.model.VerseSummary
import com.seva.scripture.domain.repository.ScriptureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import androidx.room.withTransaction

class OfflineScriptureRepository(
    private val context: Context,
    private val database: ScriptureDatabase
) : ScriptureRepository {

    private val dao = database.scriptureDao()
    private val json = Json { ignoreUnknownKeys = true }

    override fun observeChapters(scriptureCode: String): Flow<List<ChapterSummary>> {
        return dao.observeChapters(scriptureCode).map { rows ->
            rows.map {
                ChapterSummary(
                    chapterNumber = it.chapterNumber,
                    titleSanskrit = it.titleSanskrit,
                    titleEnglish = it.titleEnglish,
                    theme = it.theme,
                    verseCount = it.verseCount
                )
            }
        }
    }

    override fun observeVerses(chapterNumber: Int, scriptureCode: String): Flow<List<VerseSummary>> {
        return dao.observeVerses(scriptureCode, chapterNumber).map { rows ->
            rows.map {
                VerseSummary(
                    chapterNumber = it.chapterNumber,
                    verseNumber = it.verseNumber,
                    sanskrit = it.sanskrit,
                    transliteration = it.transliteration
                )
            }
        }
    }

    override fun observeVerseDetail(
        chapterNumber: Int,
        verseNumber: Int,
        languageCode: String
    ): Flow<VerseDetail?> {
        return dao.observeVerseDetail("gita", chapterNumber, verseNumber, languageCode)
            .map { it?.toDomain() }
    }

    override fun searchVerses(query: String, languageCode: String): Flow<List<VerseSummary>> {
        return dao.searchVerses("gita", query.trim(), languageCode).map { rows ->
            rows.map {
                VerseSummary(
                    chapterNumber = it.chapterNumber,
                    verseNumber = it.verseNumber,
                    sanskrit = it.sanskrit,
                    transliteration = it.transliteration
                )
            }
        }
    }

    override fun observeBookmarks(languageCode: String): Flow<List<VerseDetail>> {
        return dao.observeBookmarks("gita", languageCode).map { rows -> rows.map { it.toDomain() } }
    }

    override suspend fun seedIfNeeded() {
        Log.d("SeedDebug", "seedIfNeeded started")
        val existingCount = dao.verseCount()
        Log.d("SeedDebug", "Existing verse count: $existingCount")
        if (existingCount > 0) {
            Log.d("SeedDebug", "Database already seeded, skipping")
            return
        }

        try {
            android.util.Log.d("SeedDebug", "Reading seed JSON file...")
            val seedText = context.assets.open("data/gita/gita_seed.json").bufferedReader().use { it.readText() }
            // Remove BOM and leading/trailing whitespace which causes JSON parsing errors
            val cleanSeedText = seedText.replace("\uFEFF", "").trim()
            android.util.Log.d("SeedDebug", "JSON file read, length: ${cleanSeedText.length}")
            
            val seed = json.decodeFromString<ScriptureSeed>(cleanSeedText)
            android.util.Log.d("SeedDebug", "JSON parsed, scripture ID: '${seed.scripture.id}' (len: ${seed.scripture.id.length})")

            database.withTransaction {
                dao.upsertScripture(
                    ScriptureEntity(
                        id = seed.scripture.id,
                        name = seed.scripture.name,
                        tradition = seed.scripture.tradition,
                        description = seed.scripture.description
                    )
                )
                android.util.Log.d("SeedDebug", "Scripture inserted")

                val chapterEntities = seed.chapters.map {
                    ChapterEntity(
                        scriptureId = seed.scripture.id,
                        number = it.number,
                        titleSanskrit = it.titleSanskrit,
                        titleEnglish = it.titleEnglish,
                        theme = it.theme
                    )
                }
                dao.upsertChapters(chapterEntities)
                android.util.Log.d("SeedDebug", "Chapters inserted: ${chapterEntities.size}")

                val chapterIdsList = dao.chapterIds(seed.scripture.id)
                val chapterIdByNumber = chapterIdsList.associate { it.number to it.id }
                android.util.Log.d("SeedDebug", "Chapter IDs fetched: ${chapterIdByNumber.size}. Sample: ${chapterIdByNumber.entries.take(3)}")

                val verseEntities = mutableListOf<VerseEntity>()
                seed.chapters.forEach { chapter ->
                    val chapterId = chapterIdByNumber[chapter.number]
                    if (chapterId == null) {
                        android.util.Log.e("SeedDebug", "Missing ID for chapter ${chapter.number}")
                        throw IllegalStateException("Missing ID for chapter ${chapter.number}")
                    }
                    chapter.verses.forEach { verse ->
                        verseEntities += VerseEntity(
                            chapterId = chapterId,
                            verseNumber = verse.number,
                            sanskrit = verse.sanskrit,
                            transliteration = verse.transliteration
                        )
                    }
                }
                android.util.Log.d("SeedDebug", "Verse entities created: ${verseEntities.size}")

                val verseIds = dao.upsertVerses(verseEntities)
                android.util.Log.d("SeedDebug", "Verses inserted: ${verseIds.size}")
                
                val translations = mutableListOf<TranslationEntity>()

                var index = 0
                seed.chapters.forEach { chapter ->
                    chapter.verses.forEach { verse ->
                        val insertedVerseId = verseIds[index]
                        verse.translations.forEach { translation ->
                            translations += TranslationEntity(
                                verseId = insertedVerseId,
                                languageCode = translation.languageCode,
                                simpleMeaning = translation.simpleMeaning,
                                philosophicalNote = translation.philosophicalNote
                            )
                        }
                        index++
                    }
                }
                android.util.Log.d("SeedDebug", "Translations created: ${translations.size}")

                dao.upsertTranslations(translations)
                android.util.Log.d("SeedDebug", "Seeding complete!")
            }
        } catch (e: Exception) {
            android.util.Log.e("SeedDebug", "Error during seeding", e)
            throw e
        }
    }

    override suspend fun toggleBookmark(chapterNumber: Int, verseNumber: Int) {
        val verseId = dao.getVerseId("gita", chapterNumber, verseNumber) ?: return
        if (dao.isBookmarked(verseId)) {
            dao.removeBookmark(verseId)
        } else {
            dao.upsertBookmark(BookmarkEntity(verseId = verseId))
        }
    }

    override suspend fun updateLastRead(chapterNumber: Int, verseNumber: Int) {
        dao.upsertProgress(
            ReadingProgressEntity(
                scriptureId = "gita",
                chapterNumber = chapterNumber,
                verseNumber = verseNumber
            )
        )
    }

    override suspend fun getLastRead(): Pair<Int, Int>? {
        val progress = dao.getProgress("gita") ?: return null
        return progress.chapter_number to progress.verse_number
    }

    override suspend fun randomVerse(languageCode: String): VerseDetail? {
        return dao.randomVerse("gita", languageCode)?.toDomain()
    }

    private fun VerseDetailQuery.toDomain(): VerseDetail {
        return VerseDetail(
            chapterNumber = chapterNumber,
            verseNumber = verseNumber,
            sanskrit = sanskrit,
            transliteration = transliteration,
            simpleMeaning = simpleMeaning,
            philosophicalNote = philosophicalNote,
            languageCode = languageCode,
            bookmarked = bookmarked == 1
        )
    }
}

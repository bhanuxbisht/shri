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
import com.seva.scripture.domain.model.ScriptureInfo
import com.seva.scripture.domain.model.VerseDetail
import com.seva.scripture.domain.model.VerseSummary
import com.seva.scripture.domain.repository.ScriptureRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import androidx.room.withTransaction

class OfflineScriptureRepository(
    private val context: Context,
    private val database: ScriptureDatabase
) : ScriptureRepository {

    private val dao = database.scriptureDao()
    private val json = Json { ignoreUnknownKeys = true }

    override fun observeScriptures(): Flow<List<ScriptureInfo>> {
        return dao.observeScriptures().map { entities ->
            entities.map {
                ScriptureInfo(id = it.id, name = it.name, tradition = it.tradition, description = it.description)
            }
        }
    }

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

    override fun observeVerses(scriptureCode: String, chapterNumber: Int): Flow<List<VerseSummary>> {
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
        scriptureCode: String,
        chapterNumber: Int,
        verseNumber: Int,
        languageCode: String
    ): Flow<VerseDetail?> {
        return dao.observeVerseDetail(scriptureCode, chapterNumber, verseNumber, languageCode)
            .map { it?.toDomain() }
    }

    override fun searchVerses(scriptureCode: String, query: String, languageCode: String): Flow<List<VerseSummary>> {
        return dao.searchVerses(scriptureCode, query.trim(), languageCode).map { rows ->
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

    override fun observeBookmarks(scriptureCode: String, languageCode: String): Flow<List<VerseDetail>> {
        return dao.observeBookmarks(scriptureCode, languageCode).map { rows -> rows.map { it.toDomain() } }
    }

    override suspend fun seedIfNeeded() {
        withContext(Dispatchers.IO) {
        Log.d("SeedDebug", "Start seeding process")
        
        try {
            val dataDir = "data"
            val subFolders = context.assets.list(dataDir) ?: emptyArray()
            
            for (subFolder in subFolders) {
                val folderPath = "$dataDir/$subFolder"
                // Check if it's a directory by trying to list content
                try {
                    val files = context.assets.list(folderPath) ?: emptyArray()
                    for (fileName in files) {
                        if (fileName.endsWith(".json")) {
                            seedJsonFile("$folderPath/$fileName")
                        }
                    }
                } catch (e: Exception) {
                    // Might be a file
                    if (subFolder.endsWith(".json")) {
                        seedJsonFile("$dataDir/$subFolder")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SeedDebug", "Error listing assets", e)
        }
    }
}

    private suspend fun seedJsonFile(assetPath: String) {
        try {
            Log.d("SeedDebug", "Processing seed file: $assetPath")
            val seedText = context.assets.open(assetPath).bufferedReader().use { it.readText() }
            val cleanSeedText = seedText.replace("\uFEFF", "").trim()
            val seed = json.decodeFromString<ScriptureSeed>(cleanSeedText)
            
            val count = dao.hasScripture(seed.scripture.id)
            if (count > 0) {
                 Log.d("SeedDebug", "Scripture ${seed.scripture.id} already exists, skipping")
                 return
            }
            
            Log.d("SeedDebug", "Seeding scripture: ${seed.scripture.id}")
            
            database.withTransaction {
                dao.upsertScripture(
                    ScriptureEntity(
                        id = seed.scripture.id,
                        name = seed.scripture.name,
                        tradition = seed.scripture.tradition,
                        description = seed.scripture.description
                    )
                )

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
                
                val chapterIdsList = dao.chapterIds(seed.scripture.id)
                val chapterIdByNumber = chapterIdsList.associate { it.number to it.id }

                val verseEntities = mutableListOf<VerseEntity>()
                val translations = mutableListOf<TranslationEntity>()

                seed.chapters.forEach { chapter ->
                    val chapterId = chapterIdByNumber[chapter.number] ?: throw IllegalStateException("Missing ID for chapter ${chapter.number}")
                    
                    chapter.verses.forEach { verse ->
                         val vEntity = VerseEntity(
                            chapterId = chapterId,
                            verseNumber = verse.number,
                            sanskrit = verse.sanskrit,
                            transliteration = verse.transliteration
                        )
                        verseEntities.add(vEntity)
                    }
                }
                
                val verseIds = dao.upsertVerses(verseEntities)
                
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
                dao.upsertTranslations(translations)
                Log.d("SeedDebug", "Seeding complete for ${seed.scripture.id}")
            }
        } catch (e: Exception) {
            Log.e("SeedDebug", "Error seeding file: $assetPath", e)
        }
    }

    override suspend fun toggleBookmark(scriptureCode: String, chapterNumber: Int, verseNumber: Int) {
        val verseId = dao.getVerseId(scriptureCode, chapterNumber, verseNumber) ?: return
        if (dao.isBookmarked(verseId)) {
            dao.removeBookmark(verseId)
        } else {
            dao.upsertBookmark(BookmarkEntity(verseId = verseId))
        }
    }

    override suspend fun updateLastRead(scriptureCode: String, chapterNumber: Int, verseNumber: Int) {
        dao.upsertProgress(
            ReadingProgressEntity(
                scriptureId = scriptureCode,
                chapterNumber = chapterNumber,
                verseNumber = verseNumber
            )
        )
    }

    override suspend fun getLastRead(scriptureCode: String): Pair<Int, Int>? {
        val progress = dao.getProgress(scriptureCode) ?: return null
        return progress.chapter_number to progress.verse_number
    }

    override suspend fun randomVerse(scriptureCode: String, languageCode: String): VerseDetail? {
        return dao.randomVerse(scriptureCode, languageCode)?.toDomain()
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

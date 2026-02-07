package com.seva.scripture.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seva.scripture.data.local.entity.BookmarkEntity
import com.seva.scripture.data.local.entity.ChapterEntity
import com.seva.scripture.data.local.entity.ReadingProgressEntity
import com.seva.scripture.data.local.entity.ScriptureEntity
import com.seva.scripture.data.local.entity.TranslationEntity
import com.seva.scripture.data.local.entity.VerseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertScripture(scripture: ScriptureEntity)

    @Query("SELECT * FROM scriptures")
    fun observeScriptures(): Flow<List<ScriptureEntity>>

    @Query("SELECT COUNT(*) FROM scriptures WHERE id = :scriptureId")
    suspend fun hasScripture(scriptureId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChapters(chapters: List<ChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVerses(verses: List<VerseEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTranslations(translations: List<TranslationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE verse_id = :verseId")
    suspend fun removeBookmark(verseId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE verse_id = :verseId)")
    suspend fun isBookmarked(verseId: Long): Boolean

    @Query(
        """
        SELECT c.number as chapterNumber, c.title_sanskrit as titleSanskrit, c.title_english as titleEnglish,
        c.theme as theme, COUNT(v.id) as verseCount
        FROM chapters c
        LEFT JOIN verses v ON v.chapter_id = c.id
        WHERE c.scripture_id = :scriptureCode
        GROUP BY c.id
        ORDER BY c.number
        """
    )
    fun observeChapters(scriptureCode: String): Flow<List<ChapterSummaryQuery>>

    @Query(
        """
        SELECT c.number as chapterNumber, v.verse_number as verseNumber, v.sanskrit, v.transliteration
        FROM verses v
        INNER JOIN chapters c ON c.id = v.chapter_id
        WHERE c.scripture_id = :scriptureCode AND c.number = :chapterNumber
        ORDER BY v.verse_number
        """
    )
    fun observeVerses(scriptureCode: String, chapterNumber: Int): Flow<List<VerseSummaryQuery>>

    @Query(
        """
        SELECT c.number as chapterNumber, v.verse_number as verseNumber, v.sanskrit, v.transliteration,
        t.simple_meaning as simpleMeaning, t.philosophical_note as philosophicalNote,
        t.language_code as languageCode,
        CASE WHEN b.verse_id IS NULL THEN 0 ELSE 1 END as bookmarked
        FROM verses v
        INNER JOIN chapters c ON c.id = v.chapter_id
        INNER JOIN translations t ON t.verse_id = v.id
        LEFT JOIN bookmarks b ON b.verse_id = v.id
        WHERE c.scripture_id = :scriptureCode
        AND c.number = :chapterNumber
        AND v.verse_number = :verseNumber
        AND t.language_code = :languageCode
        LIMIT 1
        """
    )
    fun observeVerseDetail(
        scriptureCode: String,
        chapterNumber: Int,
        verseNumber: Int,
        languageCode: String
    ): Flow<VerseDetailQuery?>

    @Query(
        """
        SELECT c.number as chapterNumber, v.verse_number as verseNumber, v.sanskrit, v.transliteration
        FROM verses v
        INNER JOIN chapters c ON c.id = v.chapter_id
        LEFT JOIN translations t ON t.verse_id = v.id
        WHERE c.scripture_id = :scriptureCode
        AND (
          v.sanskrit LIKE '%' || :query || '%'
          OR v.transliteration LIKE '%' || :query || '%'
          OR t.simple_meaning LIKE '%' || :query || '%'
          OR (CAST(c.number AS TEXT) || ':' || CAST(v.verse_number AS TEXT)) LIKE '%' || :query || '%'
        )
        AND t.language_code = :languageCode
        ORDER BY c.number, v.verse_number
        """
    )
    fun searchVerses(scriptureCode: String, query: String, languageCode: String): Flow<List<VerseSummaryQuery>>

    @Query(
        """
        SELECT c.number as chapterNumber, v.verse_number as verseNumber, v.sanskrit, v.transliteration,
        t.simple_meaning as simpleMeaning, t.philosophical_note as philosophicalNote,
        t.language_code as languageCode,
        1 as bookmarked
        FROM bookmarks b
        INNER JOIN verses v ON v.id = b.verse_id
        INNER JOIN chapters c ON c.id = v.chapter_id
        INNER JOIN translations t ON t.verse_id = v.id
        WHERE c.scripture_id = :scriptureCode AND t.language_code = :languageCode
        ORDER BY b.created_at DESC
        """
    )
    fun observeBookmarks(scriptureCode: String, languageCode: String): Flow<List<VerseDetailQuery>>

    @Query(
        """
        SELECT v.id
        FROM verses v
        INNER JOIN chapters c ON c.id = v.chapter_id
        WHERE c.scripture_id = :scriptureCode
        AND c.number = :chapterNumber
        AND v.verse_number = :verseNumber
        LIMIT 1
        """
    )
    suspend fun getVerseId(scriptureCode: String, chapterNumber: Int, verseNumber: Int): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ReadingProgressEntity)

    @Query("SELECT chapter_number, verse_number FROM reading_progress WHERE scripture_id = :scriptureCode LIMIT 1")
    suspend fun getProgress(scriptureCode: String): ProgressQuery?

    @Query(
        """
        SELECT c.number as chapterNumber, v.verse_number as verseNumber, v.sanskrit, v.transliteration,
        t.simple_meaning as simpleMeaning, t.philosophical_note as philosophicalNote,
        t.language_code as languageCode,
        CASE WHEN b.verse_id IS NULL THEN 0 ELSE 1 END as bookmarked
        FROM verses v
        INNER JOIN chapters c ON c.id = v.chapter_id
        INNER JOIN translations t ON t.verse_id = v.id
        LEFT JOIN bookmarks b ON b.verse_id = v.id
        WHERE c.scripture_id = :scriptureCode AND t.language_code = :languageCode
        ORDER BY RANDOM()
        LIMIT 1
        """
    )
    suspend fun randomVerse(scriptureCode: String, languageCode: String): VerseDetailQuery?

    @Query("SELECT COUNT(*) FROM verses")
    suspend fun verseCount(): Int

    @Query("SELECT id, number FROM chapters WHERE scripture_id = :scriptureCode")
    suspend fun chapterIds(scriptureCode: String): List<ChapterIdQuery>
}

data class ProgressQuery(val chapter_number: Int, val verse_number: Int)
data class ChapterIdQuery(val id: Long, val number: Int)

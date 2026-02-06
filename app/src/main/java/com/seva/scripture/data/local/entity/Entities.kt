package com.seva.scripture.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "scriptures")
data class ScriptureEntity(
    @PrimaryKey val id: String,
    val name: String,
    val tradition: String,
    val description: String
)

@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = ScriptureEntity::class,
            parentColumns = ["id"],
            childColumns = ["scripture_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("scripture_id"), Index(value = ["scripture_id", "number"], unique = true)]
)
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "scripture_id") val scriptureId: String,
    val number: Int,
    @ColumnInfo(name = "title_sanskrit") val titleSanskrit: String,
    @ColumnInfo(name = "title_english") val titleEnglish: String,
    val theme: String
)

@Entity(
    tableName = "verses",
    foreignKeys = [
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapter_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("chapter_id"), Index(value = ["chapter_id", "verse_number"], unique = true)]
)
data class VerseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "chapter_id") val chapterId: Long,
    @ColumnInfo(name = "verse_number") val verseNumber: Int,
    val sanskrit: String,
    val transliteration: String
)

@Entity(
    tableName = "translations",
    foreignKeys = [
        ForeignKey(
            entity = VerseEntity::class,
            parentColumns = ["id"],
            childColumns = ["verse_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("verse_id"), Index(value = ["verse_id", "language_code"], unique = true)]
)
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "verse_id") val verseId: Long,
    @ColumnInfo(name = "language_code") val languageCode: String,
    @ColumnInfo(name = "simple_meaning") val simpleMeaning: String,
    @ColumnInfo(name = "philosophical_note") val philosophicalNote: String
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey @ColumnInfo(name = "verse_id") val verseId: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey @ColumnInfo(name = "scripture_id") val scriptureId: String,
    @ColumnInfo(name = "chapter_number") val chapterNumber: Int,
    @ColumnInfo(name = "verse_number") val verseNumber: Int,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

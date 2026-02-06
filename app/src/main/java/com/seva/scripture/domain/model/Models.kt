package com.seva.scripture.domain.model

data class ChapterSummary(
    val chapterNumber: Int,
    val titleSanskrit: String,
    val titleEnglish: String,
    val theme: String,
    val verseCount: Int
)

data class VerseSummary(
    val chapterNumber: Int,
    val verseNumber: Int,
    val sanskrit: String,
    val transliteration: String
)

data class VerseDetail(
    val chapterNumber: Int,
    val verseNumber: Int,
    val sanskrit: String,
    val transliteration: String,
    val simpleMeaning: String,
    val philosophicalNote: String,
    val languageCode: String,
    val bookmarked: Boolean
)

data class AppSettings(
    val languageCode: String = "en",
    val fontScale: Float = 1f,
    val darkMode: Boolean = false,
    val transliterationVisible: Boolean = true,
    val philosophicalVisible: Boolean = true,
    val dailyNotificationEnabled: Boolean = true
)

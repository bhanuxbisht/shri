package com.seva.scripture.data.local.dao

data class ChapterSummaryQuery(
    val chapterNumber: Int,
    val titleSanskrit: String,
    val titleEnglish: String,
    val theme: String,
    val verseCount: Int
)

data class VerseSummaryQuery(
    val chapterNumber: Int,
    val verseNumber: Int,
    val sanskrit: String,
    val transliteration: String
)

data class VerseDetailQuery(
    val chapterNumber: Int,
    val verseNumber: Int,
    val sanskrit: String,
    val transliteration: String,
    val simpleMeaning: String,
    val philosophicalNote: String,
    val languageCode: String,
    val bookmarked: Int
)

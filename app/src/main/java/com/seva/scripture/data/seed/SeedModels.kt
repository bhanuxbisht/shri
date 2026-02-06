package com.seva.scripture.data.seed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScriptureSeed(
    val scripture: ScriptureSeedInfo,
    val chapters: List<ChapterSeed>
)

@Serializable
data class ScriptureSeedInfo(
    val id: String,
    val name: String,
    val tradition: String,
    val description: String
)

@Serializable
data class ChapterSeed(
    val number: Int,
    @SerialName("title_sanskrit") val titleSanskrit: String,
    @SerialName("title_english") val titleEnglish: String,
    val theme: String,
    val verses: List<VerseSeed>
)

@Serializable
data class VerseSeed(
    val number: Int,
    val sanskrit: String,
    val transliteration: String,
    val translations: List<TranslationSeed>
)

@Serializable
data class TranslationSeed(
    @SerialName("language_code") val languageCode: String,
    @SerialName("simple_meaning") val simpleMeaning: String,
    @SerialName("philosophical_note") val philosophicalNote: String
)

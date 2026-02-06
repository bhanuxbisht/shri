# Bhagavad Gita Seva (Android)

A calm, offline-first scripture experience built with service-first intent.

## What is implemented

- Jetpack Compose UI for:
  - Splash
  - Chapter list (18 chapters)
  - Chapter verse list
  - Verse detail with Sanskrit, transliteration toggle behavior, meaning, philosophical note
  - Search
  - Bookmarks
  - Settings
- Offline-first local storage using Room.
- DataStore-backed app settings:
  - Language (`en` / `hi`)
  - Font scale
  - Dark mode
  - Transliteration visibility
  - Philosophical note visibility
  - Daily shloka notification toggle
- Daily shloka notification worker via WorkManager.
- Last-read position persistence.
- Text sharing for a verse.

## Tech stack

- `Kotlin`
- `Jetpack Compose` + `Material3`
- `Room`
- `DataStore Preferences`
- `WorkManager`
- `Kotlinx Serialization`

## Project structure

- `app/src/main/java/com/seva/scripture/MainActivity.kt`: Compose entry point and theme binding.
- `app/src/main/java/com/seva/scripture/ScriptureApp.kt`: app container setup and notification scheduling.
- `app/src/main/java/com/seva/scripture/ui/navigation/ScriptureNavHost.kt`: app screens and navigation flow.
- `app/src/main/java/com/seva/scripture/ui/viewmodel/ScriptureViewModel.kt`: UI state orchestration.
- `app/src/main/java/com/seva/scripture/data/local`: Room DB, entities, DAOs.
- `app/src/main/java/com/seva/scripture/data/repository`: offline repository + settings repository.
- `app/src/main/java/com/seva/scripture/data/seed`: serialization models for scripture seed.
- `app/src/main/assets/data/gita/gita_seed.json`: initial scripture content.

## Database schema

Core tables are generic, not Gita-hardcoded:

- `scriptures`
- `chapters`
- `verses`
- `translations`
- `bookmarks`
- `reading_progress`

This allows adding Ramayana, Upanishads, and other collections by inserting a new `scripture` with chapters/verses/translations.

## Seed content status

- Chapter metadata for all 18 Bhagavad Gita chapters is included.
- Verse payload is intentionally seeded with initial authentic verses (sample) for launch scaffolding.
- The ingestion path supports full 700+ verse expansion without schema or UI rewrites.

## Extension plan (future scriptures)

1. Add new JSON seeds under `assets/data/<scripture_code>/`.
2. Insert new row in `scriptures` and chapter/verse/translation records.
3. Add scripture selector to home and route flows by `scriptureCode`.
4. Add optional media tables for chanting/audio streams.
5. Add language packs by expanding `translations.language_code`.

## Notes

- Notification permission (`POST_NOTIFICATIONS`) is declared for Android 13+.
- Current share flow provides text sharing.
- Share-as-image and full canonical 700+ verse dataset can be added next using this base architecture.

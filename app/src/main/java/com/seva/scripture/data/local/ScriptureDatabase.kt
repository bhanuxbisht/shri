package com.seva.scripture.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.seva.scripture.data.local.dao.ScriptureDao
import com.seva.scripture.data.local.entity.BookmarkEntity
import com.seva.scripture.data.local.entity.ChapterEntity
import com.seva.scripture.data.local.entity.ReadingProgressEntity
import com.seva.scripture.data.local.entity.ScriptureEntity
import com.seva.scripture.data.local.entity.TranslationEntity
import com.seva.scripture.data.local.entity.VerseEntity

@Database(
    entities = [
        ScriptureEntity::class,
        ChapterEntity::class,
        VerseEntity::class,
        TranslationEntity::class,
        BookmarkEntity::class,
        ReadingProgressEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class ScriptureDatabase : RoomDatabase() {
    abstract fun scriptureDao(): ScriptureDao

    companion object {
        @Volatile
        private var INSTANCE: ScriptureDatabase? = null

        fun getInstance(context: Context): ScriptureDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ScriptureDatabase::class.java,
                    "scripture-db"
                )
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
        }
    }
}

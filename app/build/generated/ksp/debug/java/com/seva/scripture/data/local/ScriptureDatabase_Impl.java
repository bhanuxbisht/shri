package com.seva.scripture.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.seva.scripture.data.local.dao.ScriptureDao;
import com.seva.scripture.data.local.dao.ScriptureDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ScriptureDatabase_Impl extends ScriptureDatabase {
  private volatile ScriptureDao _scriptureDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(10) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `scriptures` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `tradition` TEXT NOT NULL, `description` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `chapters` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `scripture_id` TEXT NOT NULL, `number` INTEGER NOT NULL, `title_sanskrit` TEXT NOT NULL, `title_english` TEXT NOT NULL, `theme` TEXT NOT NULL, FOREIGN KEY(`scripture_id`) REFERENCES `scriptures`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_chapters_scripture_id` ON `chapters` (`scripture_id`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_chapters_scripture_id_number` ON `chapters` (`scripture_id`, `number`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `verses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `chapter_id` INTEGER NOT NULL, `verse_number` INTEGER NOT NULL, `sanskrit` TEXT NOT NULL, `transliteration` TEXT NOT NULL, FOREIGN KEY(`chapter_id`) REFERENCES `chapters`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_verses_chapter_id` ON `verses` (`chapter_id`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_verses_chapter_id_verse_number` ON `verses` (`chapter_id`, `verse_number`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `translations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `verse_id` INTEGER NOT NULL, `language_code` TEXT NOT NULL, `simple_meaning` TEXT NOT NULL, `philosophical_note` TEXT NOT NULL, FOREIGN KEY(`verse_id`) REFERENCES `verses`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_translations_verse_id` ON `translations` (`verse_id`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_translations_verse_id_language_code` ON `translations` (`verse_id`, `language_code`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bookmarks` (`verse_id` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, PRIMARY KEY(`verse_id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reading_progress` (`scripture_id` TEXT NOT NULL, `chapter_number` INTEGER NOT NULL, `verse_number` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`scripture_id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '5a6f0cbbf26d2b92f80e16cdd9ee3370')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `scriptures`");
        db.execSQL("DROP TABLE IF EXISTS `chapters`");
        db.execSQL("DROP TABLE IF EXISTS `verses`");
        db.execSQL("DROP TABLE IF EXISTS `translations`");
        db.execSQL("DROP TABLE IF EXISTS `bookmarks`");
        db.execSQL("DROP TABLE IF EXISTS `reading_progress`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsScriptures = new HashMap<String, TableInfo.Column>(4);
        _columnsScriptures.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScriptures.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScriptures.put("tradition", new TableInfo.Column("tradition", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScriptures.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScriptures = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesScriptures = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScriptures = new TableInfo("scriptures", _columnsScriptures, _foreignKeysScriptures, _indicesScriptures);
        final TableInfo _existingScriptures = TableInfo.read(db, "scriptures");
        if (!_infoScriptures.equals(_existingScriptures)) {
          return new RoomOpenHelper.ValidationResult(false, "scriptures(com.seva.scripture.data.local.entity.ScriptureEntity).\n"
                  + " Expected:\n" + _infoScriptures + "\n"
                  + " Found:\n" + _existingScriptures);
        }
        final HashMap<String, TableInfo.Column> _columnsChapters = new HashMap<String, TableInfo.Column>(6);
        _columnsChapters.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChapters.put("scripture_id", new TableInfo.Column("scripture_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChapters.put("number", new TableInfo.Column("number", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChapters.put("title_sanskrit", new TableInfo.Column("title_sanskrit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChapters.put("title_english", new TableInfo.Column("title_english", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChapters.put("theme", new TableInfo.Column("theme", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChapters = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysChapters.add(new TableInfo.ForeignKey("scriptures", "CASCADE", "NO ACTION", Arrays.asList("scripture_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesChapters = new HashSet<TableInfo.Index>(2);
        _indicesChapters.add(new TableInfo.Index("index_chapters_scripture_id", false, Arrays.asList("scripture_id"), Arrays.asList("ASC")));
        _indicesChapters.add(new TableInfo.Index("index_chapters_scripture_id_number", true, Arrays.asList("scripture_id", "number"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoChapters = new TableInfo("chapters", _columnsChapters, _foreignKeysChapters, _indicesChapters);
        final TableInfo _existingChapters = TableInfo.read(db, "chapters");
        if (!_infoChapters.equals(_existingChapters)) {
          return new RoomOpenHelper.ValidationResult(false, "chapters(com.seva.scripture.data.local.entity.ChapterEntity).\n"
                  + " Expected:\n" + _infoChapters + "\n"
                  + " Found:\n" + _existingChapters);
        }
        final HashMap<String, TableInfo.Column> _columnsVerses = new HashMap<String, TableInfo.Column>(5);
        _columnsVerses.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVerses.put("chapter_id", new TableInfo.Column("chapter_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVerses.put("verse_number", new TableInfo.Column("verse_number", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVerses.put("sanskrit", new TableInfo.Column("sanskrit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVerses.put("transliteration", new TableInfo.Column("transliteration", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVerses = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysVerses.add(new TableInfo.ForeignKey("chapters", "CASCADE", "NO ACTION", Arrays.asList("chapter_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesVerses = new HashSet<TableInfo.Index>(2);
        _indicesVerses.add(new TableInfo.Index("index_verses_chapter_id", false, Arrays.asList("chapter_id"), Arrays.asList("ASC")));
        _indicesVerses.add(new TableInfo.Index("index_verses_chapter_id_verse_number", true, Arrays.asList("chapter_id", "verse_number"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoVerses = new TableInfo("verses", _columnsVerses, _foreignKeysVerses, _indicesVerses);
        final TableInfo _existingVerses = TableInfo.read(db, "verses");
        if (!_infoVerses.equals(_existingVerses)) {
          return new RoomOpenHelper.ValidationResult(false, "verses(com.seva.scripture.data.local.entity.VerseEntity).\n"
                  + " Expected:\n" + _infoVerses + "\n"
                  + " Found:\n" + _existingVerses);
        }
        final HashMap<String, TableInfo.Column> _columnsTranslations = new HashMap<String, TableInfo.Column>(5);
        _columnsTranslations.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTranslations.put("verse_id", new TableInfo.Column("verse_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTranslations.put("language_code", new TableInfo.Column("language_code", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTranslations.put("simple_meaning", new TableInfo.Column("simple_meaning", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTranslations.put("philosophical_note", new TableInfo.Column("philosophical_note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTranslations = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysTranslations.add(new TableInfo.ForeignKey("verses", "CASCADE", "NO ACTION", Arrays.asList("verse_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTranslations = new HashSet<TableInfo.Index>(2);
        _indicesTranslations.add(new TableInfo.Index("index_translations_verse_id", false, Arrays.asList("verse_id"), Arrays.asList("ASC")));
        _indicesTranslations.add(new TableInfo.Index("index_translations_verse_id_language_code", true, Arrays.asList("verse_id", "language_code"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoTranslations = new TableInfo("translations", _columnsTranslations, _foreignKeysTranslations, _indicesTranslations);
        final TableInfo _existingTranslations = TableInfo.read(db, "translations");
        if (!_infoTranslations.equals(_existingTranslations)) {
          return new RoomOpenHelper.ValidationResult(false, "translations(com.seva.scripture.data.local.entity.TranslationEntity).\n"
                  + " Expected:\n" + _infoTranslations + "\n"
                  + " Found:\n" + _existingTranslations);
        }
        final HashMap<String, TableInfo.Column> _columnsBookmarks = new HashMap<String, TableInfo.Column>(2);
        _columnsBookmarks.put("verse_id", new TableInfo.Column("verse_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBookmarks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBookmarks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBookmarks = new TableInfo("bookmarks", _columnsBookmarks, _foreignKeysBookmarks, _indicesBookmarks);
        final TableInfo _existingBookmarks = TableInfo.read(db, "bookmarks");
        if (!_infoBookmarks.equals(_existingBookmarks)) {
          return new RoomOpenHelper.ValidationResult(false, "bookmarks(com.seva.scripture.data.local.entity.BookmarkEntity).\n"
                  + " Expected:\n" + _infoBookmarks + "\n"
                  + " Found:\n" + _existingBookmarks);
        }
        final HashMap<String, TableInfo.Column> _columnsReadingProgress = new HashMap<String, TableInfo.Column>(4);
        _columnsReadingProgress.put("scripture_id", new TableInfo.Column("scripture_id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("chapter_number", new TableInfo.Column("chapter_number", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("verse_number", new TableInfo.Column("verse_number", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReadingProgress = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReadingProgress = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReadingProgress = new TableInfo("reading_progress", _columnsReadingProgress, _foreignKeysReadingProgress, _indicesReadingProgress);
        final TableInfo _existingReadingProgress = TableInfo.read(db, "reading_progress");
        if (!_infoReadingProgress.equals(_existingReadingProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "reading_progress(com.seva.scripture.data.local.entity.ReadingProgressEntity).\n"
                  + " Expected:\n" + _infoReadingProgress + "\n"
                  + " Found:\n" + _existingReadingProgress);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "5a6f0cbbf26d2b92f80e16cdd9ee3370", "13c1210a2176af04289039f0b7322e7b");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "scriptures","chapters","verses","translations","bookmarks","reading_progress");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `scriptures`");
      _db.execSQL("DELETE FROM `chapters`");
      _db.execSQL("DELETE FROM `verses`");
      _db.execSQL("DELETE FROM `translations`");
      _db.execSQL("DELETE FROM `bookmarks`");
      _db.execSQL("DELETE FROM `reading_progress`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ScriptureDao.class, ScriptureDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ScriptureDao scriptureDao() {
    if (_scriptureDao != null) {
      return _scriptureDao;
    } else {
      synchronized(this) {
        if(_scriptureDao == null) {
          _scriptureDao = new ScriptureDao_Impl(this);
        }
        return _scriptureDao;
      }
    }
  }
}

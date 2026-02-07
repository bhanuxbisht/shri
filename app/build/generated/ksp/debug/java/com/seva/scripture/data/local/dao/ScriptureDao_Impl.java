package com.seva.scripture.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.seva.scripture.data.local.entity.BookmarkEntity;
import com.seva.scripture.data.local.entity.ChapterEntity;
import com.seva.scripture.data.local.entity.ReadingProgressEntity;
import com.seva.scripture.data.local.entity.ScriptureEntity;
import com.seva.scripture.data.local.entity.TranslationEntity;
import com.seva.scripture.data.local.entity.VerseEntity;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ScriptureDao_Impl implements ScriptureDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ScriptureEntity> __insertionAdapterOfScriptureEntity;

  private final EntityInsertionAdapter<ChapterEntity> __insertionAdapterOfChapterEntity;

  private final EntityInsertionAdapter<VerseEntity> __insertionAdapterOfVerseEntity;

  private final EntityInsertionAdapter<TranslationEntity> __insertionAdapterOfTranslationEntity;

  private final EntityInsertionAdapter<BookmarkEntity> __insertionAdapterOfBookmarkEntity;

  private final EntityInsertionAdapter<ReadingProgressEntity> __insertionAdapterOfReadingProgressEntity;

  private final SharedSQLiteStatement __preparedStmtOfRemoveBookmark;

  public ScriptureDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfScriptureEntity = new EntityInsertionAdapter<ScriptureEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `scriptures` (`id`,`name`,`tradition`,`description`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScriptureEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getTradition());
        statement.bindString(4, entity.getDescription());
      }
    };
    this.__insertionAdapterOfChapterEntity = new EntityInsertionAdapter<ChapterEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `chapters` (`id`,`scripture_id`,`number`,`title_sanskrit`,`title_english`,`theme`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChapterEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getScriptureId());
        statement.bindLong(3, entity.getNumber());
        statement.bindString(4, entity.getTitleSanskrit());
        statement.bindString(5, entity.getTitleEnglish());
        statement.bindString(6, entity.getTheme());
      }
    };
    this.__insertionAdapterOfVerseEntity = new EntityInsertionAdapter<VerseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `verses` (`id`,`chapter_id`,`verse_number`,`sanskrit`,`transliteration`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VerseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getChapterId());
        statement.bindLong(3, entity.getVerseNumber());
        statement.bindString(4, entity.getSanskrit());
        statement.bindString(5, entity.getTransliteration());
      }
    };
    this.__insertionAdapterOfTranslationEntity = new EntityInsertionAdapter<TranslationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `translations` (`id`,`verse_id`,`language_code`,`simple_meaning`,`philosophical_note`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TranslationEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVerseId());
        statement.bindString(3, entity.getLanguageCode());
        statement.bindString(4, entity.getSimpleMeaning());
        statement.bindString(5, entity.getPhilosophicalNote());
      }
    };
    this.__insertionAdapterOfBookmarkEntity = new EntityInsertionAdapter<BookmarkEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `bookmarks` (`verse_id`,`created_at`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookmarkEntity entity) {
        statement.bindLong(1, entity.getVerseId());
        statement.bindLong(2, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfReadingProgressEntity = new EntityInsertionAdapter<ReadingProgressEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reading_progress` (`scripture_id`,`chapter_number`,`verse_number`,`updated_at`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReadingProgressEntity entity) {
        statement.bindString(1, entity.getScriptureId());
        statement.bindLong(2, entity.getChapterNumber());
        statement.bindLong(3, entity.getVerseNumber());
        statement.bindLong(4, entity.getUpdatedAt());
      }
    };
    this.__preparedStmtOfRemoveBookmark = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmarks WHERE verse_id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertScripture(final ScriptureEntity scripture,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfScriptureEntity.insert(scripture);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertChapters(final List<ChapterEntity> chapters,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfChapterEntity.insert(chapters);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertVerses(final List<VerseEntity> verses,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfVerseEntity.insertAndReturnIdsList(verses);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertTranslations(final List<TranslationEntity> translations,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTranslationEntity.insert(translations);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertBookmark(final BookmarkEntity bookmark,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBookmarkEntity.insert(bookmark);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertProgress(final ReadingProgressEntity progress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReadingProgressEntity.insert(progress);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object removeBookmark(final long verseId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveBookmark.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, verseId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfRemoveBookmark.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ScriptureEntity>> observeScriptures() {
    final String _sql = "SELECT * FROM scriptures";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"scriptures"}, new Callable<List<ScriptureEntity>>() {
      @Override
      @NonNull
      public List<ScriptureEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTradition = CursorUtil.getColumnIndexOrThrow(_cursor, "tradition");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final List<ScriptureEntity> _result = new ArrayList<ScriptureEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScriptureEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpTradition;
            _tmpTradition = _cursor.getString(_cursorIndexOfTradition);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            _item = new ScriptureEntity(_tmpId,_tmpName,_tmpTradition,_tmpDescription);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object hasScripture(final String scriptureId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM scriptures WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, scriptureId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object isBookmarked(final long verseId, final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM bookmarks WHERE verse_id = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, verseId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp != 0;
          } else {
            _result = false;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ChapterSummaryQuery>> observeChapters(final String scriptureCode) {
    final String _sql = "\n"
            + "        SELECT c.number as chapterNumber, c.title_sanskrit as titleSanskrit, c.title_english as titleEnglish,\n"
            + "        c.theme as theme, COUNT(v.id) as verseCount\n"
            + "        FROM chapters c\n"
            + "        LEFT JOIN verses v ON v.chapter_id = c.id\n"
            + "        WHERE c.scripture_id = ?\n"
            + "        GROUP BY c.id\n"
            + "        ORDER BY c.number\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, scriptureCode);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"chapters",
        "verses"}, new Callable<List<ChapterSummaryQuery>>() {
      @Override
      @NonNull
      public List<ChapterSummaryQuery> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfChapterNumber = 0;
          final int _cursorIndexOfTitleSanskrit = 1;
          final int _cursorIndexOfTitleEnglish = 2;
          final int _cursorIndexOfTheme = 3;
          final int _cursorIndexOfVerseCount = 4;
          final List<ChapterSummaryQuery> _result = new ArrayList<ChapterSummaryQuery>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChapterSummaryQuery _item;
            final int _tmpChapterNumber;
            _tmpChapterNumber = _cursor.getInt(_cursorIndexOfChapterNumber);
            final String _tmpTitleSanskrit;
            _tmpTitleSanskrit = _cursor.getString(_cursorIndexOfTitleSanskrit);
            final String _tmpTitleEnglish;
            _tmpTitleEnglish = _cursor.getString(_cursorIndexOfTitleEnglish);
            final String _tmpTheme;
            _tmpTheme = _cursor.getString(_cursorIndexOfTheme);
            final int _tmpVerseCount;
            _tmpVerseCount = _cursor.getInt(_cursorIndexOfVerseCount);
            _item = new ChapterSummaryQuery(_tmpChapterNumber,_tmpTitleSanskrit,_tmpTitleEnglish,_tmpTheme,_tmpVerseCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<VerseSummaryQuery>> observeVerses(final String scriptureCode,
      final int chapterNumber) {
    final String _sql = "\n"
            + "        SELECT c.number as chapterNumber, v.verse_number as verseNumber, v.sanskrit, v.transliteration\n"
            + "        FROM verses v\n"
            + "        INNER JOIN chapters c ON c.id = v.chapter_id\n"
            + "        WHERE c.scripture_id = ? AND c.number = ?\n"
            + "        ORDER BY v.verse_number\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, scriptureCode);
    _argIndex = 2;
    _statement.bindLong(_argIndex, chapterNumber);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"verses",
        "chapters"}, new Callable<List<VerseSummaryQuery>>() {
      @Override
      @NonNull
      public List<VerseSummaryQuery> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfChapterNumber = 0;
          final int _cursorIndexOfVerseNumber = 1;
          final int _cursorIndexOfSanskrit = 2;
          final int _cursorIndexOfTransliteration = 3;
          final List<VerseSummaryQuery> _result = new ArrayList<VerseSummaryQuery>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VerseSummaryQuery _item;
            final int _tmpChapterNumber;
            _tmpChapterNumber = _cursor.getInt(_cursorIndexOfChapterNumber);
            final int _tmpVerseNumber;
            _tmpVerseNumber = _cursor.getInt(_cursorIndexOfVerseNumber);
            final String _tmpSanskrit;
            _tmpSanskrit = _cursor.getString(_cursorIndexOfSanskrit);
            final String _tmpTransliteration;
            _tmpTransliteration = _cursor.getString(_cursorIndexOfTransliteration);
            _item = new VerseSummaryQuery(_tmpChapterNumber,_tmpVerseNumber,_tmpSanskrit,_tmpTransliteration);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<VerseDetailQuery> observeVerseDetail(final String scriptureCode,
      final int chapterNumber, final int verseNumber, final String languageCode) {
    final String _sql = "\n"
            + "        SELECT c.number as chapterNumber, v.verse_number as verseNumber, v.sanskrit, v.transliteration,\n"
            + "        t.simple_meaning as simpleMeaning, t.philosophical_note as philosophicalNote,\n"
            + "        t.language_code as languageCode,\n"
            + "        CASE WHEN b.verse_id IS NULL THEN 0 ELSE 1 END as bookmarked\n"
            + "        FROM verses v\n"
            + "        INNER JOIN chapters c ON c.id = v.chapter_id\n"
            + "        INNER JOIN translations t ON t.verse_id = v.id\n"
            + "        LEFT JOIN bookmarks b ON b.verse_id = v.id\n"
            + "        WHERE c.scripture_id = ?\n"
            + "        AND c.number = ?\n"
            + "        AND v.verse_number = ?\n"
            + "        AND t.language_code = ?\n"
            + "        LIMIT 1\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindString(_argIndex, scriptureCode);
    _argIndex = 2;
    _statement.bindLong(_argIndex, chapterNumber);
    _argIndex = 3;
    _statement.bindLong(_argIndex, verseNumber);
    _argIndex = 4;
    _statement.bindString(_argIndex, languageCode);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"verses", "chapters",
        "translations", "bookmarks"}, new Callable<VerseDetailQuery>() {
      @Override
      @Nullable
      public VerseDetailQuery call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfChapterNumber = 0;
          final int _cursorIndexOfVerseNumber = 1;
          final int _cursorIndexOfSanskrit = 2;
          final int _cursorIndexOfTransliteration = 3;
          final int _cursorIndexOfSimpleMeaning = 4;
          final int _cursorIndexOfPhilosophicalNote = 5;
          final int _cursorIndexOfLanguageCode = 6;
          final int _cursorIndexOfBookmarked = 7;
          final VerseDetailQuery _result;
          if (_cursor.moveToFirst()) {
            final int _tmpChapterNumber;
            _tmpChapterNumber = _cursor.getInt(_cursorIndexOfChapterNumber);
            final int _tmpVerseNumber;
            _tmpVerseNumber = _cursor.getInt(_cursorIndexOfVerseNumber);
            final String _tmpSanskrit;
            _tmpSanskrit = _cursor.getString(_cursorIndexOfSanskrit);
            final String _tmpTransliteration;
            _tmpTransliteration = _cursor.getString(_cursorIndexOfTransliteration);
            final String _tmpSimpleMeaning;
            _tmpSimpleMeaning = _cursor.getString(_cursorIndexOfSimpleMeaning);
            final String _tmpPhilosophicalNote;
            _tmpPhilosophicalNote = _cursor.getString(_cursorIndexOfPhilosophicalNote);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final int _tmpBookmarked;
            _tmpBookmarked = _cursor.getInt(_cursorIndexOfBookmarked);
            _result = new VerseDetailQuery(_tmpChapterNumber,_tmpVerseNumber,_tmpSanskrit,_tmpTransliteration,_tmpSimpleMeaning,_tmpPhilosophicalNote,_tmpLanguageCode,_tmpBookmarked);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<VerseSummaryQuery>> searchVerses(final String scriptureCode, final String query,
      final String languageCode) {
    final String _sql = "\n"
            + "        SELECT c.number as chapterNumber, v.verse_number as verseNumber, v.sanskrit, v.transliteration\n"
            + "        FROM verses v\n"
            + "        INNER JOIN chapters c ON c.id = v.chapter_id\n"
            + "        LEFT JOIN translations t ON t.verse_id = v.id\n"
            + "        WHERE c.scripture_id = ?\n"
            + "        AND (\n"
            + "          v.sanskrit LIKE '%' || ? || '%'\n"
            + "          OR v.transliteration LIKE '%' || ? || '%'\n"
            + "          OR t.simple_meaning LIKE '%' || ? || '%'\n"
            + "          OR (CAST(c.number AS TEXT) || ':' || CAST(v.verse_number AS TEXT)) LIKE '%' || ? || '%'\n"
            + "        )\n"
            + "        AND t.language_code = ?\n"
            + "        ORDER BY c.number, v.verse_number\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 6);
    int _argIndex = 1;
    _statement.bindString(_argIndex, scriptureCode);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    _argIndex = 3;
    _statement.bindString(_argIndex, query);
    _argIndex = 4;
    _statement.bindString(_argIndex, query);
    _argIndex = 5;
    _statement.bindString(_argIndex, query);
    _argIndex = 6;
    _statement.bindString(_argIndex, languageCode);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"verses", "chapters",
        "translations"}, new Callable<List<VerseSummaryQuery>>() {
      @Override
      @NonNull
      public List<VerseSummaryQuery> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfChapterNumber = 0;
          final int _cursorIndexOfVerseNumber = 1;
          final int _cursorIndexOfSanskrit = 2;
          final int _cursorIndexOfTransliteration = 3;
          final List<VerseSummaryQuery> _result = new ArrayList<VerseSummaryQuery>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VerseSummaryQuery _item;
            final int _tmpChapterNumber;
            _tmpChapterNumber = _cursor.getInt(_cursorIndexOfChapterNumber);
            final int _tmpVerseNumber;
            _tmpVerseNumber = _cursor.getInt(_cursorIndexOfVerseNumber);
            final String _tmpSanskrit;
            _tmpSanskrit = _cursor.getString(_cursorIndexOfSanskrit);
            final String _tmpTransliteration;
            _tmpTransliteration = _cursor.getString(_cursorIndexOfTransliteration);
            _item = new VerseSummaryQuery(_tmpChapterNumber,_tmpVerseNumber,_tmpSanskrit,_tmpTransliteration);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<VerseDetailQuery>> observeBookmarks(final String scriptureCode,
      final String languageCode) {
    final String _sql = "\n"
            + "        SELECT c.number as chapterNumber, v.verse_number as verseNumber, v.sanskrit, v.transliteration,\n"
            + "        t.simple_meaning as simpleMeaning, t.philosophical_note as philosophicalNote,\n"
            + "        t.language_code as languageCode,\n"
            + "        1 as bookmarked\n"
            + "        FROM bookmarks b\n"
            + "        INNER JOIN verses v ON v.id = b.verse_id\n"
            + "        INNER JOIN chapters c ON c.id = v.chapter_id\n"
            + "        INNER JOIN translations t ON t.verse_id = v.id\n"
            + "        WHERE c.scripture_id = ? AND t.language_code = ?\n"
            + "        ORDER BY b.created_at DESC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, scriptureCode);
    _argIndex = 2;
    _statement.bindString(_argIndex, languageCode);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bookmarks", "verses", "chapters",
        "translations"}, new Callable<List<VerseDetailQuery>>() {
      @Override
      @NonNull
      public List<VerseDetailQuery> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfChapterNumber = 0;
          final int _cursorIndexOfVerseNumber = 1;
          final int _cursorIndexOfSanskrit = 2;
          final int _cursorIndexOfTransliteration = 3;
          final int _cursorIndexOfSimpleMeaning = 4;
          final int _cursorIndexOfPhilosophicalNote = 5;
          final int _cursorIndexOfLanguageCode = 6;
          final int _cursorIndexOfBookmarked = 7;
          final List<VerseDetailQuery> _result = new ArrayList<VerseDetailQuery>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VerseDetailQuery _item;
            final int _tmpChapterNumber;
            _tmpChapterNumber = _cursor.getInt(_cursorIndexOfChapterNumber);
            final int _tmpVerseNumber;
            _tmpVerseNumber = _cursor.getInt(_cursorIndexOfVerseNumber);
            final String _tmpSanskrit;
            _tmpSanskrit = _cursor.getString(_cursorIndexOfSanskrit);
            final String _tmpTransliteration;
            _tmpTransliteration = _cursor.getString(_cursorIndexOfTransliteration);
            final String _tmpSimpleMeaning;
            _tmpSimpleMeaning = _cursor.getString(_cursorIndexOfSimpleMeaning);
            final String _tmpPhilosophicalNote;
            _tmpPhilosophicalNote = _cursor.getString(_cursorIndexOfPhilosophicalNote);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final int _tmpBookmarked;
            _tmpBookmarked = _cursor.getInt(_cursorIndexOfBookmarked);
            _item = new VerseDetailQuery(_tmpChapterNumber,_tmpVerseNumber,_tmpSanskrit,_tmpTransliteration,_tmpSimpleMeaning,_tmpPhilosophicalNote,_tmpLanguageCode,_tmpBookmarked);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getVerseId(final String scriptureCode, final int chapterNumber,
      final int verseNumber, final Continuation<? super Long> $completion) {
    final String _sql = "\n"
            + "        SELECT v.id\n"
            + "        FROM verses v\n"
            + "        INNER JOIN chapters c ON c.id = v.chapter_id\n"
            + "        WHERE c.scripture_id = ?\n"
            + "        AND c.number = ?\n"
            + "        AND v.verse_number = ?\n"
            + "        LIMIT 1\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, scriptureCode);
    _argIndex = 2;
    _statement.bindLong(_argIndex, chapterNumber);
    _argIndex = 3;
    _statement.bindLong(_argIndex, verseNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getLong(0);
            }
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getProgress(final String scriptureCode,
      final Continuation<? super ProgressQuery> $completion) {
    final String _sql = "SELECT chapter_number, verse_number FROM reading_progress WHERE scripture_id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, scriptureCode);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProgressQuery>() {
      @Override
      @Nullable
      public ProgressQuery call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfChapterNumber = 0;
          final int _cursorIndexOfVerseNumber = 1;
          final ProgressQuery _result;
          if (_cursor.moveToFirst()) {
            final int _tmpChapter_number;
            _tmpChapter_number = _cursor.getInt(_cursorIndexOfChapterNumber);
            final int _tmpVerse_number;
            _tmpVerse_number = _cursor.getInt(_cursorIndexOfVerseNumber);
            _result = new ProgressQuery(_tmpChapter_number,_tmpVerse_number);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object randomVerse(final String scriptureCode, final String languageCode,
      final Continuation<? super VerseDetailQuery> $completion) {
    final String _sql = "\n"
            + "        SELECT c.number as chapterNumber, v.verse_number as verseNumber, v.sanskrit, v.transliteration,\n"
            + "        t.simple_meaning as simpleMeaning, t.philosophical_note as philosophicalNote,\n"
            + "        t.language_code as languageCode,\n"
            + "        CASE WHEN b.verse_id IS NULL THEN 0 ELSE 1 END as bookmarked\n"
            + "        FROM verses v\n"
            + "        INNER JOIN chapters c ON c.id = v.chapter_id\n"
            + "        INNER JOIN translations t ON t.verse_id = v.id\n"
            + "        LEFT JOIN bookmarks b ON b.verse_id = v.id\n"
            + "        WHERE c.scripture_id = ? AND t.language_code = ?\n"
            + "        ORDER BY RANDOM()\n"
            + "        LIMIT 1\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, scriptureCode);
    _argIndex = 2;
    _statement.bindString(_argIndex, languageCode);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VerseDetailQuery>() {
      @Override
      @Nullable
      public VerseDetailQuery call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfChapterNumber = 0;
          final int _cursorIndexOfVerseNumber = 1;
          final int _cursorIndexOfSanskrit = 2;
          final int _cursorIndexOfTransliteration = 3;
          final int _cursorIndexOfSimpleMeaning = 4;
          final int _cursorIndexOfPhilosophicalNote = 5;
          final int _cursorIndexOfLanguageCode = 6;
          final int _cursorIndexOfBookmarked = 7;
          final VerseDetailQuery _result;
          if (_cursor.moveToFirst()) {
            final int _tmpChapterNumber;
            _tmpChapterNumber = _cursor.getInt(_cursorIndexOfChapterNumber);
            final int _tmpVerseNumber;
            _tmpVerseNumber = _cursor.getInt(_cursorIndexOfVerseNumber);
            final String _tmpSanskrit;
            _tmpSanskrit = _cursor.getString(_cursorIndexOfSanskrit);
            final String _tmpTransliteration;
            _tmpTransliteration = _cursor.getString(_cursorIndexOfTransliteration);
            final String _tmpSimpleMeaning;
            _tmpSimpleMeaning = _cursor.getString(_cursorIndexOfSimpleMeaning);
            final String _tmpPhilosophicalNote;
            _tmpPhilosophicalNote = _cursor.getString(_cursorIndexOfPhilosophicalNote);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final int _tmpBookmarked;
            _tmpBookmarked = _cursor.getInt(_cursorIndexOfBookmarked);
            _result = new VerseDetailQuery(_tmpChapterNumber,_tmpVerseNumber,_tmpSanskrit,_tmpTransliteration,_tmpSimpleMeaning,_tmpPhilosophicalNote,_tmpLanguageCode,_tmpBookmarked);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object verseCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM verses";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object chapterIds(final String scriptureCode,
      final Continuation<? super List<ChapterIdQuery>> $completion) {
    final String _sql = "SELECT id, number FROM chapters WHERE scripture_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, scriptureCode);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ChapterIdQuery>>() {
      @Override
      @NonNull
      public List<ChapterIdQuery> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfNumber = 1;
          final List<ChapterIdQuery> _result = new ArrayList<ChapterIdQuery>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChapterIdQuery _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpNumber;
            _tmpNumber = _cursor.getInt(_cursorIndexOfNumber);
            _item = new ChapterIdQuery(_tmpId,_tmpNumber);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

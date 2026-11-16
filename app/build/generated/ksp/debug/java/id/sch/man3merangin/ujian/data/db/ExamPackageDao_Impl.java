package id.sch.man3merangin.ujian.data.db;

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
import java.lang.Class;
import java.lang.Exception;
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
public final class ExamPackageDao_Impl implements ExamPackageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ExamPackageEntity> __insertionAdapterOfExamPackageEntity;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public ExamPackageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExamPackageEntity = new EntityInsertionAdapter<ExamPackageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `exam_packages` (`examId`,`title`,`startAt`,`endAt`,`envelopeJson`,`version`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExamPackageEntity entity) {
        statement.bindString(1, entity.getExamId());
        statement.bindString(2, entity.getTitle());
        statement.bindLong(3, entity.getStartAt());
        statement.bindLong(4, entity.getEndAt());
        statement.bindString(5, entity.getEnvelopeJson());
        statement.bindLong(6, entity.getVersion());
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM exam_packages WHERE examId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ExamPackageEntity pkg, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfExamPackageEntity.insert(pkg);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final String examId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, examId);
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
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String examId,
      final Continuation<? super ExamPackageEntity> $completion) {
    final String _sql = "SELECT * FROM exam_packages WHERE examId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, examId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ExamPackageEntity>() {
      @Override
      @Nullable
      public ExamPackageEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfStartAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startAt");
          final int _cursorIndexOfEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endAt");
          final int _cursorIndexOfEnvelopeJson = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeJson");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final ExamPackageEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpExamId;
            _tmpExamId = _cursor.getString(_cursorIndexOfExamId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final long _tmpStartAt;
            _tmpStartAt = _cursor.getLong(_cursorIndexOfStartAt);
            final long _tmpEndAt;
            _tmpEndAt = _cursor.getLong(_cursorIndexOfEndAt);
            final String _tmpEnvelopeJson;
            _tmpEnvelopeJson = _cursor.getString(_cursorIndexOfEnvelopeJson);
            final int _tmpVersion;
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion);
            _result = new ExamPackageEntity(_tmpExamId,_tmpTitle,_tmpStartAt,_tmpEndAt,_tmpEnvelopeJson,_tmpVersion);
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
  public Flow<List<ExamPackageEntity>> getAllFlow() {
    final String _sql = "SELECT * FROM exam_packages ORDER BY startAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exam_packages"}, new Callable<List<ExamPackageEntity>>() {
      @Override
      @NonNull
      public List<ExamPackageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfStartAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startAt");
          final int _cursorIndexOfEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endAt");
          final int _cursorIndexOfEnvelopeJson = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeJson");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final List<ExamPackageEntity> _result = new ArrayList<ExamPackageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExamPackageEntity _item;
            final String _tmpExamId;
            _tmpExamId = _cursor.getString(_cursorIndexOfExamId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final long _tmpStartAt;
            _tmpStartAt = _cursor.getLong(_cursorIndexOfStartAt);
            final long _tmpEndAt;
            _tmpEndAt = _cursor.getLong(_cursorIndexOfEndAt);
            final String _tmpEnvelopeJson;
            _tmpEnvelopeJson = _cursor.getString(_cursorIndexOfEnvelopeJson);
            final int _tmpVersion;
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion);
            _item = new ExamPackageEntity(_tmpExamId,_tmpTitle,_tmpStartAt,_tmpEndAt,_tmpEnvelopeJson,_tmpVersion);
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
  public Object getAll(final Continuation<? super List<ExamPackageEntity>> $completion) {
    final String _sql = "SELECT * FROM exam_packages ORDER BY startAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ExamPackageEntity>>() {
      @Override
      @NonNull
      public List<ExamPackageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfStartAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startAt");
          final int _cursorIndexOfEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endAt");
          final int _cursorIndexOfEnvelopeJson = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeJson");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final List<ExamPackageEntity> _result = new ArrayList<ExamPackageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExamPackageEntity _item;
            final String _tmpExamId;
            _tmpExamId = _cursor.getString(_cursorIndexOfExamId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final long _tmpStartAt;
            _tmpStartAt = _cursor.getLong(_cursorIndexOfStartAt);
            final long _tmpEndAt;
            _tmpEndAt = _cursor.getLong(_cursorIndexOfEndAt);
            final String _tmpEnvelopeJson;
            _tmpEnvelopeJson = _cursor.getString(_cursorIndexOfEnvelopeJson);
            final int _tmpVersion;
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion);
            _item = new ExamPackageEntity(_tmpExamId,_tmpTitle,_tmpStartAt,_tmpEndAt,_tmpEnvelopeJson,_tmpVersion);
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

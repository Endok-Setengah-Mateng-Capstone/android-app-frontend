package id.sch.man3merangin.ujian.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ExamSessionDao_Impl implements ExamSessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ExamSessionEntity> __insertionAdapterOfExamSessionEntity;

  public ExamSessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExamSessionEntity = new EntityInsertionAdapter<ExamSessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `exam_sessions` (`examId`,`nisn`,`remainingMs`,`snapshotElapsed`,`clockUnverified`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExamSessionEntity entity) {
        statement.bindString(1, entity.getExamId());
        statement.bindString(2, entity.getNisn());
        statement.bindLong(3, entity.getRemainingMs());
        statement.bindLong(4, entity.getSnapshotElapsed());
        final int _tmp = entity.getClockUnverified() ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
  }

  @Override
  public Object upsert(final ExamSessionEntity session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfExamSessionEntity.insert(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object get(final String examId, final String nisn,
      final Continuation<? super ExamSessionEntity> $completion) {
    final String _sql = "SELECT * FROM exam_sessions WHERE examId = ? AND nisn = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, examId);
    _argIndex = 2;
    _statement.bindString(_argIndex, nisn);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ExamSessionEntity>() {
      @Override
      @Nullable
      public ExamSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfNisn = CursorUtil.getColumnIndexOrThrow(_cursor, "nisn");
          final int _cursorIndexOfRemainingMs = CursorUtil.getColumnIndexOrThrow(_cursor, "remainingMs");
          final int _cursorIndexOfSnapshotElapsed = CursorUtil.getColumnIndexOrThrow(_cursor, "snapshotElapsed");
          final int _cursorIndexOfClockUnverified = CursorUtil.getColumnIndexOrThrow(_cursor, "clockUnverified");
          final ExamSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpExamId;
            _tmpExamId = _cursor.getString(_cursorIndexOfExamId);
            final String _tmpNisn;
            _tmpNisn = _cursor.getString(_cursorIndexOfNisn);
            final long _tmpRemainingMs;
            _tmpRemainingMs = _cursor.getLong(_cursorIndexOfRemainingMs);
            final long _tmpSnapshotElapsed;
            _tmpSnapshotElapsed = _cursor.getLong(_cursorIndexOfSnapshotElapsed);
            final boolean _tmpClockUnverified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfClockUnverified);
            _tmpClockUnverified = _tmp != 0;
            _result = new ExamSessionEntity(_tmpExamId,_tmpNisn,_tmpRemainingMs,_tmpSnapshotElapsed,_tmpClockUnverified);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

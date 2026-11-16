package id.sch.man3merangin.ujian.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ViolationDao_Impl implements ViolationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ViolationEntity> __insertionAdapterOfViolationEntity;

  public ViolationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfViolationEntity = new EntityInsertionAdapter<ViolationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `violations` (`id`,`examId`,`nisn`,`category`,`startedAt`,`durationMs`,`questionNo`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ViolationEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getExamId());
        statement.bindString(3, entity.getNisn());
        statement.bindString(4, entity.getCategory());
        statement.bindLong(5, entity.getStartedAt());
        statement.bindLong(6, entity.getDurationMs());
        statement.bindLong(7, entity.getQuestionNo());
      }
    };
  }

  @Override
  public Object insert(final ViolationEntity violation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfViolationEntity.insert(violation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object forUser(final String examId, final String nisn,
      final Continuation<? super List<ViolationEntity>> $completion) {
    final String _sql = "SELECT * FROM violations WHERE examId = ? AND nisn = ? ORDER BY startedAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, examId);
    _argIndex = 2;
    _statement.bindString(_argIndex, nisn);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ViolationEntity>>() {
      @Override
      @NonNull
      public List<ViolationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfNisn = CursorUtil.getColumnIndexOrThrow(_cursor, "nisn");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfQuestionNo = CursorUtil.getColumnIndexOrThrow(_cursor, "questionNo");
          final List<ViolationEntity> _result = new ArrayList<ViolationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ViolationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpExamId;
            _tmpExamId = _cursor.getString(_cursorIndexOfExamId);
            final String _tmpNisn;
            _tmpNisn = _cursor.getString(_cursorIndexOfNisn);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final int _tmpQuestionNo;
            _tmpQuestionNo = _cursor.getInt(_cursorIndexOfQuestionNo);
            _item = new ViolationEntity(_tmpId,_tmpExamId,_tmpNisn,_tmpCategory,_tmpStartedAt,_tmpDurationMs,_tmpQuestionNo);
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

package id.sch.man3merangin.ujian.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AnswerDao_Impl implements AnswerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AnswerEntity> __insertionAdapterOfAnswerEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearForUser;

  public AnswerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAnswerEntity = new EntityInsertionAdapter<AnswerEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `answers` (`examId`,`nisn`,`questionId`,`selected`,`answeredAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AnswerEntity entity) {
        statement.bindString(1, entity.getExamId());
        statement.bindString(2, entity.getNisn());
        statement.bindString(3, entity.getQuestionId());
        statement.bindString(4, entity.getSelected());
        statement.bindLong(5, entity.getAnsweredAt());
      }
    };
    this.__preparedStmtOfClearForUser = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM answers WHERE examId = ? AND nisn = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final AnswerEntity answer, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAnswerEntity.insert(answer);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearForUser(final String examId, final String nisn,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearForUser.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, examId);
        _argIndex = 2;
        _stmt.bindString(_argIndex, nisn);
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
          __preparedStmtOfClearForUser.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object forUser(final String examId, final String nisn,
      final Continuation<? super List<AnswerEntity>> $completion) {
    final String _sql = "SELECT * FROM answers WHERE examId = ? AND nisn = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, examId);
    _argIndex = 2;
    _statement.bindString(_argIndex, nisn);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AnswerEntity>>() {
      @Override
      @NonNull
      public List<AnswerEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfExamId = CursorUtil.getColumnIndexOrThrow(_cursor, "examId");
          final int _cursorIndexOfNisn = CursorUtil.getColumnIndexOrThrow(_cursor, "nisn");
          final int _cursorIndexOfQuestionId = CursorUtil.getColumnIndexOrThrow(_cursor, "questionId");
          final int _cursorIndexOfSelected = CursorUtil.getColumnIndexOrThrow(_cursor, "selected");
          final int _cursorIndexOfAnsweredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "answeredAt");
          final List<AnswerEntity> _result = new ArrayList<AnswerEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AnswerEntity _item;
            final String _tmpExamId;
            _tmpExamId = _cursor.getString(_cursorIndexOfExamId);
            final String _tmpNisn;
            _tmpNisn = _cursor.getString(_cursorIndexOfNisn);
            final String _tmpQuestionId;
            _tmpQuestionId = _cursor.getString(_cursorIndexOfQuestionId);
            final String _tmpSelected;
            _tmpSelected = _cursor.getString(_cursorIndexOfSelected);
            final long _tmpAnsweredAt;
            _tmpAnsweredAt = _cursor.getLong(_cursorIndexOfAnsweredAt);
            _item = new AnswerEntity(_tmpExamId,_tmpNisn,_tmpQuestionId,_tmpSelected,_tmpAnsweredAt);
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

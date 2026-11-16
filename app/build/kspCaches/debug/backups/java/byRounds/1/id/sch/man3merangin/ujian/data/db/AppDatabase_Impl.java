package id.sch.man3merangin.ujian.data.db;

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
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile AnswerDao _answerDao;

  private volatile OutboxDao _outboxDao;

  private volatile ExamPackageDao _examPackageDao;

  private volatile ViolationDao _violationDao;

  private volatile ExamSessionDao _examSessionDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `exam_packages` (`examId` TEXT NOT NULL, `title` TEXT NOT NULL, `startAt` INTEGER NOT NULL, `endAt` INTEGER NOT NULL, `envelopeJson` TEXT NOT NULL, `version` INTEGER NOT NULL, PRIMARY KEY(`examId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `answers` (`examId` TEXT NOT NULL, `nisn` TEXT NOT NULL, `questionId` TEXT NOT NULL, `selected` TEXT NOT NULL, `answeredAt` INTEGER NOT NULL, PRIMARY KEY(`examId`, `nisn`, `questionId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `violations` (`id` TEXT NOT NULL, `examId` TEXT NOT NULL, `nisn` TEXT NOT NULL, `category` TEXT NOT NULL, `startedAt` INTEGER NOT NULL, `durationMs` INTEGER NOT NULL, `questionNo` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_outbox` (`id` TEXT NOT NULL, `kind` TEXT NOT NULL, `payloadJson` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `attempts` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `exam_sessions` (`examId` TEXT NOT NULL, `nisn` TEXT NOT NULL, `remainingMs` INTEGER NOT NULL, `snapshotElapsed` INTEGER NOT NULL, `clockUnverified` INTEGER NOT NULL, PRIMARY KEY(`examId`, `nisn`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0f476f8e1a644f07a9a9d407a679bab3')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `exam_packages`");
        db.execSQL("DROP TABLE IF EXISTS `answers`");
        db.execSQL("DROP TABLE IF EXISTS `violations`");
        db.execSQL("DROP TABLE IF EXISTS `sync_outbox`");
        db.execSQL("DROP TABLE IF EXISTS `exam_sessions`");
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
        final HashMap<String, TableInfo.Column> _columnsExamPackages = new HashMap<String, TableInfo.Column>(6);
        _columnsExamPackages.put("examId", new TableInfo.Column("examId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamPackages.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamPackages.put("startAt", new TableInfo.Column("startAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamPackages.put("endAt", new TableInfo.Column("endAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamPackages.put("envelopeJson", new TableInfo.Column("envelopeJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamPackages.put("version", new TableInfo.Column("version", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExamPackages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExamPackages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExamPackages = new TableInfo("exam_packages", _columnsExamPackages, _foreignKeysExamPackages, _indicesExamPackages);
        final TableInfo _existingExamPackages = TableInfo.read(db, "exam_packages");
        if (!_infoExamPackages.equals(_existingExamPackages)) {
          return new RoomOpenHelper.ValidationResult(false, "exam_packages(id.sch.man3merangin.ujian.data.db.ExamPackageEntity).\n"
                  + " Expected:\n" + _infoExamPackages + "\n"
                  + " Found:\n" + _existingExamPackages);
        }
        final HashMap<String, TableInfo.Column> _columnsAnswers = new HashMap<String, TableInfo.Column>(5);
        _columnsAnswers.put("examId", new TableInfo.Column("examId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAnswers.put("nisn", new TableInfo.Column("nisn", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAnswers.put("questionId", new TableInfo.Column("questionId", "TEXT", true, 3, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAnswers.put("selected", new TableInfo.Column("selected", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAnswers.put("answeredAt", new TableInfo.Column("answeredAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAnswers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAnswers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAnswers = new TableInfo("answers", _columnsAnswers, _foreignKeysAnswers, _indicesAnswers);
        final TableInfo _existingAnswers = TableInfo.read(db, "answers");
        if (!_infoAnswers.equals(_existingAnswers)) {
          return new RoomOpenHelper.ValidationResult(false, "answers(id.sch.man3merangin.ujian.data.db.AnswerEntity).\n"
                  + " Expected:\n" + _infoAnswers + "\n"
                  + " Found:\n" + _existingAnswers);
        }
        final HashMap<String, TableInfo.Column> _columnsViolations = new HashMap<String, TableInfo.Column>(7);
        _columnsViolations.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsViolations.put("examId", new TableInfo.Column("examId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsViolations.put("nisn", new TableInfo.Column("nisn", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsViolations.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsViolations.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsViolations.put("durationMs", new TableInfo.Column("durationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsViolations.put("questionNo", new TableInfo.Column("questionNo", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysViolations = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesViolations = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoViolations = new TableInfo("violations", _columnsViolations, _foreignKeysViolations, _indicesViolations);
        final TableInfo _existingViolations = TableInfo.read(db, "violations");
        if (!_infoViolations.equals(_existingViolations)) {
          return new RoomOpenHelper.ValidationResult(false, "violations(id.sch.man3merangin.ujian.data.db.ViolationEntity).\n"
                  + " Expected:\n" + _infoViolations + "\n"
                  + " Found:\n" + _existingViolations);
        }
        final HashMap<String, TableInfo.Column> _columnsSyncOutbox = new HashMap<String, TableInfo.Column>(5);
        _columnsSyncOutbox.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncOutbox.put("kind", new TableInfo.Column("kind", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncOutbox.put("payloadJson", new TableInfo.Column("payloadJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncOutbox.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncOutbox.put("attempts", new TableInfo.Column("attempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSyncOutbox = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSyncOutbox = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSyncOutbox = new TableInfo("sync_outbox", _columnsSyncOutbox, _foreignKeysSyncOutbox, _indicesSyncOutbox);
        final TableInfo _existingSyncOutbox = TableInfo.read(db, "sync_outbox");
        if (!_infoSyncOutbox.equals(_existingSyncOutbox)) {
          return new RoomOpenHelper.ValidationResult(false, "sync_outbox(id.sch.man3merangin.ujian.data.db.OutboxEntity).\n"
                  + " Expected:\n" + _infoSyncOutbox + "\n"
                  + " Found:\n" + _existingSyncOutbox);
        }
        final HashMap<String, TableInfo.Column> _columnsExamSessions = new HashMap<String, TableInfo.Column>(5);
        _columnsExamSessions.put("examId", new TableInfo.Column("examId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamSessions.put("nisn", new TableInfo.Column("nisn", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamSessions.put("remainingMs", new TableInfo.Column("remainingMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamSessions.put("snapshotElapsed", new TableInfo.Column("snapshotElapsed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamSessions.put("clockUnverified", new TableInfo.Column("clockUnverified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExamSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExamSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExamSessions = new TableInfo("exam_sessions", _columnsExamSessions, _foreignKeysExamSessions, _indicesExamSessions);
        final TableInfo _existingExamSessions = TableInfo.read(db, "exam_sessions");
        if (!_infoExamSessions.equals(_existingExamSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "exam_sessions(id.sch.man3merangin.ujian.data.db.ExamSessionEntity).\n"
                  + " Expected:\n" + _infoExamSessions + "\n"
                  + " Found:\n" + _existingExamSessions);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "0f476f8e1a644f07a9a9d407a679bab3", "c2fc39113734c67554ff25c8aa45cfa8");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "exam_packages","answers","violations","sync_outbox","exam_sessions");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `exam_packages`");
      _db.execSQL("DELETE FROM `answers`");
      _db.execSQL("DELETE FROM `violations`");
      _db.execSQL("DELETE FROM `sync_outbox`");
      _db.execSQL("DELETE FROM `exam_sessions`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
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
    _typeConvertersMap.put(AnswerDao.class, AnswerDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(OutboxDao.class, OutboxDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExamPackageDao.class, ExamPackageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ViolationDao.class, ViolationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExamSessionDao.class, ExamSessionDao_Impl.getRequiredConverters());
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
  public AnswerDao answerDao() {
    if (_answerDao != null) {
      return _answerDao;
    } else {
      synchronized(this) {
        if(_answerDao == null) {
          _answerDao = new AnswerDao_Impl(this);
        }
        return _answerDao;
      }
    }
  }

  @Override
  public OutboxDao outboxDao() {
    if (_outboxDao != null) {
      return _outboxDao;
    } else {
      synchronized(this) {
        if(_outboxDao == null) {
          _outboxDao = new OutboxDao_Impl(this);
        }
        return _outboxDao;
      }
    }
  }

  @Override
  public ExamPackageDao examPackageDao() {
    if (_examPackageDao != null) {
      return _examPackageDao;
    } else {
      synchronized(this) {
        if(_examPackageDao == null) {
          _examPackageDao = new ExamPackageDao_Impl(this);
        }
        return _examPackageDao;
      }
    }
  }

  @Override
  public ViolationDao violationDao() {
    if (_violationDao != null) {
      return _violationDao;
    } else {
      synchronized(this) {
        if(_violationDao == null) {
          _violationDao = new ViolationDao_Impl(this);
        }
        return _violationDao;
      }
    }
  }

  @Override
  public ExamSessionDao examSessionDao() {
    if (_examSessionDao != null) {
      return _examSessionDao;
    } else {
      synchronized(this) {
        if(_examSessionDao == null) {
          _examSessionDao = new ExamSessionDao_Impl(this);
        }
        return _examSessionDao;
      }
    }
  }
}

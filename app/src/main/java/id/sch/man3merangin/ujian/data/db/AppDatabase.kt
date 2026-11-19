package id.sch.man3merangin.ujian.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ExamPackageEntity::class,
        AnswerEntity::class,
        ViolationEntity::class,
        OutboxEntity::class,
        ExamSessionEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun answerDao(): AnswerDao
    abstract fun outboxDao(): OutboxDao
    abstract fun examPackageDao(): ExamPackageDao
    abstract fun violationDao(): ViolationDao
    abstract fun examSessionDao(): ExamSessionDao

    companion object {
        // ponytail: pre-upgrade rows get nisn '' (payload has no nisn); the worker sends them with the active session, acceptable for one-off legacy rows
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sync_outbox ADD COLUMN nisn TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE exam_packages ADD COLUMN published INTEGER NOT NULL DEFAULT 1")
            }
        }

        private const val DB_NAME = "man3_ujian.db"

        fun build(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
        }

        fun inMemory(context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                AppDatabase::class.java
            ).allowMainThreadQueries().build()
        }
    }
}

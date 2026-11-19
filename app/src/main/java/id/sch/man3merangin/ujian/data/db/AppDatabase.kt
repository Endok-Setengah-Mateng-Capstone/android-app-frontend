package id.sch.man3merangin.ujian.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ExamPackageEntity::class,
        AnswerEntity::class,
        ViolationEntity::class,
        OutboxEntity::class,
        ExamSessionEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun answerDao(): AnswerDao
    abstract fun outboxDao(): OutboxDao
    abstract fun examPackageDao(): ExamPackageDao
    abstract fun violationDao(): ViolationDao
    abstract fun examSessionDao(): ExamSessionDao

    companion object {
        private const val DB_NAME = "man3_ujian.db"

        fun build(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            ).build()
        }

        fun inMemory(context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                AppDatabase::class.java
            ).allowMainThreadQueries().build()
        }
    }
}

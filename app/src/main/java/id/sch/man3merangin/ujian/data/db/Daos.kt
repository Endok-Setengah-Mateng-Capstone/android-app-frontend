package id.sch.man3merangin.ujian.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(answer: AnswerEntity)

    @Query("SELECT * FROM answers WHERE examId = :examId AND nisn = :nisn")
    suspend fun forUser(examId: String, nisn: String): List<AnswerEntity>

    @Query("DELETE FROM answers WHERE examId = :examId AND nisn = :nisn")
    suspend fun clearForUser(examId: String, nisn: String)
}

@Dao
interface OutboxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(outbox: OutboxEntity)

    @Query("SELECT * FROM sync_outbox ORDER BY createdAt ASC LIMIT :limit")
    suspend fun pending(limit: Int): List<OutboxEntity>

    @Query("SELECT * FROM sync_outbox WHERE nisn = :nisn ORDER BY createdAt ASC LIMIT :limit")
    suspend fun pending(nisn: String, limit: Int): List<OutboxEntity>

    @Query("SELECT DISTINCT nisn FROM sync_outbox")
    suspend fun pendingNisns(): List<String>

    @Query("DELETE FROM sync_outbox WHERE id IN (:ids)")
    suspend fun delete(ids: List<String>)

    @Query("UPDATE sync_outbox SET attempts = attempts + 1 WHERE id IN (:ids)")
    suspend fun bumpAttempts(ids: List<String>)

    @Query("SELECT COUNT(*) FROM sync_outbox")
    suspend fun count(): Int
}

@Dao
interface ExamPackageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pkg: ExamPackageEntity)

    @Query("SELECT * FROM exam_packages WHERE examId = :examId LIMIT 1")
    suspend fun getById(examId: String): ExamPackageEntity?

    @Query("SELECT * FROM exam_packages ORDER BY startAt DESC")
    fun getAllFlow(): Flow<List<ExamPackageEntity>>

    @Query("SELECT * FROM exam_packages ORDER BY startAt DESC")
    suspend fun getAll(): List<ExamPackageEntity>

    @Query("DELETE FROM exam_packages WHERE examId = :examId")
    suspend fun delete(examId: String)
}

@Dao
interface ViolationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(violation: ViolationEntity)

    @Query("SELECT * FROM violations WHERE examId = :examId AND nisn = :nisn ORDER BY startedAt ASC")
    suspend fun forUser(examId: String, nisn: String): List<ViolationEntity>
}

@Dao
interface ExamSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: ExamSessionEntity)

    @Query("SELECT * FROM exam_sessions WHERE examId = :examId AND nisn = :nisn LIMIT 1")
    suspend fun get(examId: String, nisn: String): ExamSessionEntity?
}

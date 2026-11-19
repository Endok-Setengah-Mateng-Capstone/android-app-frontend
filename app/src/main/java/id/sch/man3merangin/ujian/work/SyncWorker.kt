package id.sch.man3merangin.ujian.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import id.sch.man3merangin.ujian.UjianApplication
import id.sch.man3merangin.ujian.data.net.ApiException
import id.sch.man3merangin.ujian.data.net.DeviceMismatchException
import id.sch.man3merangin.ujian.data.net.SyncItem
import java.io.IOException
import kotlinx.coroutines.CancellationException
import java.util.concurrent.TimeUnit

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? UjianApplication ?: return Result.failure()
        val db = app.database
        val api = app.api

        val session = app.session
        var result: Result = Result.success()
        for (owner in db.outboxDao().pendingNisns()) {
            // ponytail: '' = rows from before migration 1->2, sent with the active session
            val token = (if (owner.isEmpty()) session.token else session.tokenFor(owner)) ?: continue
            try {
                val rows = db.outboxDao().pending(owner, 50)
                val accepted = api.sync(rows.map { SyncItem(it.id, it.kind, it.payloadJson) }, token).acceptedIds
                if (accepted.isNotEmpty()) db.outboxDao().delete(accepted)
            } catch (e: CancellationException) {
                throw e
            } catch (_: IOException) {
                result = Result.retry()
            } catch (_: DeviceMismatchException) {
                // TODO(Task 10c): surface "perangkat belum diizinkan" in UI; rows are kept for later
            } catch (e: ApiException) {
                if (e.code == 401) session.forgetToken(owner) // rows kept; owner waits for a new login
                if (e.code == 401 || e.code == 403 || e.code in 500..599) result = Result.retry()
                else if (result != Result.retry()) result = Result.failure()
            } catch (_: Exception) {
                result = Result.retry()
            }
        }
        // drop tokens of non-active students whose queue is empty
        val waiting = db.outboxDao().pendingNisns().toSet()
        session.storedNisns().filter { it != session.nisn && it !in waiting }.forEach(session::forgetToken)
        return result
    }
}

class PackageDownloadWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? UjianApplication ?: return Result.failure()
        return try {
            app.examRepository.refresh()
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}

object Scheduler {
    private const val SYNC_WORK_NAME = "sync"
    private const val PACKAGE_WORK_NAME = "packages"

    fun enqueueSync(context: Context) {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                SYNC_WORK_NAME,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                request
            )
        } catch (_: Throwable) {}
    }

    fun kickSyncNow(context: Context) {
        try {
            val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
        } catch (_: Throwable) {}
    }

    fun schedulePackages(context: Context) {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val periodic = PeriodicWorkRequestBuilder<PackageDownloadWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PACKAGE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodic
            )
        } catch (_: Throwable) {}
    }

    fun pullNow(context: Context) {
        try {
            val request = OneTimeWorkRequestBuilder<PackageDownloadWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
        } catch (_: Throwable) {}
    }
}

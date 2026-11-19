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
import java.util.concurrent.TimeUnit

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? UjianApplication ?: return Result.failure()
        val db = app.database
        val api = app.api

        val pendingList = db.outboxDao().pending(50)
        if (pendingList.isEmpty()) {
            return Result.success()
        }

        val syncBatch = pendingList.map {
            SyncItem(
                id = it.id,
                kind = it.kind,
                payloadJson = it.payloadJson
            )
        }

        return try {
            val response = api.sync(syncBatch)
            if (response.acceptedIds.isNotEmpty()) {
                db.outboxDao().delete(response.acceptedIds)
            }
            Result.success()
        } catch (_: IOException) {
            Result.retry()
        } catch (_: DeviceMismatchException) {
            // TODO(Task 10c): surface "perangkat belum diizinkan" in UI; rows are kept for later
            Result.success()
        } catch (e: ApiException) {
            if (e.code == 401 || e.code == 403 || e.code in 500..599) {
                Result.retry()
            } else {
                Result.failure()
            }
        } catch (_: Exception) {
            Result.retry()
        }
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

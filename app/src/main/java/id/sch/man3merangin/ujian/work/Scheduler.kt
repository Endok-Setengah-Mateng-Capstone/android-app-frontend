package id.sch.man3merangin.ujian.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

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

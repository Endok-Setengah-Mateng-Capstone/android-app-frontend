package id.sch.man3merangin.ujian.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import id.sch.man3merangin.ujian.UjianApplication

class PackageDownloadWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? UjianApplication ?: return Result.failure()
        val repo = app.examRepository
        repo.refresh()
        return if (repo.failures > 0) Result.retry() else Result.success()
    }
}

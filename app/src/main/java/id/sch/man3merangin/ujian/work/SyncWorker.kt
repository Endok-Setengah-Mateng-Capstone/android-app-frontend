package id.sch.man3merangin.ujian.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import id.sch.man3merangin.ujian.UjianApplication
import id.sch.man3merangin.ujian.data.net.ApiException
import id.sch.man3merangin.ujian.data.net.DeviceMismatchException
import id.sch.man3merangin.ujian.data.net.SyncItem
import java.io.IOException
import kotlinx.coroutines.CancellationException

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
                // drain in batches of 50; stop when empty or the server accepts none of the rows sent
                while (true) {
                    val rows = db.outboxDao().pending(owner, 50)
                    if (rows.isEmpty()) break
                    val accepted = api.sync(rows.map { SyncItem(it.id, it.kind, it.payloadJson) }, token).acceptedIds
                    if (rows.none { it.id in accepted }) break
                    db.outboxDao().delete(accepted)
                }
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

package id.sch.man3merangin.ujian

import android.app.Application
import android.preference.PreferenceManager
import id.sch.man3merangin.ujian.data.AnswerRepository
import id.sch.man3merangin.ujian.data.DeviceId
import id.sch.man3merangin.ujian.data.ExamRepository
import id.sch.man3merangin.ujian.data.Session
import id.sch.man3merangin.ujian.data.ViolationRepository
import id.sch.man3merangin.ujian.data.db.AppDatabase
import id.sch.man3merangin.ujian.data.net.ExamApi
import id.sch.man3merangin.ujian.data.net.FakeExamApi
import id.sch.man3merangin.ujian.data.net.HttpExamApi
import id.sch.man3merangin.ujian.time.MemoryClockStore
import id.sch.man3merangin.ujian.time.TrustedClock
import id.sch.man3merangin.ujian.violation.AlarmPlayer
import id.sch.man3merangin.ujian.violation.IAlarmPlayer
import id.sch.man3merangin.ujian.work.Scheduler

class UjianApplication : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var session: Session
        private set
    lateinit var clock: TrustedClock
        private set
    lateinit var api: ExamApi
        private set
    lateinit var answerRepository: AnswerRepository
        private set
    lateinit var violationRepository: ViolationRepository
        private set
    lateinit var examRepository: ExamRepository
        private set
    lateinit var alarmPlayer: IAlarmPlayer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        session = Session(prefs)
        database = AppDatabase.build(this)

        clock = TrustedClock(store = MemoryClockStore())

        // Default to FakeExamApi with sample package for standalone testing; can point to server URL if configured
        val baseUrl = prefs.getString("server_base_url", null)
        api = if (!baseUrl.isNullOrBlank()) {
            HttpExamApi(
                baseUrl = baseUrl,
                sessionProvider = { session.token },
                deviceIdProvider = { DeviceId.get(prefs) }
            )
        } else {
            FakeExamApi()
        }

        answerRepository = AnswerRepository(database)
        violationRepository = ViolationRepository(database)
        examRepository = ExamRepository(api, database, clock)
        alarmPlayer = AlarmPlayer(this)

        try {
            Scheduler.schedulePackages(this)
        } catch (_: Throwable) {
            // Ignored in test environments where WorkManager is uninitialized
        }
    }

    companion object {
        lateinit var instance: UjianApplication
            private set
    }
}

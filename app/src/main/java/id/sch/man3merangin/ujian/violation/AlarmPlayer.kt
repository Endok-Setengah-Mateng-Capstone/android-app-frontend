package id.sch.man3merangin.ujian.violation

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager

interface IAlarmPlayer {
    fun play()
    fun stop()
}

class AlarmPlayer(private val context: Context) : IAlarmPlayer {
    private var ringtone: Ringtone? = null

    override fun play() {
        try {
            if (ringtone == null) {
                val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ringtone = RingtoneManager.getRingtone(context.applicationContext, notification)
            }
            if (ringtone?.isPlaying == false) {
                ringtone?.play()
            }
        } catch (_: Exception) {}
    }

    override fun stop() {
        try {
            if (ringtone?.isPlaying == true) {
                ringtone?.stop()
            }
        } catch (_: Exception) {}
    }
}

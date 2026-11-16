package id.sch.man3merangin.ujian.data

import android.content.SharedPreferences
import java.util.UUID

class Session(private val prefs: SharedPreferences) {
    var nisn: String?
        get() = prefs.getString("session_nisn", null)
        set(value) = prefs.edit().putString("session_nisn", value).apply()

    var token: String?
        get() = prefs.getString("session_token", null)
        set(value) = prefs.edit().putString("session_token", value).apply()

    var studentName: String?
        get() = prefs.getString("session_student_name", "Ahmad Fauzan Al-Ghifari")
        set(value) = prefs.edit().putString("session_student_name", value).apply()

    fun isLoggedIn(): Boolean = !token.isNullOrBlank() && !nisn.isNullOrBlank()

    fun clear() {
        prefs.edit()
            .remove("session_nisn")
            .remove("session_token")
            .remove("session_student_name")
            .apply()
    }
}

object DeviceId {
    private const val KEY_DEVICE_ID = "app_device_uuid"

    fun get(prefs: SharedPreferences): String {
        var id = prefs.getString(KEY_DEVICE_ID, null)
        if (id.isNullOrBlank()) {
            id = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_DEVICE_ID, id).apply()
        }
        return id
    }
}

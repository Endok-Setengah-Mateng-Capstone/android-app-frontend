package id.sch.man3merangin.ujian.data.net

import id.sch.man3merangin.ujian.data.crypto.Envelope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class HttpExamApi(
    private val baseUrl: String,
    private val sessionProvider: () -> String?,
    private val deviceIdProvider: () -> String,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
) : ExamApi {
    private val json = Json { ignoreUnknownKeys = true }
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private fun requestBuilder(path: String, token: String? = sessionProvider()): Request.Builder {
        val url = if (baseUrl.endsWith("/")) "$baseUrl$path" else "$baseUrl/$path"
        val builder = Request.Builder().url(url)
        token?.let { builder.addHeader("Authorization", "Bearer $it") }
        builder.addHeader("X-Device-Id", deviceIdProvider())
        return builder
    }

    // OkHttp execute() is blocking: always run it off the caller's (possibly Main) dispatcher.
    private suspend fun <T> call(request: Request, parse: (okhttp3.Response) -> T): T =
        withContext(Dispatchers.IO) { client.newCall(request).execute().use(parse) }

    private fun handleResponse(response: okhttp3.Response): String {
        val code = response.code
        val bodyString = response.body?.string().orEmpty()
        if (code == 409) {
            throw DeviceMismatchException()
        }
        if (!response.isSuccessful) {
            throw ApiException(code, "Server returned error: $code - $bodyString")
        }
        return bodyString
    }

    @Serializable
    private data class LoginRequest(val nisn: String, val password: String)

    override suspend fun login(nisn: String, password: String): LoginResult {
        val reqBody = json.encodeToString(LoginRequest(nisn, password)).toRequestBody(jsonMediaType)
        val request = requestBuilder("auth/login").post(reqBody).build()
        return call(request) { response ->
            val body = handleResponse(response)
            json.decodeFromString<LoginResult>(body)
        }
    }

    override suspend fun listExams(): List<ExamSummary> {
        val request = requestBuilder("exams").get().build()
        return call(request) { response ->
            val body = handleResponse(response)
            json.decodeFromString<List<ExamSummary>>(body)
        }
    }

    override suspend fun downloadPackage(examId: String): Envelope {
        val request = requestBuilder("exams/$examId/package").get().build()
        return call(request) { response ->
            val body = handleResponse(response)
            json.decodeFromString<Envelope>(body)
        }
    }

    @Serializable
    private data class SyncBatchRequest(val items: List<SyncItem>)

    override suspend fun sync(batch: List<SyncItem>, sessionToken: String): SyncResult {
        val reqBody = json.encodeToString(SyncBatchRequest(batch)).toRequestBody(jsonMediaType)
        val request = requestBuilder("sync", sessionToken).post(reqBody).build()
        return call(request) { response ->
            val body = handleResponse(response)
            json.decodeFromString<SyncResult>(body)
        }
    }

    @Serializable
    private data class TimeResponse(val serverNowMs: Long)

    override suspend fun serverTime(): Long {
        val request = requestBuilder("time").get().build()
        return call(request) { response ->
            val body = handleResponse(response)
            json.decodeFromString<TimeResponse>(body).serverNowMs
        }
    }

    @Serializable
    private data class ReadyRequest(val version: Int)

    override suspend fun ready(examId: String, version: Int) {
        val reqBody = json.encodeToString(ReadyRequest(version)).toRequestBody(jsonMediaType)
        val request = requestBuilder("exams/$examId/ready").post(reqBody).build()
        call(request) { response ->
            handleResponse(response)
        }
    }

    @Serializable
    private data class HeartbeatRequest(val answeredCount: Int)
    @Serializable
    private data class HeartbeatResponse(val serverNowMs: Long)

    override suspend fun heartbeat(examId: String, answeredCount: Int): Long {
        val reqBody = json.encodeToString(HeartbeatRequest(answeredCount)).toRequestBody(jsonMediaType)
        val request = requestBuilder("exams/$examId/heartbeat").post(reqBody).build()
        return call(request) { response ->
            val body = handleResponse(response)
            json.decodeFromString<HeartbeatResponse>(body).serverNowMs
        }
    }

    override suspend fun myAnswers(examId: String): RemoteAnswers {
        val request = requestBuilder("exams/$examId/my-answers").get().build()
        return call(request) { response ->
            val body = handleResponse(response)
            json.decodeFromString<RemoteAnswers>(body)
        }
    }
}

package id.sch.man3merangin.ujian.data.net

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class HttpExamApiTest {
    private lateinit var server: MockWebServer
    private lateinit var api: HttpExamApi

    @Before fun setUp() {
        server = MockWebServer().also { it.start() }
        api = HttpExamApi(server.url("/").toString(), { "sess-1" }, { "dev-uuid" })
    }

    @After fun tearDown() { server.shutdown() }

    @Test fun loginPostsJsonAndParses() = runBlocking {
        server.enqueue(MockResponse().setBody("""{"sessionToken":"t","nisn":"123","name":"Budi"}"""))
        val r = api.login("123", "pw")
        assertEquals(LoginResult("t", "123", "Budi"), r)
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        assertEquals("/auth/login", req.path)
        assertTrue(req.getHeader("Content-Type")!!.startsWith("application/json"))
        assertEquals("""{"nisn":"123","password":"pw"}""", req.body.readUtf8())
    }

    @Test fun syncSendsBearerAndBatch() = runBlocking {
        server.enqueue(MockResponse().setBody("""{"acceptedIds":["a"]}"""))
        val r = api.sync(listOf(SyncItem("a", "ANSWER", "{}")), "owner-tok")
        assertEquals(listOf("a"), r.acceptedIds)
        val req = server.takeRequest()
        assertEquals("/sync", req.path)
        assertEquals("Bearer owner-tok", req.getHeader("Authorization"))
        assertEquals("""{"items":[{"id":"a","kind":"ANSWER","payloadJson":"{}"}]}""", req.body.readUtf8())
    }

    @Test fun serverErrorThrowsApiException() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(500))
        try { api.listExams(); fail() } catch (e: ApiException) { assertEquals(500, e.code) }
    }

    @Test fun sendsDeviceIdHeader() = runBlocking {
        server.enqueue(MockResponse().setBody("""{"serverNowMs":5}"""))
        assertEquals(5L, api.serverTime())
        assertEquals("dev-uuid", server.takeRequest().getHeader("X-Device-Id"))
    }

    @Test fun status409ThrowsDeviceMismatch() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(409))
        try { api.sync(emptyList(), "t"); fail() } catch (_: DeviceMismatchException) {}
    }

    @Test fun listExamsParsesContractShape() = runBlocking {
        server.enqueue(MockResponse().setBody("""[{"examId":"e1","version":2,"startAt":10,"endAt":20}]"""))
        assertEquals(listOf(ExamSummary("e1", version = 2, startAt = 10, endAt = 20)), api.listExams())
    }

    @Test fun readyPostsVersion() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(204))
        api.ready("e1", 3)
        val req = server.takeRequest()
        assertEquals("/exams/e1/ready", req.path)
        assertEquals("""{"version":3}""", req.body.readUtf8())
    }

    @Test fun heartbeatPostsCountAndParsesServerNow() = runBlocking {
        server.enqueue(MockResponse().setBody("""{"serverNowMs":99}"""))
        assertEquals(99L, api.heartbeat("e1", 4))
        val req = server.takeRequest()
        assertEquals("/exams/e1/heartbeat", req.path)
        assertEquals("""{"answeredCount":4}""", req.body.readUtf8())
    }

    @Test fun myAnswersParsesContractShape() = runBlocking {
        server.enqueue(MockResponse().setBody("""{"answers":[{"questionId":"q","selected":["a"],"answeredAt":7}],"submittedAt":null}"""))
        assertEquals(RemoteAnswers(listOf(RemoteAnswer("q", listOf("a"), 7)), null), api.myAnswers("e1"))
    }
}

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
        val r = api.sync(listOf(SyncItem("a", "ANSWER", "{}")))
        assertEquals(listOf("a"), r.acceptedIds)
        val req = server.takeRequest()
        assertEquals("/sync", req.path)
        assertEquals("Bearer sess-1", req.getHeader("Authorization"))
        assertTrue(req.body.readUtf8().contains(""""id":"a""""))
    }

    @Test fun serverErrorThrowsApiException() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(500))
        try { api.listExams(); fail() } catch (e: ApiException) { assertEquals(500, e.code) }
    }

    @Test fun sendsDeviceIdHeader() = runBlocking {
        server.enqueue(MockResponse().setBody("""{"timeMs":5}"""))
        assertEquals(5L, api.serverTime())
        assertEquals("dev-uuid", server.takeRequest().getHeader("X-Device-Id"))
    }

    @Test fun status409ThrowsDeviceMismatch() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(409))
        try { api.sync(emptyList()); fail() } catch (_: DeviceMismatchException) {}
    }
}

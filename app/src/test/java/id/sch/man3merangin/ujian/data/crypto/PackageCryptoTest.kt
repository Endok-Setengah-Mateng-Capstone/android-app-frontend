package id.sch.man3merangin.ujian.data.crypto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class PackageCryptoTest {

    private val sampleContent = ExamContent(
        questions = listOf(
            Question(
                id = "q1",
                no = 1,
                text = "Pertanyaan biologi 1",
                type = QuestionType.SINGLE,
                options = listOf(
                    Option("a", "Pilihan A"),
                    Option("b", "Pilihan B")
                )
            )
        ),
        shuffleQuestions = false,
        shuffleOptions = false
    )

    private val token = "MN3-9X4"
    private val saltB64 = "c2FsdHNhbHRzYWx0MTIzNA=="
    private val iterations = 10_000

    @Test
    fun roundTripWithRightToken() {
        val env = PackageCrypto.seal(
            content = sampleContent,
            examId = "exam-1",
            title = "Ujian Biologi",
            startAt = 1000L,
            endAt = 5000L,
            token = token,
            saltB64 = saltB64,
            iterations = iterations
        )

        val decrypted = PackageCrypto.open(env, token)
        assertEquals(sampleContent, decrypted)
    }

    @Test
    fun wrongTokenThrows() {
        val env = PackageCrypto.seal(
            content = sampleContent,
            examId = "exam-1",
            title = "Ujian Biologi",
            startAt = 1000L,
            endAt = 5000L,
            token = token,
            saltB64 = saltB64,
            iterations = iterations
        )

        assertThrows(WrongTokenException::class.java) {
            PackageCrypto.open(env, "WRONG-TOKEN")
        }
    }

    @Test
    fun tokenIsCaseSensitive() {
        val env = PackageCrypto.seal(
            content = sampleContent,
            examId = "exam-1",
            title = "Ujian Biologi",
            startAt = 1000L,
            endAt = 5000L,
            token = "AbC123",
            saltB64 = saltB64,
            iterations = iterations
        )

        assertThrows(WrongTokenException::class.java) {
            PackageCrypto.open(env, "abc123")
        }
    }

    @Test
    fun tamperedCipherThrowsWrongToken() {
        val env = PackageCrypto.seal(
            content = sampleContent,
            examId = "exam-1",
            title = "Ujian Biologi",
            startAt = 1000L,
            endAt = 5000L,
            token = token,
            saltB64 = saltB64,
            iterations = iterations
        )

        val bytes = java.util.Base64.getDecoder().decode(env.cipherB64)
        bytes[0] = (bytes[0].toInt() xor 1).toByte()
        val tampered = env.copy(cipherB64 = java.util.Base64.getEncoder().encodeToString(bytes))
        assertThrows(WrongTokenException::class.java) {
            PackageCrypto.open(tampered, token)
        }
    }

    @Test
    fun absurdIterationsRejected() {
        val env = PackageCrypto.seal(sampleContent, "exam-1", "T", 1L, 2L, token, saltB64, iterations)
        listOf(0, 9_999, 1_000_001, Int.MAX_VALUE).forEach {
            assertThrows(WrongTokenException::class.java) { PackageCrypto.open(env.copy(iterations = it), token) }
        }
    }

    @Test
    fun pointsRoundTripAndDefaultOne() {
        val content = ExamContent(listOf(sampleContent.questions[0].copy(points = 2.5)))
        val env = PackageCrypto.seal(content, "exam-1", "T", 1L, 2L, token, saltB64, iterations)
        assertEquals(2.5, PackageCrypto.open(env, token).questions[0].points, 0.0)
        val q = Json.decodeFromString<Question>("""{"id":"q","no":1,"text":"t","type":"SINGLE","options":[]}""")
        assertEquals(1.0, q.points, 0.0)
    }

    @Serializable
    private data class Vector(val token: String, val envelope: Envelope, val content: ExamContent)

    @Test
    fun vector1FixtureOpensWithItsToken() {
        val v = Json { ignoreUnknownKeys = true }.decodeFromString<Vector>(File("../fixtures/vector1.json").readText())
        assertEquals(v.content, PackageCrypto.open(v.envelope, v.token))
    }
}

package id.sch.man3merangin.ujian.data.crypto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

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
    private val iterations = 1000

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

        val tampered = env.copy(cipherB64 = env.cipherB64.reversed())
        assertThrows(WrongTokenException::class.java) {
            PackageCrypto.open(tampered, token)
        }
    }
}

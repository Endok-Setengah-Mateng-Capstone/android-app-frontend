package id.sch.man3merangin.ujian.data.crypto

import java.util.Base64
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class WrongTokenException : Exception("Token ujian tidak valid atau berkas rusak")

@Serializable
enum class QuestionType {
    SINGLE,
    MULTI
}

@Serializable
data class Option(
    val id: String,
    val text: String
)

@Serializable
data class Question(
    val id: String,
    val no: Int,
    val text: String,
    val type: QuestionType,
    val options: List<Option>,
    val imageB64: String? = null,
    val points: Double = 1.0
)

@Serializable
data class ExamContent(
    val questions: List<Question>,
    val shuffleQuestions: Boolean = false,
    val shuffleOptions: Boolean = false
)

@Serializable
data class Envelope(
    val examId: String,
    val title: String,
    val startAt: Long,
    val endAt: Long,
    val saltB64: String,
    val iterations: Int,
    val ivB64: String,
    val cipherB64: String
)

object PackageCrypto {
    private val json = Json { ignoreUnknownKeys = true }
    private const val KEY_LENGTH_BITS = 256
    private const val GCM_TAG_LENGTH_BITS = 128
    private val encoder = Base64.getEncoder()
    private val decoder = Base64.getDecoder()

    private fun deriveKey(token: String, salt: ByteArray, iterations: Int): SecretKeySpec {
        val spec = PBEKeySpec(token.toCharArray(), salt, iterations, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    fun seal(
        content: ExamContent,
        examId: String,
        title: String,
        startAt: Long,
        endAt: Long,
        token: String,
        saltB64: String,
        iterations: Int
    ): Envelope {
        val salt = decoder.decode(saltB64)
        val key = deriveKey(token, salt, iterations)

        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))

        val plainJson = json.encodeToString(content)
        val cipherBytes = cipher.doFinal(plainJson.toByteArray(StandardCharsets.UTF_8))

        return Envelope(
            examId = examId,
            title = title,
            startAt = startAt,
            endAt = endAt,
            saltB64 = saltB64,
            iterations = iterations,
            ivB64 = encoder.encodeToString(iv),
            cipherB64 = encoder.encodeToString(cipherBytes)
        )
    }

    fun open(env: Envelope, token: String): ExamContent {
        return try {
            // DoS guard: an attacker-controlled iteration count must not stall the device
            require(env.iterations in 10_000..1_000_000)
            val salt = decoder.decode(env.saltB64)
            val iv = decoder.decode(env.ivB64)
            val cipherBytes = decoder.decode(env.cipherB64)

            val key = deriveKey(token, salt, env.iterations)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))

            val plainBytes = cipher.doFinal(cipherBytes)
            val plainJson = String(plainBytes, StandardCharsets.UTF_8)
            json.decodeFromString<ExamContent>(plainJson)
        } catch (e: Exception) {
            throw WrongTokenException()
        }
    }
}

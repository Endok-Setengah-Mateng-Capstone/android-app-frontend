package id.sch.man3merangin.ujian.exam

import id.sch.man3merangin.ujian.data.crypto.ExamContent
import id.sch.man3merangin.ujian.data.crypto.Option
import id.sch.man3merangin.ujian.data.crypto.Question
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Random

object ExamOrder {
    fun arrange(content: ExamContent, examId: String, nisn: String): ExamContent {
        if (!content.shuffleQuestions && !content.shuffleOptions) {
            return content
        }

        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest("$examId:$nisn".toByteArray(StandardCharsets.UTF_8))
        val seed = ByteBuffer.wrap(hash, 0, 8).long
        val rng = Random(seed)

        val questions = content.questions.map { q ->
            if (content.shuffleOptions) {
                val shuffledOptions = q.options.toMutableList()
                shuffleList(shuffledOptions, rng)
                q.copy(options = shuffledOptions)
            } else {
                q
            }
        }.toMutableList()

        if (content.shuffleQuestions) {
            shuffleList(questions, rng)
        }

        return content.copy(questions = questions)
    }

    private fun <T> shuffleList(list: MutableList<T>, rng: Random) {
        for (i in list.size - 1 downTo 1) {
            val j = rng.nextInt(i + 1)
            val tmp = list[i]
            list[i] = list[j]
            list[j] = tmp
        }
    }
}

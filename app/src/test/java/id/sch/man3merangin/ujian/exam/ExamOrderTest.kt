package id.sch.man3merangin.ujian.exam

import id.sch.man3merangin.ujian.data.crypto.ExamContent
import id.sch.man3merangin.ujian.data.crypto.Option
import id.sch.man3merangin.ujian.data.crypto.Question
import id.sch.man3merangin.ujian.data.crypto.QuestionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ExamOrderTest {

    private fun makeTwentyQuestions(): List<Question> {
        return (1..20).map { i ->
            Question(
                id = "q$i",
                no = i,
                text = "Soal nomor $i",
                type = QuestionType.SINGLE,
                options = listOf(
                    Option("opt1", "Jawaban 1"),
                    Option("opt2", "Jawaban 2"),
                    Option("opt3", "Jawaban 3"),
                    Option("opt4", "Jawaban 4")
                )
            )
        }
    }

    @Test
    fun flagsOffKeepsOriginalOrder() {
        val content = ExamContent(questions = makeTwentyQuestions(), shuffleQuestions = false, shuffleOptions = false)
        val arranged = ExamOrder.arrange(content, "exam-1", "nisn-a")
        assertEquals(content.questions, arranged.questions)
    }

    @Test
    fun sameStudentSameOrderEveryCall() {
        val content = ExamContent(questions = makeTwentyQuestions(), shuffleQuestions = true, shuffleOptions = true)
        val call1 = ExamOrder.arrange(content, "exam-1", "nisn-student-1")
        val call2 = ExamOrder.arrange(content, "exam-1", "nisn-student-1")
        assertEquals(call1.questions, call2.questions)
    }

    @Test
    fun differentNisnGivesDifferentOrderForTwentyQuestions() {
        val content = ExamContent(questions = makeTwentyQuestions(), shuffleQuestions = true, shuffleOptions = false)
        val orderA = ExamOrder.arrange(content, "exam-1", "nisn-A")
        val orderB = ExamOrder.arrange(content, "exam-1", "nisn-B")
        assertNotEquals(orderA.questions.map { it.id }, orderB.questions.map { it.id })
    }

    @Test
    fun idsAndNoPreserved() {
        val original = makeTwentyQuestions()
        val content = ExamContent(questions = original, shuffleQuestions = true, shuffleOptions = true)
        val arranged = ExamOrder.arrange(content, "exam-1", "nisn-random")

        assertEquals(original.map { it.id }.toSet(), arranged.questions.map { it.id }.toSet())
        assertEquals(original.map { it.no }.toSet(), arranged.questions.map { it.no }.toSet())
    }
}

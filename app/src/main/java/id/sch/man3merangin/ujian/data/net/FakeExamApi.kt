package id.sch.man3merangin.ujian.data.net

import id.sch.man3merangin.ujian.data.crypto.Envelope
import id.sch.man3merangin.ujian.data.crypto.ExamContent
import id.sch.man3merangin.ujian.data.crypto.Option
import id.sch.man3merangin.ujian.data.crypto.PackageCrypto
import id.sch.man3merangin.ujian.data.crypto.Question
import id.sch.man3merangin.ujian.data.crypto.QuestionType

class FakeExamApi : ExamApi {
    var currentTimeMs: Long = System.currentTimeMillis()
    var shouldFailSync: Boolean = false
    var shouldThrow409: Boolean = false
    val receivedSyncItems = mutableListOf<SyncItem>()
    var readyCount = 0

    val sampleContent = ExamContent(
        questions = listOf(
            Question(
                id = "q1",
                no = 1,
                text = "Perhatikan ekosistem rawa gambut di Muara Siau Merangin. Manakah faktor pembatas utama yang mempengaruhi laju dekomposisi bahan organik di lapisan anaerobik?",
                type = QuestionType.SINGLE,
                options = listOf(
                    Option("a", "Tingkat keasaman (pH) rendah dan keterbatasan oksigen terlarut"),
                    Option("b", "Kandungan mineral nitrogen anorganik yang terlalu pekat"),
                    Option("c", "Intensitas radiasi ultraviolet gelombang pendek"),
                    Option("d", "Fluktuasi suhu permukaan akibat kanopi terbuka")
                ),
                points = 2.5
            ),
            Question(
                id = "q2",
                no = 2,
                text = "Pilihlah dua atau lebih prinsip fikih lingkungan (Fiqh al-Bi'ah) yang relevan dalam konservasi hutan lindung di Kabupaten Merangin:",
                type = QuestionType.MULTI,
                options = listOf(
                    Option("a", "Hifzh al-bi'ah (pemeliharaan lingkungan hidup sebagai amanah khalifah)"),
                    Option("b", "La dharara wa la dhirar (larangan menimbulkan bahaya bagi diri sendiri dan ekosistem)"),
                    Option("c", "Eksploitasi sumber daya tambang tanpa batas untuk percepatan ekonomi lokal"),
                    Option("d", "Tawazun (menjaga keseimbangan ekologis yang telah diciptakan Allah)")
                ),
                points = 2.5
            ),
            Question(
                id = "q3",
                no = 3,
                text = "Bagian sel tumbuhan manakah yang memiliki peran utama dalam turgiditas sel saat kondisi kadar air tanah mencukupi?",
                type = QuestionType.SINGLE,
                options = listOf(
                    Option("a", "Vakuola sentral"),
                    Option("b", "Mitokondria"),
                    Option("c", "Retikulum endoplasma kasar"),
                    Option("d", "Badan golgi")
                ),
                points = 2.5
            )
        ),
        shuffleQuestions = false,
        shuffleOptions = false
    )

    private val sampleEnvelope: Envelope by lazy {
        PackageCrypto.seal(
            content = sampleContent,
            examId = "exam-bio-fikih-01",
            title = "Biologi & Fikih Lingkungan - Semester Ganjil",
            startAt = System.currentTimeMillis() - 3600_000L,
            endAt = System.currentTimeMillis() + 7200_000L,
            token = "MN3-9X4",
            saltB64 = "c2FsdHNhbHRzYWx0MTIzNA==",
            iterations = 1000
        )
    }

    override suspend fun login(nisn: String, password: String): LoginResult {
        if (password == "wrong") throw ApiException(401, "Password salah")
        return LoginResult(
            sessionToken = "fake-token-$nisn",
            nisn = nisn,
            name = if (nisn == "212210045") "Ahmad Fauzan Al-Ghifari" else "Siswa MAN 3"
        )
    }

    override suspend fun listExams(): List<ExamSummary> {
        return listOf(
            ExamSummary(
                examId = "exam-bio-fikih-01",
                title = "Biologi & Fikih Lingkungan",
                version = 1,
                startAt = System.currentTimeMillis() - 3600_000L,
                endAt = System.currentTimeMillis() + 7200_000L
            )
        )
    }

    override suspend fun downloadPackage(examId: String): Envelope = sampleEnvelope

    override suspend fun sync(batch: List<SyncItem>): SyncResult {
        if (shouldThrow409) throw DeviceMismatchException()
        if (shouldFailSync) throw ApiException(500, "Server error")
        receivedSyncItems.addAll(batch)
        return SyncResult(acceptedIds = batch.map { it.id })
    }

    override suspend fun serverTime(): Long = currentTimeMs

    override suspend fun ready(examId: String, version: Int) {
        readyCount++
    }

    override suspend fun heartbeat(examId: String, answeredCount: Int): Long = currentTimeMs

    override suspend fun myAnswers(examId: String): RemoteAnswers {
        return RemoteAnswers(answers = emptyList(), submittedAt = null)
    }
}

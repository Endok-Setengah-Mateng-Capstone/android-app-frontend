package id.sch.man3merangin.ujian.data.db

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MigrationTest {
    @get:Rule val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test fun migration1To2KeepsRows() {
        helper.createDatabase("mig.db", 1).apply {
            execSQL("INSERT INTO sync_outbox (id, kind, payloadJson, createdAt, attempts) VALUES ('x','ANSWER','{}',5,2)")
            close()
        }
        val db = helper.runMigrationsAndValidate("mig.db", 2, true, AppDatabase.MIGRATION_1_2)
        db.query("SELECT id, nisn, attempts FROM sync_outbox").use {
            assertEquals(1, it.count)
            it.moveToFirst()
            assertEquals("x", it.getString(0))
            assertEquals("", it.getString(1))
            assertEquals(2, it.getInt(2))
        }
    }
}

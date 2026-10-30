package com.rsalanga.roadmap.shared.data.local.dao

import androidx.sqlite.SQLiteException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rsalanga.roadmap.shared.data.local.database.AppDatabase
import com.rsalanga.roadmap.shared.data.local.entity.PlanEntity
import com.rsalanga.roadmap.shared.models.Occurrence
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlanDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: PlanDao

    private val plan = PlanEntity(
        id = 1,
        name = "Kotlin",
        occurrence = Occurrence.WEEK,
        commitmentPerOccurrence = 5.hours,
        preferredTime = LocalTime(hour = 9, minute = 30),
        sessionLength = 45.minutes
    )

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        dao = database.getPlanDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_storesConvertedColumns() = runTest {
        dao.insert(plan)

        val row = database.queryRows(
            "SELECT id, name, occurrence, commitmentPerOccurrence, preferredTime, sessionLength FROM PlanEntity"
        ) {
            listOf(
                it.getLong(0),
                it.getText(1),
                it.getText(2),
                it.getLong(3),
                it.getText(4),
                it.getLong(5)
            )
        }.single()

        assertEquals(
            listOf(
                1L,
                "Kotlin",
                "WEEK",
                5.hours.inWholeMilliseconds,
                "09:30",
                45.minutes.inWholeMilliseconds
            ),
            row
        )
    }

    @Test
    fun insert_withoutPreferredTime_storesNull() = runTest {
        dao.insert(plan.copy(preferredTime = null))

        val preferredTime = database.queryRows("SELECT preferredTime FROM PlanEntity") {
            if (it.isNull(0)) null else it.getText(0)
        }.single()

        assertNull(preferredTime)
    }

    @Test
    fun insert_withDuplicateId_throwsAndKeepsOriginal() = runTest {
        dao.insert(plan)

        assertThrows(SQLiteException::class.java) {
            runBlocking { dao.insert(plan.copy(name = "Swift")) }
        }

        assertEquals(1L, database.count("PlanEntity"))
        assertEquals(
            listOf("Kotlin"),
            database.queryRows("SELECT name FROM PlanEntity") {
                it.getText(0)
            }
        )
    }
}

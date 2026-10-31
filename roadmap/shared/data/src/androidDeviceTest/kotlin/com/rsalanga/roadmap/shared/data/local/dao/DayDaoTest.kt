package com.rsalanga.roadmap.shared.data.local.dao

import androidx.sqlite.SQLiteException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rsalanga.roadmap.shared.data.local.database.AppDatabase
import com.rsalanga.roadmap.shared.data.local.entity.DayEntity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DayDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: DayDao

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        dao = database.getDayDao()
        runBlocking {
            database.insertPlans(10, 20)
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_storesDayOfWeekAsIsoDayNumber() = runTest {
        dao.insert(DayEntity(id = 1, planId = 10, dayOfWeek = DayOfWeek.MONDAY))
        dao.insert(DayEntity(id = 2, planId = 10, dayOfWeek = DayOfWeek.SUNDAY))

        val rows = database.queryRows(
            "SELECT ${DayEntity.ID}, ${DayEntity.PLAN_ID}, ${DayEntity.DAY_OF_WEEK} " +
                "FROM ${DayEntity.TABLE_NAME} ORDER BY ${DayEntity.ID}"
        ) {
            Triple(it.getLong(0), it.getLong(1), it.getLong(2))
        }

        assertEquals(listOf(Triple(1L, 10L, 1L), Triple(2L, 10L, 7L)), rows)
    }

    @Test
    fun insert_withDuplicateId_throwsAndKeepsOriginal() = runTest {
        dao.insert(DayEntity(id = 1, planId = 10, dayOfWeek = DayOfWeek.MONDAY))

        assertThrows(SQLiteException::class.java) {
            runBlocking { dao.insert(DayEntity(id = 1, planId = 20, dayOfWeek = DayOfWeek.FRIDAY)) }
        }

        assertEquals(1L, database.count(DayEntity.TABLE_NAME))
        assertEquals(
            listOf(10L),
            database.queryRows("SELECT ${DayEntity.PLAN_ID} FROM ${DayEntity.TABLE_NAME}") {
                it.getLong(0)
            }
        )
    }

    @Test
    fun insert_withDuplicatePlanAndDay_throwsAndKeepsOriginal() = runTest {
        dao.insert(DayEntity(id = 1, planId = 10, dayOfWeek = DayOfWeek.MONDAY))

        assertThrows(SQLiteException::class.java) {
            runBlocking { dao.insert(DayEntity(id = 2, planId = 10, dayOfWeek = DayOfWeek.MONDAY)) }
        }

        assertEquals(
            listOf(1L),
            database.queryRows("SELECT ${DayEntity.ID} FROM ${DayEntity.TABLE_NAME}") {
                it.getLong(0)
            }
        )
    }

    @Test
    fun insert_withSameDayInDifferentPlans_storesBoth() = runTest {
        dao.insert(DayEntity(id = 1, planId = 10, dayOfWeek = DayOfWeek.MONDAY))
        dao.insert(DayEntity(id = 2, planId = 20, dayOfWeek = DayOfWeek.MONDAY))

        assertEquals(2L, database.count(DayEntity.TABLE_NAME))
    }
}

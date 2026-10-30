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
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_storesDayOfWeekAsIsoDayNumber() = runTest {
        dao.insert(DayEntity(id = 1, planId = 10, dayOfWeek = DayOfWeek.MONDAY))
        dao.insert(DayEntity(id = 2, planId = 10, dayOfWeek = DayOfWeek.SUNDAY))

        val rows = database.queryRows("SELECT id, planId, dayOfWeek FROM DayEntity ORDER BY id") {
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

        assertEquals(1L, database.count("DayEntity"))
        assertEquals(
            listOf(10L),
            database.queryRows("SELECT planId FROM DayEntity") {
                it.getLong(0)
            }
        )
    }
}

package com.rsalanga.roadmap.shared.data.local.dao

import androidx.sqlite.SQLiteException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rsalanga.roadmap.shared.data.local.database.AppDatabase
import com.rsalanga.roadmap.shared.data.local.entity.PlanDayEntity
import kotlinx.coroutines.flow.first
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
class PlanDayDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: PlanDayDao

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        dao = database.getPlanDayDao()
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
        dao.insert(PlanDayEntity(id = 1, planId = 10, dayOfWeek = DayOfWeek.MONDAY))
        dao.insert(PlanDayEntity(id = 2, planId = 10, dayOfWeek = DayOfWeek.SUNDAY))

        val rows = database.queryRows(
            "SELECT ${PlanDayEntity.ID}, ${PlanDayEntity.PLAN_ID}, ${PlanDayEntity.DAY_OF_WEEK} " +
                "FROM ${PlanDayEntity.TABLE_NAME} ORDER BY ${PlanDayEntity.ID}"
        ) {
            Triple(it.getLong(0), it.getLong(1), it.getLong(2))
        }

        assertEquals(listOf(Triple(1L, 10L, 1L), Triple(2L, 10L, 7L)), rows)
    }

    @Test
    fun insert_withDuplicateId_throwsAndKeepsOriginal() = runTest {
        dao.insert(PlanDayEntity(id = 1, planId = 10, dayOfWeek = DayOfWeek.MONDAY))

        assertThrows(SQLiteException::class.java) {
            runBlocking {
                dao.insert(PlanDayEntity(id = 1, planId = 20, dayOfWeek = DayOfWeek.FRIDAY))
            }
        }

        assertEquals(1L, database.count(PlanDayEntity.TABLE_NAME))
        assertEquals(
            listOf(10L),
            database.queryRows("SELECT ${PlanDayEntity.PLAN_ID} FROM ${PlanDayEntity.TABLE_NAME}") {
                it.getLong(0)
            }
        )
    }

    @Test
    fun insert_withDuplicatePlanAndDay_throwsAndKeepsOriginal() = runTest {
        dao.insert(PlanDayEntity(id = 1, planId = 10, dayOfWeek = DayOfWeek.MONDAY))

        assertThrows(SQLiteException::class.java) {
            runBlocking {
                dao.insert(PlanDayEntity(id = 2, planId = 10, dayOfWeek = DayOfWeek.MONDAY))
            }
        }

        assertEquals(
            listOf(1L),
            database.queryRows("SELECT ${PlanDayEntity.ID} FROM ${PlanDayEntity.TABLE_NAME}") {
                it.getLong(0)
            }
        )
    }

    @Test
    fun insert_withSameDayInDifferentPlans_storesBoth() = runTest {
        dao.insert(PlanDayEntity(id = 1, planId = 10, dayOfWeek = DayOfWeek.MONDAY))
        dao.insert(PlanDayEntity(id = 2, planId = 20, dayOfWeek = DayOfWeek.MONDAY))

        assertEquals(2L, database.count(PlanDayEntity.TABLE_NAME))
    }

    @Test
    fun getByPlanId_returnsOnlyThatPlansDays() = runTest {
        val monday = PlanDayEntity(id = 1, planId = 10, dayOfWeek = DayOfWeek.MONDAY)
        dao.insert(monday)
        dao.insert(PlanDayEntity(id = 2, planId = 20, dayOfWeek = DayOfWeek.FRIDAY))

        assertEquals(listOf(monday), dao.getByPlanId(10).first())
    }

    @Test
    fun deleteById_removesOnlyMatchingRow() = runTest {
        dao.insert(PlanDayEntity(id = 1, planId = 10, dayOfWeek = DayOfWeek.MONDAY))
        dao.insert(PlanDayEntity(id = 2, planId = 10, dayOfWeek = DayOfWeek.FRIDAY))

        assertEquals(1, dao.deleteById(1))
        assertEquals(0, dao.deleteById(1))
        assertEquals(listOf(2L), dao.getByPlanId(10).first().map { it.id })
    }

    @Test
    fun deleteByPlanId_removesOnlyThatPlansDays() = runTest {
        dao.insert(PlanDayEntity(id = 1, planId = 10, dayOfWeek = DayOfWeek.MONDAY))
        dao.insert(PlanDayEntity(id = 2, planId = 10, dayOfWeek = DayOfWeek.FRIDAY))
        dao.insert(PlanDayEntity(id = 3, planId = 20, dayOfWeek = DayOfWeek.MONDAY))

        assertEquals(2, dao.deleteByPlanId(10))
        assertEquals(emptyList<PlanDayEntity>(), dao.getByPlanId(10).first())
        assertEquals(1, dao.getByPlanId(20).first().size)
    }
}

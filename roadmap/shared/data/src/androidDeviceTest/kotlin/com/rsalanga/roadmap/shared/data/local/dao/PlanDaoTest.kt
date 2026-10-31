package com.rsalanga.roadmap.shared.data.local.dao

import androidx.sqlite.SQLiteException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rsalanga.roadmap.shared.data.local.database.AppDatabase
import com.rsalanga.roadmap.shared.data.local.entity.PlanEntity
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
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
        occurrence = PlanEntity.Occurrence.WEEK,
        commitmentPerOccurrence = 5.hours
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
            "SELECT ${PlanEntity.ID}, ${PlanEntity.NAME}, ${PlanEntity.OCCURRENCE}, " +
                "${PlanEntity.COMMITMENT_PER_OCCURRENCE} FROM ${PlanEntity.TABLE_NAME}"
        ) {
            listOf(it.getLong(0), it.getText(1), it.getText(2), it.getLong(3))
        }.single()

        assertEquals(
            listOf(1L, "Kotlin", "WEEK", 5.hours.inWholeMilliseconds),
            row
        )
    }

    @Test
    fun insert_withDefaultId_autoGeneratesId() = runTest {
        dao.insert(plan.copy(id = 0))

        assertEquals(
            listOf(1L),
            database.queryRows("SELECT ${PlanEntity.ID} FROM ${PlanEntity.TABLE_NAME}") {
                it.getLong(0)
            }
        )
    }

    @Test
    fun deletePlan_removesOnlyMatchingRow() = runTest {
        dao.insert(plan)
        dao.insert(plan.copy(id = 2, name = "Swift"))

        dao.deleteById(1)

        assertEquals(
            listOf("Swift"),
            database.queryRows("SELECT ${PlanEntity.NAME} FROM ${PlanEntity.TABLE_NAME}") {
                it.getText(0)
            }
        )
    }

    @Test
    fun insert_withDuplicateId_throwsAndKeepsOriginal() = runTest {
        dao.insert(plan)

        assertThrows(SQLiteException::class.java) {
            runBlocking { dao.insert(plan.copy(name = "Swift")) }
        }

        assertEquals(1L, database.count(PlanEntity.TABLE_NAME))
        assertEquals(
            listOf("Kotlin"),
            database.queryRows("SELECT ${PlanEntity.NAME} FROM ${PlanEntity.TABLE_NAME}") {
                it.getText(0)
            }
        )
    }

    @Test
    fun getById_returnsMatchingPlan() = runTest {
        dao.insert(plan)
        dao.insert(plan.copy(id = 2, name = "Swift"))

        assertEquals(plan, dao.getById(1).first())
    }

    @Test
    fun getById_afterDelete_emitsNull() = runTest {
        dao.insert(plan)

        dao.deleteById(1)

        assertNull(dao.getById(1).first())
    }

    @Test
    fun deleteById_returnsAffectedRowCount() = runTest {
        dao.insert(plan)

        assertEquals(1, dao.deleteById(1))
        assertEquals(0, dao.deleteById(1))
    }
}

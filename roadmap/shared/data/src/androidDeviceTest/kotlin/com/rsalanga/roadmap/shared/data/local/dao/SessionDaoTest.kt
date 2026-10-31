package com.rsalanga.roadmap.shared.data.local.dao

import androidx.sqlite.SQLiteException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rsalanga.roadmap.shared.data.local.database.AppDatabase
import com.rsalanga.roadmap.shared.data.local.entity.SessionEntity
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
class SessionDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: SessionDao

    private val session = SessionEntity(
        id = 1,
        planId = 10,
        preferredTime = LocalTime(hour = 9, minute = 30),
        sessionLength = 45.minutes
    )

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        dao = database.getSessionDao()
        runBlocking {
            database.insertPlans(10)
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_storesConvertedColumns() = runTest {
        dao.insert(session)

        val row = database.queryRows(
            "SELECT ${SessionEntity.ID}, ${SessionEntity.PLAN_ID}, " +
                "${SessionEntity.PREFERRED_TIME}, ${SessionEntity.SESSION_LENGTH} " +
                "FROM ${SessionEntity.TABLE_NAME}"
        ) {
            listOf(it.getLong(0), it.getLong(1), it.getText(2), it.getLong(3))
        }.single()

        assertEquals(listOf(1L, 10L, "09:30", 45.minutes.inWholeMilliseconds), row)
    }

    @Test
    fun insert_withoutPreferredTime_storesNull() = runTest {
        dao.insert(session.copy(preferredTime = null))

        val preferredTime = database.queryRows(
            "SELECT ${SessionEntity.PREFERRED_TIME} FROM ${SessionEntity.TABLE_NAME}"
        ) {
            if (it.isNull(0)) null else it.getText(0)
        }.single()

        assertNull(preferredTime)
    }

    @Test
    fun insert_withSecondSessionForPlan_throwsAndKeepsOriginal() = runTest {
        dao.insert(session)

        assertThrows(SQLiteException::class.java) {
            runBlocking { dao.insert(session.copy(id = 2, sessionLength = 30.minutes)) }
        }

        assertEquals(1L, database.count(SessionEntity.TABLE_NAME))
    }
}

package com.rsalanga.roadmap.shared.data.local.dao

import androidx.sqlite.SQLiteException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rsalanga.roadmap.shared.data.local.database.AppDatabase
import com.rsalanga.roadmap.shared.data.local.entity.TopicEntity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TopicDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: TopicDao

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        dao = database.getTopicDao()
        runBlocking {
            database.insertPlans(10, 20)
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_storesTopicWithStatusName() = runTest {
        dao.insert(
            TopicEntity(
                id = 1,
                planId = 10,
                status = TopicEntity.Status.PENDING,
                name = "Coroutines"
            )
        )
        dao.insert(
            TopicEntity(id = 2, planId = 10, status = TopicEntity.Status.DONE, name = "Flows")
        )

        val rows = database.queryRows(
            "SELECT ${TopicEntity.ID}, ${TopicEntity.PLAN_ID}, ${TopicEntity.STATUS}, " +
                "${TopicEntity.NAME} FROM ${TopicEntity.TABLE_NAME} ORDER BY ${TopicEntity.ID}"
        ) {
            listOf(it.getLong(0), it.getLong(1), it.getText(2), it.getText(3))
        }

        assertEquals(
            listOf(
                listOf(1L, 10L, "PENDING", "Coroutines"),
                listOf(2L, 10L, "DONE", "Flows")
            ),
            rows
        )
    }

    @Test
    fun insert_withDuplicateId_throwsAndKeepsOriginal() = runTest {
        dao.insert(
            TopicEntity(
                id = 1,
                planId = 10,
                status = TopicEntity.Status.PENDING,
                name = "Coroutines"
            )
        )

        assertThrows(SQLiteException::class.java) {
            runBlocking {
                dao.insert(
                    TopicEntity(
                        id = 1,
                        planId = 20,
                        status = TopicEntity.Status.DONE,
                        name = "Other"
                    )
                )
            }
        }

        assertEquals(1L, database.count(TopicEntity.TABLE_NAME))
        assertEquals(
            listOf("Coroutines"),
            database.queryRows("SELECT ${TopicEntity.NAME} FROM ${TopicEntity.TABLE_NAME}") {
                it.getText(0)
            }
        )
    }
}

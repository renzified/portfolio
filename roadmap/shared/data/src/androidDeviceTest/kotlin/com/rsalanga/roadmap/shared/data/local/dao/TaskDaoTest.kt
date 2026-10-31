package com.rsalanga.roadmap.shared.data.local.dao

import androidx.sqlite.SQLiteException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rsalanga.roadmap.shared.data.local.database.AppDatabase
import com.rsalanga.roadmap.shared.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: TaskDao

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        dao = database.getTaskDao()
        runBlocking {
            database.insertPlans(10)
            database.insertTopics(planId = 10, 10, 20)
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_storesTaskWithStatusName() = runTest {
        dao.insert(
            TaskEntity(
                id = 1,
                topicId = 10,
                status = TaskEntity.Status.PENDING,
                details = "Read docs"
            )
        )
        dao.insert(
            TaskEntity(
                id = 2,
                topicId = 10,
                status = TaskEntity.Status.DONE,
                details = "Write tests"
            )
        )

        val rows = database.queryRows(
            "SELECT ${TaskEntity.ID}, ${TaskEntity.TOPIC_ID}, ${TaskEntity.STATUS}, " +
                "${TaskEntity.DETAILS} FROM ${TaskEntity.TABLE_NAME} ORDER BY ${TaskEntity.ID}"
        ) {
            listOf(it.getLong(0), it.getLong(1), it.getText(2), it.getText(3))
        }

        assertEquals(
            listOf(
                listOf(1L, 10L, "PENDING", "Read docs"),
                listOf(2L, 10L, "DONE", "Write tests")
            ),
            rows
        )
    }

    @Test
    fun insert_withDuplicateId_throwsAndKeepsOriginal() = runTest {
        dao.insert(
            TaskEntity(
                id = 1,
                topicId = 10,
                status = TaskEntity.Status.PENDING,
                details = "Read docs"
            )
        )

        assertThrows(SQLiteException::class.java) {
            runBlocking {
                dao.insert(
                    TaskEntity(
                        id = 1,
                        topicId = 20,
                        status = TaskEntity.Status.DONE,
                        details = "Other"
                    )
                )
            }
        }

        assertEquals(1L, database.count(TaskEntity.TABLE_NAME))
        assertEquals(
            listOf("Read docs"),
            database.queryRows("SELECT ${TaskEntity.DETAILS} FROM ${TaskEntity.TABLE_NAME}") {
                it.getText(0)
            }
        )
    }

    @Test
    fun getByTopicId_returnsOnlyThatTopicsTasks() = runTest {
        val task =
            TaskEntity(id = 1, topicId = 10, status = TaskEntity.Status.PENDING, details = "Read")
        dao.insert(task)
        dao.insert(
            TaskEntity(id = 2, topicId = 20, status = TaskEntity.Status.PENDING, details = "Other")
        )

        assertEquals(listOf(task), dao.getByTopicId(10).first())
    }

    @Test
    fun updateStatus_changesOnlyMatchingTask() = runTest {
        dao.insert(
            TaskEntity(id = 1, topicId = 10, status = TaskEntity.Status.PENDING, details = "Read")
        )
        dao.insert(
            TaskEntity(id = 2, topicId = 10, status = TaskEntity.Status.PENDING, details = "Write")
        )

        assertEquals(1, dao.updateStatus(1, TaskEntity.Status.DONE))
        assertEquals(0, dao.updateStatus(99, TaskEntity.Status.DONE))
        assertEquals(
            listOf(TaskEntity.Status.DONE, TaskEntity.Status.PENDING),
            dao.getByTopicId(10).first().sortedBy { it.id }.map { it.status }
        )
    }

    @Test
    fun deleteById_returnsAffectedRowCount() = runTest {
        dao.insert(
            TaskEntity(id = 1, topicId = 10, status = TaskEntity.Status.PENDING, details = "Read")
        )

        assertEquals(1, dao.deleteById(1))
        assertEquals(0, dao.deleteById(1))
    }
}

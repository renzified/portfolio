package com.rsalanga.roadmap.shared.data.local.dao

import androidx.sqlite.SQLiteException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rsalanga.roadmap.shared.data.local.database.AppDatabase
import com.rsalanga.roadmap.shared.data.local.entity.TaskEntity
import com.rsalanga.roadmap.shared.models.TaskStatus
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
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_storesTaskWithStatusName() = runTest {
        dao.insert(
            TaskEntity(id = 1, topicId = 10, status = TaskStatus.PENDING, details = "Read docs")
        )
        dao.insert(
            TaskEntity(id = 2, topicId = 10, status = TaskStatus.DONE, details = "Write tests")
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
            TaskEntity(id = 1, topicId = 10, status = TaskStatus.PENDING, details = "Read docs")
        )

        assertThrows(SQLiteException::class.java) {
            runBlocking {
                dao.insert(
                    TaskEntity(id = 1, topicId = 20, status = TaskStatus.DONE, details = "Other")
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
}

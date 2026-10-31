package com.rsalanga.roadmap.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rsalanga.roadmap.shared.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(item: TaskEntity): Long

    @Query("SELECT * FROM ${TaskEntity.TABLE_NAME} WHERE ${TaskEntity.TOPIC_ID} = :topicId")
    fun getByTopicId(topicId: Long): Flow<List<TaskEntity>>

    @Query(
        "UPDATE ${TaskEntity.TABLE_NAME} SET ${TaskEntity.STATUS} = :status " +
            "WHERE ${TaskEntity.ID} = :id"
    )
    suspend fun updateStatus(id: Long, status: TaskEntity.Status): Int

    @Query("DELETE FROM ${TaskEntity.TABLE_NAME} WHERE ${TaskEntity.ID} = :id")
    suspend fun deleteById(id: Long): Int
}

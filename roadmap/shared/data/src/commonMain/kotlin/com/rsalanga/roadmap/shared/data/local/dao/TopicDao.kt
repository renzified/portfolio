package com.rsalanga.roadmap.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.rsalanga.roadmap.shared.data.local.entity.TopicEntity
import com.rsalanga.roadmap.shared.data.local.entity.TopicWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Insert
    suspend fun insert(item: TopicEntity): Long

    @Transaction
    @Query("SELECT * FROM ${TopicEntity.TABLE_NAME} WHERE ${TopicEntity.PLAN_ID} = :planId")
    fun getWithTasksByPlanId(planId: Long): Flow<List<TopicWithTasks>>

    @Query(
        "UPDATE ${TopicEntity.TABLE_NAME} SET ${TopicEntity.STATUS} = :status " +
            "WHERE ${TopicEntity.ID} = :id"
    )
    suspend fun updateStatus(id: Long, status: TopicEntity.Status): Int

    @Query("DELETE FROM ${TopicEntity.TABLE_NAME} WHERE ${TopicEntity.ID} = :id")
    suspend fun deleteById(id: Long): Int
}

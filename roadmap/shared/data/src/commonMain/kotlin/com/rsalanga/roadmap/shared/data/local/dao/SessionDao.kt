package com.rsalanga.roadmap.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rsalanga.roadmap.shared.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert
    suspend fun insert(item: SessionEntity): Long

    @Query("SELECT * FROM ${SessionEntity.TABLE_NAME} WHERE ${SessionEntity.PLAN_ID} = :planId")
    fun getByPlanId(planId: Long): Flow<SessionEntity?>

    @Update
    suspend fun update(item: SessionEntity): Int

    @Query("DELETE FROM ${SessionEntity.TABLE_NAME} WHERE ${SessionEntity.ID} = :id")
    suspend fun deleteById(id: Long): Int
}

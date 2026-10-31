package com.rsalanga.roadmap.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rsalanga.roadmap.shared.data.local.entity.PlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {

    @Insert
    suspend fun insert(item: PlanEntity)

    @Query("SELECT * FROM ${PlanEntity.TABLE_NAME}")
    fun getPlans(): Flow<List<PlanEntity>>

    @Query("DELETE FROM ${PlanEntity.TABLE_NAME} WHERE ${PlanEntity.ID} = :id")
    suspend fun deletePlan(id: Long): Int
}

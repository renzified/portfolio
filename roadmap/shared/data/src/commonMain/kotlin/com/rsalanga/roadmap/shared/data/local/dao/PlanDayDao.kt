package com.rsalanga.roadmap.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rsalanga.roadmap.shared.data.local.entity.PlanDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDayDao {

    @Insert
    suspend fun insert(item: PlanDayEntity): Long

    @Query("SELECT * FROM ${PlanDayEntity.TABLE_NAME} WHERE ${PlanDayEntity.PLAN_ID} = :planId")
    fun getByPlanId(planId: Long): Flow<List<PlanDayEntity>>

    @Query("DELETE FROM ${PlanDayEntity.TABLE_NAME} WHERE ${PlanDayEntity.ID} = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM ${PlanDayEntity.TABLE_NAME} WHERE ${PlanDayEntity.PLAN_ID} = :planId")
    suspend fun deleteByPlanId(planId: Long): Int
}

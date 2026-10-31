package com.rsalanga.roadmap.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rsalanga.roadmap.shared.data.local.entity.PlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {

    @Insert
    suspend fun insert(item: PlanEntity): Long

    @Query("SELECT * FROM ${PlanEntity.TABLE_NAME}")
    fun getAll(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM ${PlanEntity.TABLE_NAME} WHERE ${PlanEntity.ID} = :id")
    fun getById(id: Long): Flow<PlanEntity?>

    @Query("DELETE FROM ${PlanEntity.TABLE_NAME} WHERE ${PlanEntity.ID} = :id")
    suspend fun deleteById(id: Long): Int
}

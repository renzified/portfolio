package com.rsalanga.roadmap.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.rsalanga.roadmap.shared.data.local.entity.TaskEntity

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(item: TaskEntity)
}

package com.rsalanga.roadmap.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.rsalanga.roadmap.shared.data.local.entity.DayEntity

@Dao
interface DayDao {

    @Insert
    suspend fun insert(item: DayEntity)
}

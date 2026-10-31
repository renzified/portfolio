package com.rsalanga.roadmap.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.rsalanga.roadmap.shared.data.local.entity.SessionEntity

@Dao
interface SessionDao {

    @Insert
    suspend fun insert(item: SessionEntity)
}

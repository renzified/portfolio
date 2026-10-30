package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.DayOfWeek

@Entity(
    tableName = DayEntity.TABLE_NAME,
    indices = [Index(value = [DayEntity.PLAN_ID, DayEntity.DAY_OF_WEEK], unique = true)]
)
data class DayEntity(
    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = PLAN_ID) val planId: Long,
    @ColumnInfo(name = DAY_OF_WEEK) val dayOfWeek: DayOfWeek
) {
    companion object {
        const val TABLE_NAME = "DayEntity"
        const val ID = "id"
        const val PLAN_ID = "planId"
        const val DAY_OF_WEEK = "dayOfWeek"
    }
}

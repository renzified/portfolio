package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.DayOfWeek

@Entity(
    tableName = DayEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = PlanEntity::class,
            parentColumns = [PlanEntity.ID],
            childColumns = [DayEntity.PLAN_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [DayEntity.PLAN_ID, DayEntity.DAY_OF_WEEK], unique = true)]
)
data class DayEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) val id: Long = 0,
    @ColumnInfo(name = PLAN_ID) val planId: Long,
    @ColumnInfo(name = DAY_OF_WEEK) val dayOfWeek: DayOfWeek
) {
    companion object {
        const val TABLE_NAME = "day"
        const val ID = "id"
        const val PLAN_ID = "plan_id"
        const val DAY_OF_WEEK = "day_of_week"
    }
}

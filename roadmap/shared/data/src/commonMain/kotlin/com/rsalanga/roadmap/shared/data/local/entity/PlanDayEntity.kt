package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.DayOfWeek

@Entity(
    tableName = PlanDayEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = PlanEntity::class,
            parentColumns = [PlanEntity.ID],
            childColumns = [PlanDayEntity.PLAN_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [PlanDayEntity.PLAN_ID, PlanDayEntity.DAY_OF_WEEK], unique = true)]
)
data class PlanDayEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) val id: Long = 0,
    @ColumnInfo(name = PLAN_ID) val planId: Long,
    @ColumnInfo(name = DAY_OF_WEEK) val dayOfWeek: DayOfWeek
) {
    companion object {
        const val TABLE_NAME = "plan_day"
        const val ID = "id"
        const val PLAN_ID = "plan_id"
        const val DAY_OF_WEEK = "day_of_week"
    }
}

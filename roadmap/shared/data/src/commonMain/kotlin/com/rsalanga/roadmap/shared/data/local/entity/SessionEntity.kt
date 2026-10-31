package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Duration
import kotlinx.datetime.LocalTime

@Entity(
    tableName = SessionEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = PlanEntity::class,
            parentColumns = [PlanEntity.ID],
            childColumns = [SessionEntity.PLAN_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [SessionEntity.PLAN_ID], unique = true)]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) val id: Long = 0,
    @ColumnInfo(name = PLAN_ID) val planId: Long,
    @ColumnInfo(name = PREFERRED_TIME) val preferredTime: LocalTime?,
    @ColumnInfo(name = SESSION_LENGTH) val sessionLength: Duration
) {
    companion object {
        const val TABLE_NAME = "session"
        const val ID = "id"
        const val PLAN_ID = "plan_id"
        const val PREFERRED_TIME = "preferred_time"
        const val SESSION_LENGTH = "session_length"
    }
}

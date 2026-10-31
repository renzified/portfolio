package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = TopicEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = PlanEntity::class,
            parentColumns = [PlanEntity.ID],
            childColumns = [TopicEntity.PLAN_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [TopicEntity.PLAN_ID])]
)
data class TopicEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) val id: Long = 0,
    @ColumnInfo(name = PLAN_ID) val planId: Long,
    @ColumnInfo(name = STATUS) val status: Status,
    @ColumnInfo(name = NAME) val name: String
) {
    companion object {
        const val TABLE_NAME = "topic"
        const val ID = "id"
        const val PLAN_ID = "plan_id"
        const val STATUS = "status"
        const val NAME = "name"
    }

    enum class Status {
        PENDING,
        DONE
    }
}

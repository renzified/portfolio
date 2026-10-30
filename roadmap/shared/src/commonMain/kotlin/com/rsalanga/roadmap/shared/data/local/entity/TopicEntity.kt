package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.rsalanga.roadmap.shared.models.TopicStatus

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
    ]
)
data class TopicEntity(
    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = PLAN_ID) val planId: Long,
    @ColumnInfo(name = STATUS) val status: TopicStatus,
    @ColumnInfo(name = NAME) val name: String
) {
    companion object {
        const val TABLE_NAME = "TopicEntity"
        const val ID = "id"
        const val PLAN_ID = "planId"
        const val STATUS = "status"
        const val NAME = "name"
    }
}

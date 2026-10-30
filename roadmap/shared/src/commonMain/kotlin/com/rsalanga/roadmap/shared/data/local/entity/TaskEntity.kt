package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.rsalanga.roadmap.shared.models.TaskStatus

@Entity(
    tableName = TaskEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = [TopicEntity.ID],
            childColumns = [TaskEntity.TOPIC_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class TaskEntity(
    @PrimaryKey @ColumnInfo(name = ID) val id: Long,
    @ColumnInfo(name = TOPIC_ID) val topicId: Long,
    @ColumnInfo(name = STATUS) val status: TaskStatus,
    @ColumnInfo(name = DETAILS) val details: String
) {
    companion object {
        const val TABLE_NAME = "TaskEntity"
        const val ID = "id"
        const val TOPIC_ID = "topicId"
        const val STATUS = "status"
        const val DETAILS = "details"
    }
}

package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    ],
    indices = [Index(value = [TaskEntity.TOPIC_ID])]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) val id: Long = 0,
    @ColumnInfo(name = TOPIC_ID) val topicId: Long,
    @ColumnInfo(name = STATUS) val status: Status,
    @ColumnInfo(name = DETAILS) val details: String
) {
    companion object {
        const val TABLE_NAME = "task"
        const val ID = "id"
        const val TOPIC_ID = "topic_id"
        const val STATUS = "status"
        const val DETAILS = "details"
    }

    enum class Status {
        PENDING,
        DONE
    }
}

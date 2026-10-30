package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rsalanga.roadmap.shared.models.TaskStatus

@Entity
data class TaskEntity(
    @PrimaryKey val id: Long,
    val topicId: Long,
    val status: TaskStatus,
    val details: String
)

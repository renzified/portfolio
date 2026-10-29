package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.Entity
import com.rsalanga.roadmap.shared.models.TaskStatus

@Entity
data class TaskEntity(val id: Long, val topicId: Long, val status: TaskStatus, val details: String)

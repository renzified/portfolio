package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TopicWithTasks(
    @Embedded val topic: TopicEntity,
    @Relation(parentColumn = TopicEntity.ID, entityColumn = TaskEntity.TOPIC_ID)
    val tasks: List<TaskEntity>
)

package com.rsalanga.roadmap.shared.domain.plan.model

data class CreateTask(
    val topicId: Topic.TopicId,
    val details: String,
    val status: TaskStatus = TaskStatus.PENDING
)

package com.rsalanga.roadmap.shared.models

import kotlin.uuid.Uuid

data class Topic(
    val studyPlan: Uuid,
    val status: TopicStatus,
    val name: String,
    val tasks: List<Task>
)

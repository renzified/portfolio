package com.rsalanga.roadmap.shared.models

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.Uuid

data class Event(
    val id: Uuid,
    val start: LocalDateTime,
    val finish: LocalDateTime,
    val topicId: Uuid,
    val taskId: Uuid
)

package com.rsalanga.roadmap.shared.models

import kotlin.uuid.Uuid
import kotlinx.datetime.LocalDateTime

data class Event(
    val id: Uuid,
    val start: LocalDateTime,
    val finish: LocalDateTime,
    val topicId: Uuid,
    val taskId: Uuid
)

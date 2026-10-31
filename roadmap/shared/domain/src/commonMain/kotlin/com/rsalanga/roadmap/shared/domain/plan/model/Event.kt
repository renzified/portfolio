package com.rsalanga.roadmap.shared.domain.plan.model

import kotlinx.datetime.LocalDateTime

data class Event(
    val id: Long,
    val start: LocalDateTime,
    val finish: LocalDateTime,
    val topicId: Long,
    val taskId: Long
)

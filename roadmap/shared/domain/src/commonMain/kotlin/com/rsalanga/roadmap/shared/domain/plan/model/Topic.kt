package com.rsalanga.roadmap.shared.domain.plan.model

import kotlin.jvm.JvmInline

data class Topic(
    val id: TopicId,
    val status: TopicStatus,
    val name: String,
    val tasks: List<Task>
) {

    @JvmInline
    value class TopicId(val value: Long)
}

package com.rsalanga.roadmap.shared.domain.plan.model

import kotlin.jvm.JvmInline

data class Task(val id: TaskId, val status: TaskStatus, val details: String) {

    @JvmInline
    value class TaskId(val value: Long)
}

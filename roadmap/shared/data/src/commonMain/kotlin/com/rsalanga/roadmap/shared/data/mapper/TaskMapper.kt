package com.rsalanga.roadmap.shared.data.mapper

import com.rsalanga.roadmap.shared.data.local.entity.TaskEntity
import com.rsalanga.roadmap.shared.domain.plan.model.Task
import com.rsalanga.roadmap.shared.domain.plan.model.TaskStatus

internal fun TaskEntity.Status.toDomain() = when (this) {
    TaskEntity.Status.PENDING -> TaskStatus.PENDING
    TaskEntity.Status.DONE -> TaskStatus.DONE
}

internal fun TaskEntity.toDomain() = Task(
    id = Task.TaskId(id),
    status = status.toDomain(),
    details = details
)

internal fun TaskStatus.toEntity() = when (this) {
    TaskStatus.PENDING -> TaskEntity.Status.PENDING
    TaskStatus.DONE -> TaskEntity.Status.DONE
}

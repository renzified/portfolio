package com.rsalanga.roadmap.shared.data.mapper

import com.rsalanga.roadmap.shared.data.local.entity.TopicEntity
import com.rsalanga.roadmap.shared.domain.plan.model.Task
import com.rsalanga.roadmap.shared.domain.plan.model.Topic
import com.rsalanga.roadmap.shared.domain.plan.model.TopicStatus

internal fun TopicEntity.Status.toDomain() = when (this) {
    TopicEntity.Status.PENDING -> TopicStatus.PENDING
    TopicEntity.Status.DONE -> TopicStatus.DONE
}

internal fun TopicEntity.toDomain(tasks: List<Task>) = Topic(
    id = id,
    status = status.toDomain(),
    name = name,
    tasks = tasks
)

internal fun TopicStatus.toEntity() = when (this) {
    TopicStatus.PENDING -> TopicEntity.Status.PENDING
    TopicStatus.DONE -> TopicEntity.Status.DONE
}

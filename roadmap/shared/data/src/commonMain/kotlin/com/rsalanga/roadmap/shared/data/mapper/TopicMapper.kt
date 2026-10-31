package com.rsalanga.roadmap.shared.data.mapper

import com.rsalanga.roadmap.shared.data.local.entity.TopicEntity
import com.rsalanga.roadmap.shared.data.local.entity.TopicWithTasks
import com.rsalanga.roadmap.shared.domain.plan.model.CreateTopic
import com.rsalanga.roadmap.shared.domain.plan.model.Task
import com.rsalanga.roadmap.shared.domain.plan.model.Topic
import com.rsalanga.roadmap.shared.domain.plan.model.TopicStatus

internal fun TopicEntity.Status.toDomain() = when (this) {
    TopicEntity.Status.PENDING -> TopicStatus.PENDING
    TopicEntity.Status.DONE -> TopicStatus.DONE
}

internal fun TopicEntity.toDomain(tasks: List<Task>) = Topic(
    id = Topic.TopicId(id),
    status = status.toDomain(),
    name = name,
    tasks = tasks
)

internal fun TopicWithTasks.toDomain() = topic.toDomain(tasks.map { it.toDomain() })

internal fun TopicStatus.toEntity() = when (this) {
    TopicStatus.PENDING -> TopicEntity.Status.PENDING
    TopicStatus.DONE -> TopicEntity.Status.DONE
}

internal fun CreateTopic.toEntity() = TopicEntity(
    planId = planId.value,
    status = status.toEntity(),
    name = name
)

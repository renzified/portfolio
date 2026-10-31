package com.rsalanga.roadmap.shared.data.mapper

import com.rsalanga.roadmap.shared.data.local.entity.SessionEntity
import com.rsalanga.roadmap.shared.domain.plan.model.CreateSession
import com.rsalanga.roadmap.shared.domain.plan.model.Plan
import com.rsalanga.roadmap.shared.domain.plan.model.Session

internal fun SessionEntity.toDomain() = Session(
    id = Session.SessionId(id),
    planId = Plan.PlanId(planId),
    preferredTime = preferredTime,
    sessionLength = sessionLength
)

internal fun Session.toEntity() = SessionEntity(
    id = id.value,
    planId = planId.value,
    preferredTime = preferredTime,
    sessionLength = sessionLength
)

internal fun CreateSession.toEntity() = SessionEntity(
    planId = planId.value,
    preferredTime = preferredTime,
    sessionLength = sessionLength
)

package com.rsalanga.roadmap.shared.data.mapper

import com.rsalanga.roadmap.shared.data.local.entity.PlanDayEntity
import com.rsalanga.roadmap.shared.domain.plan.model.CreatePlanDay
import com.rsalanga.roadmap.shared.domain.plan.model.Plan
import com.rsalanga.roadmap.shared.domain.plan.model.PlanDay

internal fun PlanDayEntity.toDomain() = PlanDay(
    id = PlanDay.PlanDayId(id),
    planId = Plan.PlanId(planId),
    dayOfWeek = dayOfWeek
)

internal fun CreatePlanDay.toEntity() = PlanDayEntity(
    planId = planId.value,
    dayOfWeek = dayOfWeek
)

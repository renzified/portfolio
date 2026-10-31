package com.rsalanga.roadmap.shared.data.mapper

import com.rsalanga.roadmap.shared.data.local.entity.PlanEntity
import com.rsalanga.roadmap.shared.domain.plan.model.CreatePlan
import com.rsalanga.roadmap.shared.domain.plan.model.Occurrence
import com.rsalanga.roadmap.shared.domain.plan.model.Plan

internal fun PlanEntity.Occurrence.toDomain() = when (this) {
    PlanEntity.Occurrence.DAY -> Occurrence.DAY
    PlanEntity.Occurrence.WEEK -> Occurrence.WEEK
    PlanEntity.Occurrence.MONTH -> Occurrence.MONTH
}

internal fun PlanEntity.toDomain() = Plan(
    id = id,
    name = name,
    occurrence = occurrence.toDomain(),
    commitmentPerOccurrence = commitmentPerOccurrence
)

internal fun Occurrence.toEntity() = when (this) {
    Occurrence.DAY -> PlanEntity.Occurrence.DAY
    Occurrence.WEEK -> PlanEntity.Occurrence.WEEK
    Occurrence.MONTH -> PlanEntity.Occurrence.MONTH
}

internal fun CreatePlan.toEntity() = PlanEntity(
    name = name,
    occurrence = occurrence.toEntity(),
    commitmentPerOccurrence = commitmentPerOccurrence
)

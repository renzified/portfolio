package com.rsalanga.roadmap.shared.domain.plan.model

import kotlinx.datetime.DayOfWeek

data class CreatePlanDay(val planId: Plan.PlanId, val dayOfWeek: DayOfWeek)

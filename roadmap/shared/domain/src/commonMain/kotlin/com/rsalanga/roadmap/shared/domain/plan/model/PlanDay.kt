package com.rsalanga.roadmap.shared.domain.plan.model

import kotlin.jvm.JvmInline
import kotlinx.datetime.DayOfWeek

data class PlanDay(val id: PlanDayId, val planId: Plan.PlanId, val dayOfWeek: DayOfWeek) {

    @JvmInline
    value class PlanDayId(val value: Long)
}

package com.rsalanga.roadmap.shared.domain.plan.model

import kotlin.jvm.JvmInline
import kotlin.time.Duration

data class Plan(
    val id: PlanId,
    val name: String,
    val occurrence: Occurrence,
    val commitmentPerOccurrence: Duration
) {

    @JvmInline
    value class PlanId(val value: Long)
}

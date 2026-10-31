package com.rsalanga.roadmap.shared.domain.plan.model

import kotlin.time.Duration
import kotlinx.datetime.LocalTime

data class PlanDetails(
    val plan: Plan,
    val preferredDay: PreferredDay?,
    val preferredTime: LocalTime?,
    val sessionLength: Duration,
    val topics: List<Topic>
)

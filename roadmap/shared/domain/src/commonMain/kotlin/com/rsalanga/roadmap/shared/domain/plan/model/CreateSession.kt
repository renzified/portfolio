package com.rsalanga.roadmap.shared.domain.plan.model

import kotlin.time.Duration
import kotlinx.datetime.LocalTime

data class CreateSession(
    val planId: Plan.PlanId,
    val preferredTime: LocalTime?,
    val sessionLength: Duration
)

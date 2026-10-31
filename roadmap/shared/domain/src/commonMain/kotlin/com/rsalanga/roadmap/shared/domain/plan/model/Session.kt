package com.rsalanga.roadmap.shared.domain.plan.model

import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlinx.datetime.LocalTime

data class Session(
    val id: SessionId,
    val planId: Plan.PlanId,
    val preferredTime: LocalTime?,
    val sessionLength: Duration
) {

    @JvmInline
    value class SessionId(val value: Long)
}

package com.rsalanga.roadmap.shared.domain.plan.model

import kotlin.time.Duration

data class Plan(
    val id: Long,
    val name: String,
    val occurrence: Occurrence,
    val commitmentPerOccurrence: Duration
)

package com.rsalanga.roadmap.shared.models

import kotlin.time.Duration
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalTime

data class StudyPlan(
    val id: Uuid,
    val name: String,
    val occurrence: Occurrence,
    val commitmentPerOccurrence: Duration,
    val preferredDay: PreferredDay?,
    val preferredTime: LocalTime?,
    val sessionLength: Duration
)

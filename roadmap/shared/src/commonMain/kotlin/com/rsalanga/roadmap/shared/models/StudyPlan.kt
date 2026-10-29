package com.rsalanga.roadmap.shared.models

import kotlinx.datetime.LocalTime
import kotlin.time.Duration
import kotlin.uuid.Uuid

data class StudyPlan(
    val id: Uuid,
    val name: String,
    val occurrence: Occurrence,
    val commitmentPerOccurrence: Duration,
    val preferredDay: PreferredDay?,
    val preferredTime: LocalTime?,
    val sessionLength: Duration,

)

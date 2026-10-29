package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rsalanga.roadmap.shared.models.Occurrence
import kotlin.time.Duration
import kotlinx.datetime.LocalTime

@Entity
data class PlanEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val occurrence: Occurrence,
    val commitmentPerOccurrence: Duration,
    val preferredTime: LocalTime?,
    val sessionLength: Duration
)

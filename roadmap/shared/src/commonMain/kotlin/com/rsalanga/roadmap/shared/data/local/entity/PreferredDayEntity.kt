package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.Entity
import kotlinx.datetime.DayOfWeek

@Entity
data class PreferredDayEntity(val id: Long, val planId: Long, val dayOfWeek: DayOfWeek)

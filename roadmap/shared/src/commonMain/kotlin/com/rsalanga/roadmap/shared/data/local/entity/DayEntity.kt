package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.DayOfWeek

@Entity
data class DayEntity(@PrimaryKey val id: Long, val planId: Long, val dayOfWeek: DayOfWeek)

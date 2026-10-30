package com.rsalanga.roadmap.shared.data.local.database

import androidx.room.TypeConverter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

class Converters {

    @TypeConverter
    fun fromDuration(value: Duration): Long = value.inWholeMilliseconds

    @TypeConverter
    fun toDuration(value: Long): Duration = value.milliseconds

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let(LocalTime::parse)

    @TypeConverter
    fun fromDayOfWeek(value: DayOfWeek): Int = value.isoDayNumber

    @TypeConverter
    fun toDayOfWeek(value: Int): DayOfWeek = DayOfWeek(value)
}

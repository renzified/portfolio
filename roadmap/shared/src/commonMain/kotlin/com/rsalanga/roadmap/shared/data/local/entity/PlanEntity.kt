package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rsalanga.roadmap.shared.models.Occurrence
import kotlin.time.Duration
import kotlinx.datetime.LocalTime

@Entity(tableName = PlanEntity.TABLE_NAME)
data class PlanEntity(
    @PrimaryKey
    @ColumnInfo(name = ID)
    val id: Long,
    @ColumnInfo(name = NAME)
    val name: String,
    @ColumnInfo(name = OCCURRENCE)
    val occurrence: Occurrence,
    @ColumnInfo(name = COMMITMENT_PER_OCCURRENCE)
    val commitmentPerOccurrence: Duration,
    @ColumnInfo(name = PREFERRED_TIME)
    val preferredTime: LocalTime?,
    @ColumnInfo(name = SESSION_LENGTH)
    val sessionLength: Duration
) {
    companion object {
        const val TABLE_NAME = "PlanEntity"
        const val ID = "id"
        const val NAME = "name"
        const val OCCURRENCE = "occurrence"
        const val COMMITMENT_PER_OCCURRENCE = "commitmentPerOccurrence"
        const val PREFERRED_TIME = "preferredTime"
        const val SESSION_LENGTH = "sessionLength"
    }
}

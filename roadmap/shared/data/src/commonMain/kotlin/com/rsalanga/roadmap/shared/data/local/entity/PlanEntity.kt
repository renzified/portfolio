package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Duration

@Entity(tableName = PlanEntity.TABLE_NAME)
data class PlanEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Long = 0,
    @ColumnInfo(name = NAME)
    val name: String,
    @ColumnInfo(name = OCCURRENCE)
    val occurrence: Occurrence,
    @ColumnInfo(name = COMMITMENT_PER_OCCURRENCE)
    val commitmentPerOccurrence: Duration
) {
    companion object {
        const val TABLE_NAME = "plan"
        const val ID = "id"
        const val NAME = "name"
        const val OCCURRENCE = "occurrence"
        const val COMMITMENT_PER_OCCURRENCE = "commitment_per_occurrence"
    }

    enum class Occurrence {
        DAY,
        WEEK,
        MONTH
    }
}

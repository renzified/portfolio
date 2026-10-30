package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rsalanga.roadmap.shared.models.TopicStatus

@Entity
data class TopicEntity(
    @PrimaryKey val id: Long,
    val planId: Long,
    val status: TopicStatus,
    val name: String
)

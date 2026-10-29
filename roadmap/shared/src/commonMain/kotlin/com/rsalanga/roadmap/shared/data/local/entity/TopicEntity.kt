package com.rsalanga.roadmap.shared.data.local.entity

import androidx.room.Entity
import com.rsalanga.roadmap.shared.models.TopicStatus

@Entity
data class TopicEntity(val id: Long, val planId: Long, val status: TopicStatus, val name: String)

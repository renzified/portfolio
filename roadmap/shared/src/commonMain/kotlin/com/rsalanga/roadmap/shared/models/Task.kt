package com.rsalanga.roadmap.shared.models

import kotlin.uuid.Uuid

data class Task(val id: Uuid, val status: TaskStatus, val details: String)

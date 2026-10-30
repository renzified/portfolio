package com.rsalanga.roadmap.shared.models

data class Topic(val id: Long, val status: TopicStatus, val name: String, val tasks: List<Task>)

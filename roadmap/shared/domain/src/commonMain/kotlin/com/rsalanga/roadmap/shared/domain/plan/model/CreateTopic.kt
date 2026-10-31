package com.rsalanga.roadmap.shared.domain.plan.model

data class CreateTopic(
    val planId: Plan.PlanId,
    val name: String,
    val status: TopicStatus = TopicStatus.PENDING
)

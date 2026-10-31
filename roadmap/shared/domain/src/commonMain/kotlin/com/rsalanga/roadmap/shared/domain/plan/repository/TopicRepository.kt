package com.rsalanga.roadmap.shared.domain.plan.repository

import com.rsalanga.roadmap.shared.domain.common.AppResult
import com.rsalanga.roadmap.shared.domain.common.DomainError
import com.rsalanga.roadmap.shared.domain.plan.model.CreateTopic
import com.rsalanga.roadmap.shared.domain.plan.model.Plan
import com.rsalanga.roadmap.shared.domain.plan.model.Topic
import com.rsalanga.roadmap.shared.domain.plan.model.TopicStatus
import kotlinx.coroutines.flow.Flow

interface TopicRepository {

    /** Emits the plan's topics, each with its tasks. */
    fun observeBy(planId: Plan.PlanId): Flow<List<Topic>>

    suspend fun save(item: CreateTopic): AppResult<Topic.TopicId, SaveError>

    suspend fun updateStatus(id: Topic.TopicId, status: TopicStatus): AppResult<Unit, UpdateError>

    suspend fun deleteBy(id: Topic.TopicId): AppResult<Unit, DeleteError>

    enum class SaveError : DomainError {
        DATABASE_ERROR
    }

    enum class UpdateError : DomainError {
        DATABASE_ERROR,
        NOT_FOUND
    }

    enum class DeleteError : DomainError {
        DATABASE_ERROR,
        ALREADY_DELETED
    }
}

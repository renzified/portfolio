package com.rsalanga.roadmap.shared.domain.plan.repository

import com.rsalanga.roadmap.shared.domain.common.AppResult
import com.rsalanga.roadmap.shared.domain.common.DomainError
import com.rsalanga.roadmap.shared.domain.plan.model.CreateSession
import com.rsalanga.roadmap.shared.domain.plan.model.Plan
import com.rsalanga.roadmap.shared.domain.plan.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    /** A plan has at most one session; emits `null` when it has none. */
    fun observeBy(planId: Plan.PlanId): Flow<Session?>

    suspend fun save(item: CreateSession): AppResult<Session.SessionId, SaveError>

    suspend fun update(item: Session): AppResult<Unit, UpdateError>

    suspend fun deleteBy(id: Session.SessionId): AppResult<Unit, DeleteError>

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

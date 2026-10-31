package com.rsalanga.roadmap.shared.domain.plan.repository

import com.rsalanga.roadmap.shared.domain.common.AppResult
import com.rsalanga.roadmap.shared.domain.common.DomainError
import com.rsalanga.roadmap.shared.domain.plan.model.CreatePlan
import com.rsalanga.roadmap.shared.domain.plan.model.Plan
import kotlinx.coroutines.flow.Flow

interface PlanRepository {

    fun observe(): Flow<List<Plan>>

    /** Emits `null` when the plan does not exist or has been deleted. */
    fun observeBy(id: Plan.PlanId): Flow<Plan?>

    suspend fun save(item: CreatePlan): AppResult<Plan.PlanId, SaveError>

    suspend fun deleteBy(id: Plan.PlanId): AppResult<Unit, DeleteError>

    enum class SaveError : DomainError {
        DATABASE_ERROR
    }

    enum class DeleteError : DomainError {
        DATABASE_ERROR,
        ALREADY_DELETED
    }
}

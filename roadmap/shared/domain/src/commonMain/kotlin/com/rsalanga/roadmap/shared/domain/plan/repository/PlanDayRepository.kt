package com.rsalanga.roadmap.shared.domain.plan.repository

import com.rsalanga.roadmap.shared.domain.common.AppResult
import com.rsalanga.roadmap.shared.domain.common.DomainError
import com.rsalanga.roadmap.shared.domain.plan.model.CreatePlanDay
import com.rsalanga.roadmap.shared.domain.plan.model.Plan
import com.rsalanga.roadmap.shared.domain.plan.model.PlanDay
import kotlinx.coroutines.flow.Flow

interface PlanDayRepository {

    fun observeBy(planId: Plan.PlanId): Flow<List<PlanDay>>

    suspend fun save(item: CreatePlanDay): AppResult<PlanDay.PlanDayId, SaveError>

    suspend fun deleteBy(id: PlanDay.PlanDayId): AppResult<Unit, DeleteError>

    /** Removes every day of the plan; succeeds even when the plan has no days. */
    suspend fun deleteBy(planId: Plan.PlanId): AppResult<Unit, DeleteError>

    enum class SaveError : DomainError {
        DATABASE_ERROR
    }

    enum class DeleteError : DomainError {
        DATABASE_ERROR,
        ALREADY_DELETED
    }
}

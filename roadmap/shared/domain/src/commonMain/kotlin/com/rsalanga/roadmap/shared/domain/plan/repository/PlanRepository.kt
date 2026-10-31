package com.rsalanga.roadmap.shared.domain.plan.repository

import com.rsalanga.roadmap.shared.domain.common.AppResult
import com.rsalanga.roadmap.shared.domain.common.DomainError
import com.rsalanga.roadmap.shared.domain.plan.model.CreatePlan
import com.rsalanga.roadmap.shared.domain.plan.model.Plan
import kotlinx.coroutines.flow.Flow

interface PlanRepository {

    fun observePlans(): Flow<List<Plan>>

    suspend fun savePlan(item: CreatePlan): AppResult<Unit, SavePlanError>

    suspend fun deletePlan(id: Long): AppResult<Unit, DeletePlanError>

    enum class SavePlanError : DomainError {
        DATABASE_ERROR
    }

    enum class DeletePlanError : DomainError {
        DATABASE_ERROR
    }
}

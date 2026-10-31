package com.rsalanga.roadmap.shared.data.repository

import androidx.sqlite.SQLiteException
import com.rsalanga.roadmap.shared.data.local.dao.PlanDao
import com.rsalanga.roadmap.shared.data.mapper.toDomain
import com.rsalanga.roadmap.shared.data.mapper.toEntity
import com.rsalanga.roadmap.shared.domain.common.AppResult
import com.rsalanga.roadmap.shared.domain.plan.model.CreatePlan
import com.rsalanga.roadmap.shared.domain.plan.model.Plan
import com.rsalanga.roadmap.shared.domain.plan.repository.PlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class PlanRepositoryImpl(private val planDao: PlanDao) : PlanRepository {

    override fun observePlans(): Flow<List<Plan>> = planDao.getPlans().map {
        it.map { item -> item.toDomain() }
    }.flowOn(Dispatchers.Default)

    override suspend fun savePlan(item: CreatePlan): AppResult<Unit, PlanRepository.SavePlanError> =
        try {
            planDao.insert(item.toEntity())
            AppResult.Success(Unit)
        } catch (_: SQLiteException) {
            AppResult.Failure(PlanRepository.SavePlanError.DATABASE_ERROR)
        }

    override suspend fun deletePlan(id: Long): AppResult<Unit, PlanRepository.DeletePlanError> =
        try {
            planDao.deletePlan(id)
            AppResult.Success(Unit)
        } catch (_: SQLiteException) {
            AppResult.Failure(PlanRepository.DeletePlanError.DATABASE_ERROR)
        }
}

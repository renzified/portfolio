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

    override fun observe(): Flow<List<Plan>> = planDao.getAll().map {
        it.map { item -> item.toDomain() }
    }.flowOn(Dispatchers.Default)

    override fun observeBy(id: Plan.PlanId): Flow<Plan?> =
        planDao.getById(id.value).map { it?.toDomain() }.flowOn(Dispatchers.Default)

    override suspend fun save(item: CreatePlan): AppResult<Plan.PlanId, PlanRepository.SaveError> =
        try {
            AppResult.Success(Plan.PlanId(planDao.insert(item.toEntity())))
        } catch (_: SQLiteException) {
            AppResult.Failure(PlanRepository.SaveError.DATABASE_ERROR)
        }

    override suspend fun deleteBy(id: Plan.PlanId): AppResult<Unit, PlanRepository.DeleteError> =
        try {
            if (planDao.deleteById(id.value) > 0) {
                AppResult.Success(Unit)
            } else {
                AppResult.Failure(PlanRepository.DeleteError.ALREADY_DELETED)
            }
        } catch (_: SQLiteException) {
            AppResult.Failure(PlanRepository.DeleteError.DATABASE_ERROR)
        }
}

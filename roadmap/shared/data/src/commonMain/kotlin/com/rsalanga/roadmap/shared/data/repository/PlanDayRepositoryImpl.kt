package com.rsalanga.roadmap.shared.data.repository

import androidx.sqlite.SQLiteException
import com.rsalanga.roadmap.shared.data.local.dao.PlanDayDao
import com.rsalanga.roadmap.shared.data.mapper.toDomain
import com.rsalanga.roadmap.shared.data.mapper.toEntity
import com.rsalanga.roadmap.shared.domain.common.AppResult
import com.rsalanga.roadmap.shared.domain.plan.model.CreatePlanDay
import com.rsalanga.roadmap.shared.domain.plan.model.Plan
import com.rsalanga.roadmap.shared.domain.plan.model.PlanDay
import com.rsalanga.roadmap.shared.domain.plan.repository.PlanDayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class PlanDayRepositoryImpl(private val planDayDao: PlanDayDao) : PlanDayRepository {

    override fun observeBy(planId: Plan.PlanId): Flow<List<PlanDay>> =
        planDayDao.getByPlanId(planId.value).map {
            it.map { item -> item.toDomain() }
        }.flowOn(Dispatchers.Default)

    override suspend fun save(
        item: CreatePlanDay
    ): AppResult<PlanDay.PlanDayId, PlanDayRepository.SaveError> = try {
        AppResult.Success(PlanDay.PlanDayId(planDayDao.insert(item.toEntity())))
    } catch (_: SQLiteException) {
        AppResult.Failure(PlanDayRepository.SaveError.DATABASE_ERROR)
    }

    override suspend fun deleteBy(
        id: PlanDay.PlanDayId
    ): AppResult<Unit, PlanDayRepository.DeleteError> = try {
        if (planDayDao.deleteById(id.value) > 0) {
            AppResult.Success(Unit)
        } else {
            AppResult.Failure(PlanDayRepository.DeleteError.ALREADY_DELETED)
        }
    } catch (_: SQLiteException) {
        AppResult.Failure(PlanDayRepository.DeleteError.DATABASE_ERROR)
    }

    override suspend fun deleteBy(
        planId: Plan.PlanId
    ): AppResult<Unit, PlanDayRepository.DeleteError> = try {
        planDayDao.deleteByPlanId(planId.value)
        AppResult.Success(Unit)
    } catch (_: SQLiteException) {
        AppResult.Failure(PlanDayRepository.DeleteError.DATABASE_ERROR)
    }
}

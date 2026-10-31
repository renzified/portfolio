package com.rsalanga.roadmap.shared.data.repository

import androidx.sqlite.SQLiteException
import com.rsalanga.roadmap.shared.data.local.dao.SessionDao
import com.rsalanga.roadmap.shared.data.mapper.toDomain
import com.rsalanga.roadmap.shared.data.mapper.toEntity
import com.rsalanga.roadmap.shared.domain.common.AppResult
import com.rsalanga.roadmap.shared.domain.plan.model.CreateSession
import com.rsalanga.roadmap.shared.domain.plan.model.Plan
import com.rsalanga.roadmap.shared.domain.plan.model.Session
import com.rsalanga.roadmap.shared.domain.plan.repository.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class SessionRepositoryImpl(private val sessionDao: SessionDao) : SessionRepository {

    override fun observeBy(planId: Plan.PlanId): Flow<Session?> =
        sessionDao.getByPlanId(planId.value).map { it?.toDomain() }.flowOn(Dispatchers.Default)

    override suspend fun save(
        item: CreateSession
    ): AppResult<Session.SessionId, SessionRepository.SaveError> = try {
        AppResult.Success(Session.SessionId(sessionDao.insert(item.toEntity())))
    } catch (_: SQLiteException) {
        AppResult.Failure(SessionRepository.SaveError.DATABASE_ERROR)
    }

    override suspend fun update(item: Session): AppResult<Unit, SessionRepository.UpdateError> =
        try {
            if (sessionDao.update(item.toEntity()) > 0) {
                AppResult.Success(Unit)
            } else {
                AppResult.Failure(SessionRepository.UpdateError.NOT_FOUND)
            }
        } catch (_: SQLiteException) {
            AppResult.Failure(SessionRepository.UpdateError.DATABASE_ERROR)
        }

    override suspend fun deleteBy(
        id: Session.SessionId
    ): AppResult<Unit, SessionRepository.DeleteError> = try {
        if (sessionDao.deleteById(id.value) > 0) {
            AppResult.Success(Unit)
        } else {
            AppResult.Failure(SessionRepository.DeleteError.ALREADY_DELETED)
        }
    } catch (_: SQLiteException) {
        AppResult.Failure(SessionRepository.DeleteError.DATABASE_ERROR)
    }
}

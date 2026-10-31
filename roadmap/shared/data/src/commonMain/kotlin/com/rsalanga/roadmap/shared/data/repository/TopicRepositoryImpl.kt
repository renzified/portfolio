package com.rsalanga.roadmap.shared.data.repository

import androidx.sqlite.SQLiteException
import com.rsalanga.roadmap.shared.data.local.dao.TopicDao
import com.rsalanga.roadmap.shared.data.mapper.toDomain
import com.rsalanga.roadmap.shared.data.mapper.toEntity
import com.rsalanga.roadmap.shared.domain.common.AppResult
import com.rsalanga.roadmap.shared.domain.plan.model.CreateTopic
import com.rsalanga.roadmap.shared.domain.plan.model.Plan
import com.rsalanga.roadmap.shared.domain.plan.model.Topic
import com.rsalanga.roadmap.shared.domain.plan.model.TopicStatus
import com.rsalanga.roadmap.shared.domain.plan.repository.TopicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class TopicRepositoryImpl(private val topicDao: TopicDao) : TopicRepository {

    override fun observeBy(planId: Plan.PlanId): Flow<List<Topic>> =
        topicDao.getWithTasksByPlanId(planId.value).map {
            it.map { item -> item.toDomain() }
        }.flowOn(Dispatchers.Default)

    override suspend fun save(
        item: CreateTopic
    ): AppResult<Topic.TopicId, TopicRepository.SaveError> = try {
        AppResult.Success(Topic.TopicId(topicDao.insert(item.toEntity())))
    } catch (_: SQLiteException) {
        AppResult.Failure(TopicRepository.SaveError.DATABASE_ERROR)
    }

    override suspend fun updateStatus(
        id: Topic.TopicId,
        status: TopicStatus
    ): AppResult<Unit, TopicRepository.UpdateError> = try {
        if (topicDao.updateStatus(id.value, status.toEntity()) > 0) {
            AppResult.Success(Unit)
        } else {
            AppResult.Failure(TopicRepository.UpdateError.NOT_FOUND)
        }
    } catch (_: SQLiteException) {
        AppResult.Failure(TopicRepository.UpdateError.DATABASE_ERROR)
    }

    override suspend fun deleteBy(id: Topic.TopicId): AppResult<Unit, TopicRepository.DeleteError> =
        try {
            if (topicDao.deleteById(id.value) > 0) {
                AppResult.Success(Unit)
            } else {
                AppResult.Failure(TopicRepository.DeleteError.ALREADY_DELETED)
            }
        } catch (_: SQLiteException) {
            AppResult.Failure(TopicRepository.DeleteError.DATABASE_ERROR)
        }
}

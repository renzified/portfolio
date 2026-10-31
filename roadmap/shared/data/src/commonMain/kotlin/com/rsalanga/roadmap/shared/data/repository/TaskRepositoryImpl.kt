package com.rsalanga.roadmap.shared.data.repository

import androidx.sqlite.SQLiteException
import com.rsalanga.roadmap.shared.data.local.dao.TaskDao
import com.rsalanga.roadmap.shared.data.mapper.toDomain
import com.rsalanga.roadmap.shared.data.mapper.toEntity
import com.rsalanga.roadmap.shared.domain.common.AppResult
import com.rsalanga.roadmap.shared.domain.plan.model.CreateTask
import com.rsalanga.roadmap.shared.domain.plan.model.Task
import com.rsalanga.roadmap.shared.domain.plan.model.TaskStatus
import com.rsalanga.roadmap.shared.domain.plan.model.Topic
import com.rsalanga.roadmap.shared.domain.plan.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(private val taskDao: TaskDao) : TaskRepository {

    override fun observeBy(topicId: Topic.TopicId): Flow<List<Task>> =
        taskDao.getByTopicId(topicId.value).map {
            it.map { item -> item.toDomain() }
        }.flowOn(Dispatchers.Default)

    override suspend fun save(item: CreateTask): AppResult<Task.TaskId, TaskRepository.SaveError> =
        try {
            AppResult.Success(Task.TaskId(taskDao.insert(item.toEntity())))
        } catch (_: SQLiteException) {
            AppResult.Failure(TaskRepository.SaveError.DATABASE_ERROR)
        }

    override suspend fun updateStatus(
        id: Task.TaskId,
        status: TaskStatus
    ): AppResult<Unit, TaskRepository.UpdateError> = try {
        if (taskDao.updateStatus(id.value, status.toEntity()) > 0) {
            AppResult.Success(Unit)
        } else {
            AppResult.Failure(TaskRepository.UpdateError.NOT_FOUND)
        }
    } catch (_: SQLiteException) {
        AppResult.Failure(TaskRepository.UpdateError.DATABASE_ERROR)
    }

    override suspend fun deleteBy(id: Task.TaskId): AppResult<Unit, TaskRepository.DeleteError> =
        try {
            if (taskDao.deleteById(id.value) > 0) {
                AppResult.Success(Unit)
            } else {
                AppResult.Failure(TaskRepository.DeleteError.ALREADY_DELETED)
            }
        } catch (_: SQLiteException) {
            AppResult.Failure(TaskRepository.DeleteError.DATABASE_ERROR)
        }
}

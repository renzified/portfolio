package com.rsalanga.roadmap.shared.domain.plan.repository

import com.rsalanga.roadmap.shared.domain.common.AppResult
import com.rsalanga.roadmap.shared.domain.common.DomainError
import com.rsalanga.roadmap.shared.domain.plan.model.CreateTask
import com.rsalanga.roadmap.shared.domain.plan.model.Task
import com.rsalanga.roadmap.shared.domain.plan.model.TaskStatus
import com.rsalanga.roadmap.shared.domain.plan.model.Topic
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun observeBy(topicId: Topic.TopicId): Flow<List<Task>>

    suspend fun save(item: CreateTask): AppResult<Task.TaskId, SaveError>

    suspend fun updateStatus(id: Task.TaskId, status: TaskStatus): AppResult<Unit, UpdateError>

    suspend fun deleteBy(id: Task.TaskId): AppResult<Unit, DeleteError>

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

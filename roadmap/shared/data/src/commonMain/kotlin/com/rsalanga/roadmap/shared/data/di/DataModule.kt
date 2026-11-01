package com.rsalanga.roadmap.shared.data.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.rsalanga.roadmap.shared.data.local.database.AppDatabase
import com.rsalanga.roadmap.shared.data.repository.PlanDayRepositoryImpl
import com.rsalanga.roadmap.shared.data.repository.PlanRepositoryImpl
import com.rsalanga.roadmap.shared.data.repository.SessionRepositoryImpl
import com.rsalanga.roadmap.shared.data.repository.TaskRepositoryImpl
import com.rsalanga.roadmap.shared.data.repository.TopicRepositoryImpl
import com.rsalanga.roadmap.shared.domain.plan.repository.PlanDayRepository
import com.rsalanga.roadmap.shared.domain.plan.repository.PlanRepository
import com.rsalanga.roadmap.shared.domain.plan.repository.SessionRepository
import com.rsalanga.roadmap.shared.domain.plan.repository.TaskRepository
import com.rsalanga.roadmap.shared.domain.plan.repository.TopicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal const val DATABASE_NAME = "roadmap.db"

/**
 * Platform-agnostic data bindings. Each platform must also load a module that
 * provides a [RoomDatabase.Builder] for [AppDatabase] (e.g. `androidDataModule`).
 */
val dataModule = module {
    single<AppDatabase> {
        get<RoomDatabase.Builder<AppDatabase>>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    single { get<AppDatabase>().getPlanDao() }
    single { get<AppDatabase>().getPlanDayDao() }
    single { get<AppDatabase>().getSessionDao() }
    single { get<AppDatabase>().getTaskDao() }
    single { get<AppDatabase>().getTopicDao() }

    singleOf(::PlanRepositoryImpl) bind PlanRepository::class
    singleOf(::PlanDayRepositoryImpl) bind PlanDayRepository::class
    singleOf(::SessionRepositoryImpl) bind SessionRepository::class
    singleOf(::TaskRepositoryImpl) bind TaskRepository::class
    singleOf(::TopicRepositoryImpl) bind TopicRepository::class
}

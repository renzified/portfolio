package com.rsalanga.roadmap.shared.data.di

import androidx.test.platform.app.InstrumentationRegistry
import com.rsalanga.roadmap.shared.domain.plan.repository.PlanDayRepository
import com.rsalanga.roadmap.shared.domain.plan.repository.PlanRepository
import com.rsalanga.roadmap.shared.domain.plan.repository.SessionRepository
import com.rsalanga.roadmap.shared.domain.plan.repository.TaskRepository
import com.rsalanga.roadmap.shared.domain.plan.repository.TopicRepository
import org.junit.After
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication

class DataModuleTest {

    // Resolving the repositories builds the database but never opens a connection,
    // so no database file is created on the device.
    private val koinApp = koinApplication {
        androidContext(InstrumentationRegistry.getInstrumentation().targetContext)
        modules(dataModule, androidDataModule)
    }

    @After
    fun tearDown() = koinApp.close()

    @Test
    fun resolvesAllRepositories() {
        with(koinApp.koin) {
            get<PlanRepository>()
            get<PlanDayRepository>()
            get<SessionRepository>()
            get<TaskRepository>()
            get<TopicRepository>()
        }
    }
}

package com.rsalanga.roadmap.shared.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.rsalanga.roadmap.shared.data.local.dao.PlanDao
import com.rsalanga.roadmap.shared.data.local.dao.PlanDayDao
import com.rsalanga.roadmap.shared.data.local.dao.SessionDao
import com.rsalanga.roadmap.shared.data.local.dao.TaskDao
import com.rsalanga.roadmap.shared.data.local.dao.TopicDao
import com.rsalanga.roadmap.shared.data.local.entity.PlanDayEntity
import com.rsalanga.roadmap.shared.data.local.entity.PlanEntity
import com.rsalanga.roadmap.shared.data.local.entity.SessionEntity
import com.rsalanga.roadmap.shared.data.local.entity.TaskEntity
import com.rsalanga.roadmap.shared.data.local.entity.TopicEntity

@Database(
    entities = [
        PlanDayEntity::class,
        PlanEntity::class,
        SessionEntity::class,
        TaskEntity::class,
        TopicEntity::class
    ],
    version = 2
)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getPlanDayDao(): PlanDayDao

    abstract fun getPlanDao(): PlanDao

    abstract fun getSessionDao(): SessionDao

    abstract fun getTaskDao(): TaskDao

    abstract fun getTopicDao(): TopicDao
}

@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

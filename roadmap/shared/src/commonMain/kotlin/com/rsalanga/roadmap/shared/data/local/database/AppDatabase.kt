package com.rsalanga.roadmap.shared.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.rsalanga.roadmap.shared.data.local.dao.DayDao
import com.rsalanga.roadmap.shared.data.local.dao.PlanDao
import com.rsalanga.roadmap.shared.data.local.dao.TaskDao
import com.rsalanga.roadmap.shared.data.local.dao.TopicDao
import com.rsalanga.roadmap.shared.data.local.entity.DayEntity
import com.rsalanga.roadmap.shared.data.local.entity.PlanEntity
import com.rsalanga.roadmap.shared.data.local.entity.TaskEntity
import com.rsalanga.roadmap.shared.data.local.entity.TopicEntity

@Database(
    entities = [DayEntity::class, PlanEntity::class, TaskEntity::class, TopicEntity::class],
    version = 2
)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getDayDao(): DayDao

    abstract fun getPlanDao(): PlanDao

    abstract fun getTaskDao(): TaskDao

    abstract fun getTopicDao(): TopicDao
}

@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

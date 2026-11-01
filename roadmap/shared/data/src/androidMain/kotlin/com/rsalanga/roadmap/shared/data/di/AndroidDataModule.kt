package com.rsalanga.roadmap.shared.data.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.rsalanga.roadmap.shared.data.local.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** Android bindings for [dataModule]; requires `androidContext(...)` at Koin startup. */
val androidDataModule = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        val context = androidContext()
        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = context.getDatabasePath(DATABASE_NAME).absolutePath
        )
    }
}

package com.rsalanga.roadmap

import android.app.Application
import com.rsalanga.roadmap.shared.data.di.androidDataModule
import com.rsalanga.roadmap.shared.data.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RoadmapApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@RoadmapApplication)
            modules(dataModule, androidDataModule)
        }
    }
}

package com.rsalanga.roadmap.shared.data.local.dao

import androidx.room.Room
import androidx.room.useReaderConnection
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.platform.app.InstrumentationRegistry
import com.rsalanga.roadmap.shared.data.local.database.AppDatabase
import kotlinx.coroutines.Dispatchers

internal fun createInMemoryDatabase(): AppDatabase = Room
    .inMemoryDatabaseBuilder<AppDatabase>(
        InstrumentationRegistry.getInstrumentation().targetContext
    )
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()

/**
 * Reads rows with raw SQL so tests check what is actually stored, including
 * the column values produced by the type converters.
 */
internal suspend fun <T> AppDatabase.queryRows(sql: String, read: (SQLiteStatement) -> T): List<T> =
    useReaderConnection { connection ->
        connection.usePrepared(sql) { statement ->
            buildList {
                while (statement.step()) add(read(statement))
            }
        }
    }

internal suspend fun AppDatabase.count(table: String): Long =
    queryRows("SELECT COUNT(*) FROM $table") { it.getLong(0) }.single()

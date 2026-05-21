package com.eltex.androidschool.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.eltex.androidschool.feauture.event.data.EventEntity
import com.eltex.androidschool.feauture.event.data.EventsDao

@Database(
    entities = [EventEntity::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = AppDb.Migration2to3::class)
    ]
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract val eventsDao: EventsDao

    @DeleteColumn(tableName = "events", columnName = "newProperty")
    class Migration2to3 : AutoMigrationSpec

    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null

        fun getInstance(context: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }

        private fun buildDatabase(context: Context): AppDb = Room.databaseBuilder(
            context,
            AppDb::class.java,
            "app.db",
        )
            .createFromAsset("database/app.db")
            .fallbackToDestructiveMigration(true)
            .allowMainThreadQueries()
            .build()
    }
}

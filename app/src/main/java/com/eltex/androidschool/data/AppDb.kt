package com.eltex.androidschool.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eltex.androidschool.feauture.event.data.EventEntity
import com.eltex.androidschool.feauture.event.data.EventsDao

@Database(entities = [EventEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract val eventsDao: EventsDao

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
            .fallbackToDestructiveMigration(true)
            .allowMainThreadQueries()
            .build()
    }
}

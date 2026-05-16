package com.eltex.androidschool.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.eltex.androidschool.feauture.event.data.EventsDao
import com.eltex.androidschool.feauture.event.data.EventsDaoImpl

class AppDb private constructor(db: SQLiteDatabase) {
    val eventsDao: EventsDao = EventsDaoImpl(db)
    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null

        fun getInstance(context: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppDb(AppDbHelper(context.applicationContext).writableDatabase).also {
                    INSTANCE = it
                }
            }
    }
}

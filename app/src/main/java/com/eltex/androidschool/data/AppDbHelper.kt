package com.eltex.androidschool.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.eltex.androidschool.feauture.event.data.EventColumns

class AppDbHelper(context: Context) : SQLiteOpenHelper(context, "app.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE ${EventColumns.TABLE_NAME} (
                    ${EventColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                    ${EventColumns.COLUMN_AUTHOR} TEXT NOT NULL,
                    ${EventColumns.COLUMN_CONTENT} TEXT NOT NULL,
                    ${EventColumns.COLUMN_PUBLISHED} INTEGER NOT NULL,
                    ${EventColumns.COLUMN_TYPE} TEXT NOT NULL,
                    ${EventColumns.COLUMN_DATETIME} INTEGER NOT NULL,
                    ${EventColumns.COLUMN_LINK} TEXT,
                    ${EventColumns.COLUMN_LIKED_BY_ME} INTEGER NOT NULL DEFAULT 0,
                    ${EventColumns.COLUMN_LIKES} INTEGER NOT NULL DEFAULT 0,
                    ${EventColumns.COLUMN_PARTICIPATED_BY_ME} INTEGER NOT NULL DEFAULT 0,
                    ${EventColumns.COLUMN_PARTICIPANTS} INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        TODO("Not yet implemented")
    }
}

package com.eltex.androidschool.utils

import android.database.Cursor

fun Cursor.getLongOrThrow(columnName: String): Long =
    getLong(getColumnIndexOrThrow(columnName))

fun Cursor.getStringOrThrow(columnName: String): String =
    getString(getColumnIndexOrThrow(columnName))

fun Cursor.getIntOrThrow(columnName: String): Int =
    getInt(getColumnIndexOrThrow(columnName))

fun Cursor.getBooleanOrThrow(columnName: String): Boolean =
    getIntOrThrow(columnName) != 0

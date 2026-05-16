package com.eltex.androidschool.data

import androidx.room.TypeConverter
import com.eltex.androidschool.feauture.event.domain.EventType

class Converters {
    @TypeConverter
    fun fromEventType(value: EventType): String = value.name

    @TypeConverter
    fun toEventType(value: String): EventType = EventType.valueOf(value)
}

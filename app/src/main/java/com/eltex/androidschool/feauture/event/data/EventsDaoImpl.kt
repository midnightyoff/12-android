package com.eltex.androidschool.feauture.event.data

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import com.eltex.androidschool.feauture.event.domain.EventType
import com.eltex.androidschool.utils.getBooleanOrThrow
import com.eltex.androidschool.utils.getIntOrThrow
import com.eltex.androidschool.utils.getLongOrThrow
import com.eltex.androidschool.utils.getStringOrThrow

class EventsDaoImpl(private val database: SQLiteDatabase) : EventsDao {
    override fun getAll(): List<EventEntity> {
        val result = mutableListOf<EventEntity>()

        database.query(
            EventColumns.TABLE_NAME,
            EventColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${EventColumns.COLUMN_ID} DESC",
        )
            .use { cursor ->
                while (cursor.moveToNext()) {
                    result += cursor.readEvent()
                }
            }

        return result
    }

    override fun save(event: EventEntity): EventEntity {
        val contentValues = contentValuesOf(
            EventColumns.COLUMN_AUTHOR to event.author,
            EventColumns.COLUMN_CONTENT to event.content,
            EventColumns.COLUMN_PUBLISHED to event.published,
            EventColumns.COLUMN_TYPE to event.type.name,
            EventColumns.COLUMN_DATETIME to event.datetime,
            EventColumns.COLUMN_LINK to event.link,
            EventColumns.COLUMN_LIKES to event.likes,
            EventColumns.COLUMN_LIKED_BY_ME to event.likedByMe,
            EventColumns.COLUMN_PARTICIPANTS to event.participants,
            EventColumns.COLUMN_PARTICIPATED_BY_ME to event.participatedByMe,
        )

        if (event.id != 0L) {
            contentValues.put(EventColumns.COLUMN_ID, event.id)
        }

        val id = database.replace(
            EventColumns.TABLE_NAME,
            null,
            contentValues,
        )

        return getEventById(id)
    }

    override fun likeById(id: Long): EventEntity {
        database.execSQL(
            """
            UPDATE ${EventColumns.TABLE_NAME} SET 
                ${EventColumns.COLUMN_LIKED_BY_ME} = CASE WHEN ${EventColumns.COLUMN_LIKED_BY_ME} THEN 0 ELSE 1 END,
                ${EventColumns.COLUMN_LIKES} = CASE WHEN ${EventColumns.COLUMN_LIKED_BY_ME} THEN ${EventColumns.COLUMN_LIKES} - 1 ELSE ${EventColumns.COLUMN_LIKES} + 1 END
            WHERE ${EventColumns.COLUMN_ID} = ?;
        """.trimIndent(),
            arrayOf(id.toString())
        )

        return getEventById(id)
    }

    override fun participateById(id: Long): EventEntity {
        database.execSQL(
            """
            UPDATE ${EventColumns.TABLE_NAME} SET 
                ${EventColumns.COLUMN_PARTICIPATED_BY_ME} = CASE WHEN ${EventColumns.COLUMN_PARTICIPATED_BY_ME} THEN 0 ELSE 1 END,
                ${EventColumns.COLUMN_PARTICIPANTS} = CASE WHEN ${EventColumns.COLUMN_PARTICIPATED_BY_ME} THEN ${EventColumns.COLUMN_PARTICIPANTS} - 1 ELSE ${EventColumns.COLUMN_PARTICIPANTS} + 1 END
            WHERE ${EventColumns.COLUMN_ID} = ?;
        """.trimIndent(),
            arrayOf(id.toString())
        )

        return getEventById(id)
    }

    override fun deleteById(id: Long) {
        database.delete(
            EventColumns.TABLE_NAME,
            "${EventColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
        )
    }

    private fun getEventById(id: Long): EventEntity =
        database.query(
            EventColumns.TABLE_NAME,
            EventColumns.ALL_COLUMNS,
            "${EventColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null,
        )
            .use { cursor ->
                cursor.moveToNext()
                cursor.readEvent()
            }

    private fun Cursor.readEvent() = EventEntity(
        id = getLongOrThrow(EventColumns.COLUMN_ID),
        content = getStringOrThrow(EventColumns.COLUMN_CONTENT),
        author = getStringOrThrow(EventColumns.COLUMN_AUTHOR),
        published = getLongOrThrow(EventColumns.COLUMN_PUBLISHED),
        type = EventType.valueOf(getStringOrThrow(EventColumns.COLUMN_TYPE)),
        datetime = getLongOrThrow(EventColumns.COLUMN_DATETIME),
        link = getStringOrThrow(EventColumns.COLUMN_LINK),
        likedByMe = getBooleanOrThrow(EventColumns.COLUMN_LIKED_BY_ME),
        likes = getIntOrThrow(EventColumns.COLUMN_LIKES),
        participatedByMe = getBooleanOrThrow(EventColumns.COLUMN_PARTICIPATED_BY_ME),
        participants = getIntOrThrow(EventColumns.COLUMN_PARTICIPANTS),
    )
}

package com.eltex.androidschool.feauture.event.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventType
import java.time.Instant

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "content")
    val content: String = "",
    @ColumnInfo(name = "author")
    val author: String = "",
    @ColumnInfo(name = "published")
    val published: Long = 0L,
    @ColumnInfo(name = "type")
    val type: EventType = EventType.OFFLINE,
    @ColumnInfo(name = "datetime")
    val datetime: Long = 0L,
    @ColumnInfo(name = "link")
    val link: String? = null,
    @ColumnInfo(name = "likedByMe", defaultValue = "0")
    val likedByMe: Boolean = false,
    @ColumnInfo(name = "likes", defaultValue = "0")
    val likes: Int = 0,
    @ColumnInfo(name = "participatedByMe", defaultValue = "0")
    val participatedByMe: Boolean = false,
    @ColumnInfo(name = "participants", defaultValue = "0")
    val participants: Int = 0,
) {
    fun toEvent(): Event = Event(
        id = id,
        content = content,
        author = author,
        published = Instant.ofEpochMilli(published),
        type = type,
        datetime = Instant.ofEpochMilli(datetime),
        link = link ?: "",
        likedByMe = likedByMe,
        likes = likes,
        participatedByMe = participatedByMe,
        participants = participants
    )
}

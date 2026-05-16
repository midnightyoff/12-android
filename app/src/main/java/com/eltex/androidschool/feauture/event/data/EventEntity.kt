package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

data class EventEntity(
    val id: Long = 0L,
    val content: String = "",
    val author: String = "",
    val published: Long = 0L,
    val type: EventType = EventType.OFFLINE,
    val datetime: Long = 0L,
    val link: String = "",
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val participatedByMe: Boolean = false,
    val participants: Int = 0,
) {
    fun toEvent(): Event = Event(
        id = id,
        content = content,
        author = author,
        published = Instant.ofEpochMilli(published),
        type = type,
        datetime = Instant.ofEpochMilli(datetime),
        link = link,
        likedByMe = likedByMe,
        likes = likes,
        participatedByMe = participatedByMe,
        participants = participants
    )
}

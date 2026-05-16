package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class EventEntity(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("content")
    val content: String = "",
    @SerialName("author")
    val author: String = "",
    @SerialName("published")
    val published: Long = 0L,
    @SerialName("type")
    val type: EventType = EventType.OFFLINE,
    @SerialName("datetime")
    val datetime: Long = 0L,
    @SerialName("link")
    val link: String = "",
    @SerialName("likedByMe")
    val likedByMe: Boolean = false,
    @SerialName("likes")
    val likes: Int = 0,
    @SerialName("participatedByMe")
    val participatedByMe: Boolean = false,
    @SerialName("participants")
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

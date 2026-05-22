package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class EventDto(
    @SerialName("id") val id: Long = 0,
    @SerialName("author") val author: String = "",
    @SerialName("content") val content: String = "",
    @SerialName("published") val published: String = "",
    @SerialName("type") val type: EventType = EventType.OFFLINE,
    @SerialName("datetime") val datetime: String = "",
    @SerialName("link") val link: String? = null,
    @SerialName("likeOwnerIds") val likeOwnerIds: List<Long> = emptyList(),
    @SerialName("likedByMe") val likedByMe: Boolean = false,
    @SerialName("participantsIds") val participantsIds: List<Long> = emptyList(),
    @SerialName("participatedByMe") val participatedByMe: Boolean = false,
) {
    fun toEvent(): Event = Event(
        id = id,
        content = content,
        author = author,
        published = Instant.parse(published),
        type = type,
        datetime = Instant.parse(datetime),
        link = link.orEmpty(),
        likedByMe = likedByMe,
        likes = likeOwnerIds.size,
        participatedByMe = participatedByMe,
        participants = participantsIds.size,
    )
}
package com.eltex.androidschool.feauture.event.domain

import kotlinx.serialization.Serializable
import java.time.Instant

data class Event(
    val id: Long = 0L,
    val content: String = "",
    val author: String = "",
    val published: Instant = Instant.now(),
    val type: EventType = EventType.OFFLINE,
    val datetime: Instant = Instant.now(),
    val link: String = "",
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val participatedByMe: Boolean = false,
    val participants: Int = 0,
)

@Serializable
enum class EventType {
    OFFLINE, ONLINE
}

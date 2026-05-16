package com.eltex.androidschool.feauture.event.list

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class EventUiState(
    val id: Long = 0,
    val author: String = "",
    val published: Instant = Instant.now(),
    val type: EventType = EventType.OFFLINE,
    val datetime: Instant = Instant.now(),
    val content: String = "",
    val link: String = "",
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val participatedByMe: Boolean = false,
    val participants: Int = 0,
)

enum class EventType {
    OFFLINE, ONLINE
}

private val publishedFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")
    .withZone(ZoneId.systemDefault())

val EventUiState.publishedText: String
    get() = publishedFormatter.format(published)

val EventUiState.datetimeText: String
    get() = publishedFormatter.format(datetime)
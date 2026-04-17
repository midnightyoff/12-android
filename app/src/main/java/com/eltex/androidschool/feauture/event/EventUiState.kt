package com.eltex.androidschool.feauture.event

data class EventUiState(
    val id: Long = 0,
    val author: String = "",
    val published: String = "",
    val type: EventType = EventType.OFFLINE,
    val datetime: String = "",
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
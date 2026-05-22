package com.eltex.androidschool.feauture.event.list

import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventType
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class EventUiModel(
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
) {
    companion object {
        private val formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")
            .withZone(ZoneId.systemDefault())

        fun fromEvent(event: Event) = with(event) {
            EventUiModel(
                id = id,
                author = author,
                published = formatter.format(published),
                type = type,
                datetime = formatter.format(datetime),
                content = content,
                link = link,
                likedByMe = likedByMe,
                likes = likes,
                participatedByMe = participatedByMe,
                participants = participants,
            )
        }
    }
}
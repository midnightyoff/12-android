package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventRepository

class EventRepositoryImpl : EventRepository {
    override suspend fun getEvents(): List<Event> =
        EventApi.value.getAll().map(EventDto::toEvent)

    override suspend fun likeById(id: Long, likedByMe: Boolean): Event {
        val dto = if (likedByMe) EventApi.value.dislikeById(id) else EventApi.value.likeById(id)
        return dto.toEvent()
    }

    override suspend fun participateById(id: Long, participatedByMe: Boolean): Event {
        val dto = if (participatedByMe) EventApi.value.unparticipateById(id) else EventApi.value.participateById(id)
        return dto.toEvent()
    }

    override suspend fun saveEvent(id: Long, content: String): Event =
        EventApi.value.saveEvent(EventDto(id = id, content = content)).toEvent()

    override suspend fun deleteById(id: Long) {
        EventApi.value.deleteById(id)
    }
}
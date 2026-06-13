package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.data.HttpClientFactory
import com.eltex.androidschool.feauture.event.data.EventApi.deleteEvent
import com.eltex.androidschool.feauture.event.data.EventApi.dislikeEvent
import com.eltex.androidschool.feauture.event.data.EventApi.getEventsBefore
import com.eltex.androidschool.feauture.event.data.EventApi.getLatestEvents
import com.eltex.androidschool.feauture.event.data.EventApi.likeEvent
import com.eltex.androidschool.feauture.event.data.EventApi.participateEvent
import com.eltex.androidschool.feauture.event.data.EventApi.saveEvent
import com.eltex.androidschool.feauture.event.data.EventApi.unparticipateEvent
import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventRepository
import io.ktor.client.HttpClient

class EventRepositoryImpl(
    private val client: HttpClient = HttpClientFactory.client,
) : EventRepository {
    override suspend fun getEventsLatest(size: Int): List<Event> = client.getLatestEvents(size)
        .map(EventDto::toEvent)

    override suspend fun getEventsBefore(id: Long, size: Int): List<Event> =
        client.getEventsBefore(id, size)
            .map(EventDto::toEvent)

    override suspend fun likeById(id: Long, likedByMe: Boolean): Event {
        val dto = if (likedByMe) client.dislikeEvent(id) else client.likeEvent(id)
        return dto.toEvent()
    }

    override suspend fun participateById(id: Long, participatedByMe: Boolean): Event {
        val dto = if (participatedByMe) client.unparticipateEvent(id) else client.participateEvent(id)
        return dto.toEvent()
    }

    override suspend fun saveEvent(id: Long, content: String): Event =
        client.saveEvent(EventDto(id = id, content = content)).toEvent()

    override suspend fun deleteById(id: Long) {
        client.deleteEvent(id)
    }
}
package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class EventRepositoryImpl : EventRepository {
    override fun getEvents(): Single<List<Event>> =
        EventApi.value.getAll()
            .map { it.map(EventDto::toEvent) }

    override fun likeById(id: Long, likedByMe: Boolean): Single<Event> {
        val call = if (likedByMe) EventApi.value.dislikeById(id) else EventApi.value.likeById(id)
        return call.map(EventDto::toEvent)
    }

    override fun participateById(id: Long, participatedByMe: Boolean): Single<Event> {
        val call = if (participatedByMe) EventApi.value.unparticipateById(id) else EventApi.value.participateById(id)
        return call.map(EventDto::toEvent)
    }

    override fun saveEvent(id: Long, content: String): Single<Event> =
        EventApi.value.saveEvent(EventDto(id = id, content = content))
            .map(EventDto::toEvent)

    override fun deleteById(id: Long): Completable =
        EventApi.value.deleteById(id)
}
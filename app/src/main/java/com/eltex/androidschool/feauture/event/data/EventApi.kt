package com.eltex.androidschool.feauture.event.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

object EventApi {
    suspend fun HttpClient.getAllEvents(): List<EventDto> =
        get("events").body()

    suspend fun HttpClient.saveEvent(eventDto: EventDto): EventDto =
        post("events") {
            setBody(eventDto)
        }.body()

    suspend fun HttpClient.likeEvent(id: Long): EventDto =
        post("events/$id/likes").body()

    suspend fun HttpClient.dislikeEvent(id: Long): EventDto =
        delete("events/$id/likes").body()

    suspend fun HttpClient.participateEvent(id: Long): EventDto =
        post("events/$id/participants").body()

    suspend fun HttpClient.unparticipateEvent(id: Long): EventDto =
        delete("events/$id/participants").body()

    suspend fun HttpClient.deleteEvent(id: Long) {
        delete("events/$id")
    }
}
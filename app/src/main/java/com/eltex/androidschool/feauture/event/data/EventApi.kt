package com.eltex.androidschool.feauture.event.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

object EventApi {
    suspend fun HttpClient.getLatestEvents(count: Int): List<EventDto> =
        get("events/latest") {
            parameter("count", count)
        }.body()

    suspend fun HttpClient.getEventsBefore(id: Long, count: Int): List<EventDto> =
        get("events/$id/before") {
            parameter("count", count)
        }.body()

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
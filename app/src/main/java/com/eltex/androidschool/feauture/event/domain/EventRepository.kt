package com.eltex.androidschool.feauture.event.domain

interface EventRepository {
    suspend fun getEventsLatest(size: Int): List<Event> = emptyList()
    suspend fun getEventsBefore(id: Long, size: Int): List<Event> = emptyList()
    suspend fun likeById(id: Long, likedByMe: Boolean): Event
    suspend fun participateById(id: Long, participatedByMe: Boolean): Event
    suspend fun saveEvent(id: Long, content: String): Event
    suspend fun deleteById(id: Long)
}
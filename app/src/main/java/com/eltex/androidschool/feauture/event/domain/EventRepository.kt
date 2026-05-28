package com.eltex.androidschool.feauture.event.domain

interface EventRepository {
    suspend fun getEvents(): List<Event>
    suspend fun likeById(id: Long, likedByMe: Boolean): Event
    suspend fun participateById(id: Long, participatedByMe: Boolean): Event
    suspend fun saveEvent(id: Long, content: String): Event
    suspend fun deleteById(id: Long)
}
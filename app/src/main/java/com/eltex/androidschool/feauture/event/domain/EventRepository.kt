package com.eltex.androidschool.feauture.event.domain

import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEvents(): Flow<List<Event>>
    fun likeById(id: Long)
    fun participateById(id: Long)
    fun saveEvent(id: Long, content: String)
    fun deleteById(id: Long)
}
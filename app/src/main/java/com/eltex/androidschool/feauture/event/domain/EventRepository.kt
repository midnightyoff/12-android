package com.eltex.androidschool.feauture.event.domain

interface EventRepository {
    fun getEvents(callback: Callback<List<Event>>)
    fun likeById(id: Long, likedByMe: Boolean, callback: Callback<Event>)
    fun participateById(id: Long, participatedByMe: Boolean, callback: Callback<Event>)
    fun saveEvent(id: Long, content: String, callback: Callback<Event>)
    fun deleteById(id: Long, callback: Callback<Unit>)
}
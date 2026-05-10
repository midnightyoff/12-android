package com.eltex.androidschool.feauture.event.data

interface EventsDao {
    fun getAll(): List<EventEntity>
    fun save(event: EventEntity): EventEntity
    fun likeById(id: Long): EventEntity
    fun participateById(id: Long): EventEntity
    fun deleteById(id: Long)
}

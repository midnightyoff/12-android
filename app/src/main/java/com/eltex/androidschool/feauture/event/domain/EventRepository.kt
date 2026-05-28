package com.eltex.androidschool.feauture.event.domain

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface EventRepository {
    fun getEvents(): Single<List<Event>>
    fun likeById(id: Long, likedByMe: Boolean): Single<Event>
    fun participateById(id: Long, participatedByMe: Boolean): Single<Event>
    fun saveEvent(id: Long, content: String): Single<Event>
    fun deleteById(id: Long): Completable
}
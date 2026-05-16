package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class EventRepositoryImpl(private val dao: EventsDao) : EventRepository {
    override val events: Flow<List<Event>> = dao.getAll().map { it.map(EventEntity::toEvent) }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun participateById(id: Long) {
        dao.participateById(id)
    }

    override fun saveEvent(id: Long, content: String) {
        val event = if (id == 0L) {
            EventEntity(
                content = content,
                author = "Me",
                published = Instant.now().toEpochMilli(),
                datetime = Instant.now().toEpochMilli(),
            )
        } else {
            dao.getById(id)?.copy(content = content) ?: return
        }
        dao.save(event)
    }

    override fun deleteById(id: Long) {
        dao.deleteById(id)
    }
}

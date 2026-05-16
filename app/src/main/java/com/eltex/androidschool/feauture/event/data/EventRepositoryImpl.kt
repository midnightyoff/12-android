package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.Instant

class EventRepositoryImpl(private val dao: EventsDao) : EventRepository {
    private val _events = MutableStateFlow(readEvents())

    override val events: Flow<List<Event>> = _events.map { it.map(EventEntity::toEvent) }

    override fun likeById(id: Long) {
        dao.likeById(id)
        sync()
    }

    override fun participateById(id: Long) {
        dao.participateById(id)
        sync()
    }

    override fun saveEvent(id: Long, content: String) {
        dao.save(
            if (id == 0L) {
                EventEntity(
                    content = content,
                    author = "Me",
                    published = Instant.now().toEpochMilli(),
                    datetime = Instant.now().toEpochMilli(),
                )
            } else {
                dao.getAll().find { it.id == id }?.copy(content = content) ?: return
            }
        )
        sync()
    }

    override fun deleteById(id: Long) {
        dao.deleteById(id)
        sync()
    }

    fun readEvents(): List<EventEntity> = dao.getAll()

    private fun sync() {
        _events.update { readEvents() }
    }
}

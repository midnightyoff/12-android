package com.eltex.androidschool.feauture.event.data

import android.content.Context
import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.time.Instant

class EventRepositoryImpl(private val context: Context) : EventRepository {

    private val _events = MutableStateFlow(readEvents())

    override fun getEvents(): Flow<List<Event>> = _events.map { it.map(EventEntity::toEvent) }

    override fun likeById(id: Long) {
        _events.update { events ->
            events.map { currentEvent ->
                if (currentEvent.id == id) {
                    currentEvent.copy(
                        likes = if (currentEvent.likedByMe) {
                            currentEvent.likes - 1
                        } else {
                            currentEvent.likes + 1
                        },
                        likedByMe = !currentEvent.likedByMe,
                    )
                } else {
                    currentEvent
                }
            }
        }
        sync()
    }

    override fun participateById(id: Long) {
        _events.update { events ->
            events.map { currentEvent ->
                if (currentEvent.id == id) {
                    currentEvent.copy(
                        participants = if (currentEvent.participatedByMe) {
                            currentEvent.participants - 1
                        } else {
                            currentEvent.participants + 1
                        },
                        participatedByMe = !currentEvent.participatedByMe,
                    )
                } else {
                    currentEvent
                }
            }
        }
        sync()
    }

    override fun saveEvent(id: Long, content: String) {
        _events.update { events ->
            if (id == 0L) {
                listOf(
                    EventEntity(
                        id = (events.maxByOrNull { it.id }?.id ?: 0L) + 1L,
                        content = content,
                        author = "Me",
                        published = Instant.now().toEpochMilli(),
                        datetime = Instant.now().toEpochMilli(),
                    )
                ) + events
            } else {
                events.map {
                    if (it.id == id) it.copy(content = content) else it
                }
            }
        }
        sync()
    }

    override fun deleteById(id: Long) {
        _events.update { events ->
            events.filter { it.id != id }
        }
        sync()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun readEvents(): List<EventEntity> {
        val file = context.filesDir.resolve(FILE_NAME)
        return if (file.exists()) {
            file.inputStream().buffered().use {
                Json.decodeFromStream(it)
            }
        } else {
            emptyList()
        }
    }

    private fun sync() {
        context.filesDir.resolve(FILE_NAME)
            .bufferedWriter()
            .use {
                it.write(Json.encodeToString(_events.value))
            }
    }

    private companion object {
        const val FILE_NAME = "events.json"
    }
}

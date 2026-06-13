package com.eltex.androidschool.feauture.event.list

import arrow.core.left
import arrow.core.right
import com.eltex.androidschool.domain.AppException
import com.eltex.androidschool.feauture.event.data.EventRepositoryImpl
import com.eltex.androidschool.feauture.event.domain.EventRepository
import com.eltex.androidschool.tea.EffectHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge

class EventListEffectHandler(
    private val repository: EventRepository = EventRepositoryImpl(),
) : EffectHandler<EventMessage, EventEffect> {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun connect(effects: Flow<EventEffect>): Flow<EventMessage> = listOf(
        effects.filterIsInstance<EventEffect.LoadInitial>().mapLatest { effect ->
            EventMessage.LoadInitialResult(
                try {
                    repository.getEventsLatest(effect.size).right()
                } catch (e: AppException) {
                    e.left()
                }
            )
        },
        effects.filterIsInstance<EventEffect.LoadNextPage>().mapLatest { effect ->
            EventMessage.LoadNextPageResult(
                try {
                    repository.getEventsBefore(effect.id, effect.size).right()
                } catch (e: AppException) {
                    e.left()
                }
            )
        },
        effects.filterIsInstance<EventEffect.Save>().mapLatest { effect ->
            EventMessage.AddEventResult(
                try {
                    repository.saveEvent(effect.id, effect.content).right()
                } catch (e: AppException) {
                    e.left()
                }
            )
        },
        effects.filterIsInstance<EventEffect.Like>().mapLatest { effect ->
            try {
                EventMessage.LikeSuccess(
                    repository.likeById(effect.data.id, likedByMe = effect.liked)
                )
            } catch (e: AppException) {
                EventMessage.LikeError(
                    eventId = effect.data.id,
                    originalLikedByMe = effect.data.originalLikedByMe,
                    originalLikes = effect.data.originalLikes,
                    error = e,
                )
            }
        },
        effects.filterIsInstance<EventEffect.Participate>().mapLatest { effect ->
            try {
                EventMessage.ParticipateSuccess(
                    repository.participateById(effect.data.id, participatedByMe = effect.participated)
                )
            } catch (e: AppException) {
                EventMessage.ParticipateError(
                    eventId = effect.data.id,
                    originalParticipatedByMe = effect.data.originalParticipatedByMe,
                    originalParticipants = effect.data.originalParticipants,
                    error = e,
                )
            }
        },
        effects.filterIsInstance<EventEffect.Delete>().mapLatest { effect ->
            try {
                repository.deleteById(effect.original.id)
                null
            } catch (e: AppException) {
                EventMessage.DeleteError(original = effect.original, error = e)
            }
        }
            .filterNotNull(),
    ).merge()
}
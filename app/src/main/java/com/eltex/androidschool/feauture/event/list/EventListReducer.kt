package com.eltex.androidschool.feauture.event.list

import com.eltex.androidschool.domain.LoadingState
import com.eltex.androidschool.feauture.event.list.EventEffect.Delete
import com.eltex.androidschool.feauture.event.list.EventEffect.EditEvent
import com.eltex.androidschool.feauture.event.list.EventEffect.Error
import com.eltex.androidschool.feauture.event.list.EventEffect.Like
import com.eltex.androidschool.feauture.event.list.EventEffect.LoadInitial
import com.eltex.androidschool.feauture.event.list.EventEffect.LoadNextPage
import com.eltex.androidschool.feauture.event.list.EventEffect.Participate
import com.eltex.androidschool.feauture.event.list.EventEffect.Save
import com.eltex.androidschool.feauture.event.list.EventEffect.ScrollTo
import com.eltex.androidschool.feauture.event.list.EventEffect.Share
import com.eltex.androidschool.tea.Reducer
import com.eltex.androidschool.tea.ReducerResult

class EventListReducer : Reducer<EventListState, EventMessage, EventEffect> {
    private companion object {
        const val PAGE_SIZE = 5
    }

    override fun reduce(
        currentState: EventListState,
        message: EventMessage,
    ): ReducerResult<EventListState, EventEffect> = when (message) {
        EventMessage.Retry,
        EventMessage.LoadInitial -> ReducerResult(
            newState = currentState.copy(status = LoadingState.Loading),
            effect = LoadInitial(PAGE_SIZE),
        )

        is EventMessage.LoadInitialResult -> ReducerResult(
            message.result.fold(
                ifLeft = { currentState.copy(status = LoadingState.Error(it)) },
                ifRight = { events ->
                    currentState.copy(
                        status = LoadingState.Idle,
                        events = events.map(EventUiModel::fromEvent),
                    )
                },
            )
        )

        EventMessage.LoadNextPage -> {
            val lastId = currentState.events.orEmpty().lastOrNull()?.id

            if (lastId == null || currentState.status == LoadingState.Loading) {
                ReducerResult(currentState)
            } else {
                ReducerResult(
                    newState = currentState.copy(status = LoadingState.Loading),
                    effect = LoadNextPage(lastId, PAGE_SIZE),
                )
            }
        }

        is EventMessage.LoadNextPageResult -> ReducerResult(
            message.result.fold(
                ifLeft = { currentState.copy(status = LoadingState.Error(it)) },
                ifRight = { events ->
                    currentState.copy(
                        status = LoadingState.Idle,
                        events = currentState.events.orEmpty() + events.map(EventUiModel::fromEvent),
                    )
                },
            )
        )

        is EventMessage.Like -> {
            val current = currentState.events?.find { it.id == message.id }

            current?.let {
                ReducerResult(
                    currentState.copy(
                        events = currentState.events.map {
                            if (it.id == message.id) {
                                it.copy(
                                    likes = if (message.liked) it.likes - 1 else it.likes + 1,
                                    likedByMe = !message.liked,
                                )
                            } else {
                                it
                            }
                        }
                    ),
                    Like(
                        liked = message.liked,
                        data = LikeData(
                            id = current.id,
                            originalLikedByMe = current.likedByMe,
                            originalLikes = current.likes,
                        ),
                    ),
                )
            } ?: ReducerResult(currentState)
        }

        is EventMessage.LikeError -> with(message) {
            ReducerResult(
                currentState.copy(
                    events = currentState.events?.map {
                        if (it.id == eventId) {
                            it.copy(likes = originalLikes, likedByMe = originalLikedByMe)
                        } else {
                            it
                        }
                    }
                ),
                Error(error),
            )
        }

        is EventMessage.LikeSuccess -> ReducerResult(
            currentState.copy(
                events = currentState.events?.map {
                    if (it.id == message.event.id) EventUiModel.fromEvent(message.event) else it
                }
            )
        )

        is EventMessage.Participate -> {
            val current = currentState.events?.find { it.id == message.id }

            current?.let {
                ReducerResult(
                    currentState.copy(
                        events = currentState.events.map {
                            if (it.id == message.id) {
                                it.copy(
                                    participants = if (message.participated) {
                                        it.participants - 1
                                    } else {
                                        it.participants + 1
                                    },
                                    participatedByMe = !message.participated,
                                )
                            } else {
                                it
                            }
                        }
                    ),
                    Participate(
                        participated = message.participated,
                        data = ParticipateData(
                            id = current.id,
                            originalParticipatedByMe = current.participatedByMe,
                            originalParticipants = current.participants,
                        ),
                    ),
                )
            } ?: ReducerResult(currentState)
        }

        is EventMessage.ParticipateError -> with(message) {
            ReducerResult(
                currentState.copy(
                    events = currentState.events?.map {
                        if (it.id == eventId) {
                            it.copy(
                                participants = originalParticipants,
                                participatedByMe = originalParticipatedByMe,
                            )
                        } else {
                            it
                        }
                    }
                ),
                Error(error),
            )
        }

        is EventMessage.ParticipateSuccess -> ReducerResult(
            currentState.copy(
                events = currentState.events?.map {
                    if (it.id == message.event.id) EventUiModel.fromEvent(message.event) else it
                }
            )
        )

        is EventMessage.Delete -> {
            val toDelete = currentState.events?.find { it.id == message.id }

            toDelete?.let {
                ReducerResult(
                    currentState.copy(
                        events = currentState.events.filterNot { it.id == message.id },
                    ),
                    Delete(it),
                )
            } ?: ReducerResult(currentState)
        }

        is EventMessage.DeleteError -> with(message) {
            val events = currentState.events.orEmpty()
            ReducerResult(
                currentState.copy(
                    events = buildList(events.size + 1) {
                        addAll(events.takeWhile { it.id > original.id })
                        add(original)
                        addAll(events.takeLastWhile { it.id < original.id })
                    },
                ),
                Error(error),
            )
        }

        is EventMessage.AddEvent -> ReducerResult(
            currentState,
            setOfNotNull(
                Save(message.id, message.text),
                ScrollTo(0).takeIf { message.id == 0L },
            ),
        )

        is EventMessage.AddEventResult -> message.value.fold(
            ifLeft = { ReducerResult(currentState, Error(it)) },
            ifRight = { event ->
                val ui = EventUiModel.fromEvent(event)
                val events = currentState.events.orEmpty()
                ReducerResult(
                    currentState.copy(
                        events = if (events.any { it.id == event.id }) {
                            events.map { if (it.id == event.id) ui else it }
                        } else {
                            listOf(ui) + events
                        }
                    )
                )
            }
        )

        is EventMessage.Share -> currentState.events?.find { it.id == message.id }
            ?.let { ReducerResult(currentState, Share(it.content)) }
            ?: ReducerResult(currentState)

        is EventMessage.EditEvent -> ReducerResult(currentState, EditEvent(message.event))
    }
}
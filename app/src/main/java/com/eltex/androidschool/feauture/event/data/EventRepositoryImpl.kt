package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.feauture.event.domain.Callback
import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventRepository
import retrofit2.Call
import retrofit2.Response

class EventRepositoryImpl : EventRepository {
    override fun getEvents(callback: Callback<List<Event>>) {
        EventApi.value.getAll().enqueue(object : retrofit2.Callback<List<EventDto>> {
            override fun onResponse(call: Call<List<EventDto>>, response: Response<List<EventDto>>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body().orEmpty().map(EventDto::toEvent))
                } else {
                    callback.onError(RuntimeException(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<List<EventDto>>, t: Throwable) {
                callback.onError(RuntimeException(t))
            }
        })
    }

    override fun likeById(id: Long, likedByMe: Boolean, callback: Callback<Event>) {
        val call = if (likedByMe) EventApi.value.dislikeById(id) else EventApi.value.likeById(id)
        call.enqueue(object : retrofit2.Callback<EventDto> {
            override fun onResponse(call: Call<EventDto>, response: Response<EventDto>) {
                if (response.isSuccessful) {
                    response.body()?.toEvent()?.let {
                        callback.onSuccess(it)
                    } ?: run {
                        callback.onError(RuntimeException(response.errorBody()?.string()))
                    }
                } else {
                    callback.onError(RuntimeException(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<EventDto>, t: Throwable) {
                callback.onError(RuntimeException(t))
            }
        })
    }

    override fun participateById(id: Long, participatedByMe: Boolean, callback: Callback<Event>) {
        val call = if (participatedByMe) EventApi.value.unparticipateById(id) else EventApi.value.participateById(id)
        call.enqueue(object : retrofit2.Callback<EventDto> {
            override fun onResponse(call: Call<EventDto>, response: Response<EventDto>) {
                if (response.isSuccessful) {
                    response.body()?.toEvent()?.let {
                        callback.onSuccess(it)
                    } ?: run {
                        callback.onError(RuntimeException(response.errorBody()?.string()))
                    }
                } else {
                    callback.onError(RuntimeException(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<EventDto>, t: Throwable) {
                callback.onError(RuntimeException(t))
            }
        })
    }

    override fun saveEvent(id: Long, content: String, callback: Callback<Event>) {
        EventApi.value.saveEvent(EventDto(id = id, content = content))
            .enqueue(object : retrofit2.Callback<EventDto> {
                override fun onResponse(call: Call<EventDto>, response: Response<EventDto>) {
                    if (response.isSuccessful) {
                        response.body()?.toEvent()?.let {
                            callback.onSuccess(it)
                        } ?: run {
                            callback.onError(RuntimeException(response.errorBody()?.string()))
                        }
                    } else {
                        callback.onError(RuntimeException(response.errorBody()?.string()))
                    }
                }

                override fun onFailure(call: Call<EventDto>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }
            })
    }

    override fun deleteById(id: Long, callback: Callback<Unit>) {
        EventApi.value.deleteById(id).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    callback.onSuccess(Unit)
                } else {
                    callback.onError(RuntimeException(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(RuntimeException(t))
            }
        })
    }
}
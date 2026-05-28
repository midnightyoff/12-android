package com.eltex.androidschool.domain

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

interface SchedulerProvider {
    fun computation(): Scheduler
    fun mainThread(): Scheduler

    companion object Default : SchedulerProvider {
        override fun computation(): Scheduler = Schedulers.computation()
        override fun mainThread(): Scheduler = AndroidSchedulers.mainThread()
    }
}
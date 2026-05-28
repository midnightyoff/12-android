package com.eltex.androidschool.feauture

import com.eltex.androidschool.domain.SchedulerProvider
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

object TrampolineSchedulerProvider : SchedulerProvider {
    override fun computation(): Scheduler = Schedulers.trampoline()
    override fun mainThread(): Scheduler = Schedulers.trampoline()
}
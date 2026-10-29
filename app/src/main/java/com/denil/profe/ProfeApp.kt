package com.denil.profe

import android.app.Application
import com.denil.profe.notifications.ReminderScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ProfeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ReminderScheduler.createChannel(this)
    }
}

package com.nsoft.github

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class NSoftApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)
    }

    companion object {
        private lateinit var instance: NSoftApplication

        @JvmStatic
        fun getAppContext(): Context {
            // While generally considered an anti-pattern, the benefits outweight the negatives.
            return instance.applicationContext
        }
    }
}

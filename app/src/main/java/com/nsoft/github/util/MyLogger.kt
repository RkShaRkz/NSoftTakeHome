package com.nsoft.github.util

import androidx.annotation.NonNull
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

object MyLogger {
    @JvmOverloads
    fun d(@NonNull logtag: String, @NonNull message: String, th: Throwable? = null) {
        Timber.tag(logtag).d(th, message)
    }

    @JvmOverloads
    fun v(@NonNull logtag: String, @NonNull message: String, th: Throwable? = null) {
        Timber.tag(logtag).v(th, message)
    }

    @JvmOverloads
    fun i(@NonNull logtag: String, @NonNull message: String, th: Throwable? = null) {
        Timber.tag(logtag).i(th, message)
    }

    @JvmOverloads
    fun w(@NonNull logtag: String, @NonNull message: String, th: Throwable? = null) {
        Timber.tag(logtag).w(th, message)
    }

    @JvmOverloads
    fun e(@NonNull logtag: String, @NonNull message: String, th: Throwable? = null) {
        Timber.tag(logtag).e(th, message)
    }

    @JvmOverloads
    fun wtf(@NonNull logtag: String, @NonNull message: String, th: Throwable? = null) {
        Timber.tag(logtag).wtf(th, message)
    }

    object MyOKHttpLogger : HttpLoggingInterceptor.Logger {
        private const val LOGTAG = "NSoft-OkHttp"

        override fun log(message: String) {
            d(LOGTAG, message)
        }
    }
}

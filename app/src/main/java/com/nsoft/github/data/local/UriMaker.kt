package com.nsoft.github.data.local

import android.net.Uri

/**
 * Class for making [Uri]s so presenters don't get contaminated with android-specifics
 */
object UriMaker {
    fun createUri(urlString: String): Uri {
        return Uri.parse(urlString)
    }
}

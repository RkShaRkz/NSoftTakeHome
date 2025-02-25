package com.nsoft.github.data.local

import android.content.Context
import android.content.SharedPreferences
import com.nsoft.github.data.InboundDataSource
import com.nsoft.github.data.OutboundDataSource
import javax.inject.Inject

/**
 * A two-way DataSource (inbound and outbound) using [SharedPreferences] as it's backing storage
 * @see InboundDataSource
 * @see OutboundDataSource
 */
interface SharedPrefDataSource: InboundDataSource<String>, OutboundDataSource<String>

/**
 * Base implementation of [SharedPrefDataSource]
 */
class SharedPrefDataSourceImpl @Inject constructor(
    private val context: Context
): SharedPrefDataSource {

    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, SHARED_PREFS_MODE)
    }

    override fun getData(optionalKey: String?, defaultValue: String): String {
        return optionalKey?.let {
            sharedPreferences.getString(it, defaultValue)
        } ?: throw IllegalArgumentException("Using 'optionalKey' is mandatory for SharedPrefDataSources !!!")
    }

    override fun putData(optionalKey: String?, data: String): Boolean {
        val retVal = optionalKey?.let {
            sharedPreferences
                .edit()
                .putString(it, data)
                .commit()
        }

        return if (retVal == null) {
            // Lets just assume 'true' since commit() to shared prefs can't really fail
            // and this whole case is a syntactic sugar problem
            true
        } else {
            // Otherwise just return the retVal that we got from commit() itself.
            retVal
        }
    }

    override fun deleteData(optionalKey: String?): Boolean {
        return optionalKey?.let {
            sharedPreferences
                .edit()
                .remove(optionalKey)
                .commit()
        } ?: throw IllegalArgumentException("Using 'optionalKey' is mandatory for SharedPrefDataSources !!!")
    }

    //TODO fix this later when we need to have more, separated, SharedPref sources
    companion object {
        const val SHARED_PREFS_NAME = "NSoftGitHubSharedPrefs"
        const val SHARED_PREFS_MODE = Context.MODE_PRIVATE
    }
}
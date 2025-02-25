package com.nsoft.github.testreplacements

import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting

class SharedPreferencesForTesting : SharedPreferences {

    private val dataMap = mutableMapOf<String, Any?>()

    override fun getAll(): Map<String, *> = dataMap

    override fun getString(key: String?, defValue: String?): String? =
        dataMap[key] as? String ?: defValue

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): Set<String>? =
        dataMap[key] as? MutableSet<String> ?: defValues

    override fun getInt(key: String?, defValue: Int): Int =
        dataMap[key] as? Int ?: defValue

    override fun getLong(key: String?, defValue: Long): Long =
        dataMap[key] as? Long ?: defValue

    override fun getFloat(key: String?, defValue: Float): Float =
        dataMap[key] as? Float ?: defValue

    override fun getBoolean(key: String?, defValue: Boolean): Boolean =
        dataMap[key] as? Boolean ?: defValue

    override fun contains(key: String?): Boolean = dataMap.containsKey(key)

    override fun edit(): SharedPreferences.Editor = TestEditor()

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public fun getInnerDataMap(): MutableMap<String, Any?> {
        return dataMap
    }

    inner class TestEditor : SharedPreferences.Editor {
        private val tempMap = dataMap.toMutableMap()

        override fun putString(key: String?, value: String?): SharedPreferences.Editor = apply {
            tempMap[key ?: return this] = value
        }

        override fun putStringSet(key: String?,values: MutableSet<String>?): SharedPreferences.Editor = apply {
            tempMap[key ?: return this] = values
        }

        override fun putInt(key: String?, value: Int): SharedPreferences.Editor = apply {
            tempMap[key ?: return this] = value
        }

        override fun putLong(key: String?, value: Long): SharedPreferences.Editor = apply {
            tempMap[key ?: return this] = value
        }

        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = apply {
            tempMap[key ?: return this] = value
        }

        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = apply {
            tempMap[key ?: return this] = value
        }

        override fun remove(key: String?): SharedPreferences.Editor = apply {
            tempMap.remove(key)
        }

        override fun clear(): SharedPreferences.Editor = apply {
            tempMap.clear()
        }

        override fun commit(): Boolean {
            // Nead to clear the dataMap first so that removing keys can actually work alright
            dataMap.clear()
            // Now put the tempMap in
            dataMap.putAll(tempMap)
            return true
        }

        override fun apply() {
            commit()
        }
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        // No-op for testing
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        // No-op for testing
    }
}

class MockableTestEditor(sharedPreferences: SharedPreferencesForTesting) : SharedPreferences.Editor {
    val dataMap: MutableMap<String, Any?>
    init {
        dataMap = sharedPreferences.getInnerDataMap()
    }
    private val tempMap = dataMap.toMutableMap()

    override fun putString(key: String?, value: String?): SharedPreferences.Editor = apply {
        tempMap[key ?: return this] = value
    }

    override fun putStringSet(key: String?,values: MutableSet<String>?): SharedPreferences.Editor = apply {
        tempMap[key ?: return this] = values
    }

    override fun putInt(key: String?, value: Int): SharedPreferences.Editor = apply {
        tempMap[key ?: return this] = value
    }

    override fun putLong(key: String?, value: Long): SharedPreferences.Editor = apply {
        tempMap[key ?: return this] = value
    }

    override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = apply {
        tempMap[key ?: return this] = value
    }

    override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = apply {
        tempMap[key ?: return this] = value
    }

    override fun remove(key: String?): SharedPreferences.Editor = apply {
        tempMap.remove(key)
    }

    override fun clear(): SharedPreferences.Editor = apply {
        tempMap.clear()
    }

    override fun commit(): Boolean {
        // Convert dataMap to original
        // Nead to clear the dataMap first so that removing keys can actually work alright
        dataMap.clear()
        // Now put the tempMap in
        dataMap.putAll(tempMap)
        return true
    }

    override fun apply() {
        commit()
    }
}
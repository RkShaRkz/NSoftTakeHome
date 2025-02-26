package com.nsoft.github.data.local.room

import androidx.room.TypeConverter
import org.threeten.bp.Instant
import org.threeten.bp.format.DateTimeFormatter

class DatabaseConverters {

    @TypeConverter
    fun instantToString(instant: Instant): String = DateTimeFormatter.ISO_INSTANT.format(instant)

    @TypeConverter
    fun stringToInstant(stringInstant: String): Instant = Instant.parse(stringInstant)
}

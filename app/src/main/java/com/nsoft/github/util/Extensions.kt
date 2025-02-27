package com.nsoft.github.util

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

val <T> T.exhaustive: T
    get() = this

public const val PATTERN_DDMMYYYYatHHMM = "dd/MM/yyyy 'at' HH:mm"

fun Instant.formatToPattern(pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val zonedTime = this.atZone(ZoneId.systemDefault())
    return formatter.format(zonedTime)
}

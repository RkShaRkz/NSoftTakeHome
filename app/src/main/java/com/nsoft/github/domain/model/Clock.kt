package com.nsoft.github.domain.model

import javax.inject.Inject

/**
 * A mockable object that mainly wraps [System.currentTimeMillis] but won't really cause
 * problems with tests
 */
class Clock @Inject constructor() {

    /**
     * Wrapper around [System.currentTimeMillis]
     */
    public fun getCurrentTimeInMillis(): Long {
        return System.currentTimeMillis()
    }

    /**
     * Internally calls [getCurrentTimeInMillis] and divides by [1000]
     *
     * @see getCurrentTimeInMillis
     */
    public fun getCurrentTimeInSeconds(): Long {
        return getCurrentTimeInMillis() / 1000L
    }

    override fun equals(other: Any?): Boolean {
        // Clocks are "special" in this regard
        // since they are equal to any other clock, and aren't with anything else
        return if (other is Clock) {
            true
        } else {
            false
        }
    }
}
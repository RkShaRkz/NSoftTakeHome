package com.nsoft.github.domain.model

/**
 * This class has no values and is only used to "signal" that something is done.
 * Useful for scenarios when something can succeed or fail without a concrete return value
 */
class Signal private constructor() {
    object SIGNAL
}
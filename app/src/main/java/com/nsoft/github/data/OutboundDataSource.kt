package com.nsoft.github.data

/**
 * Abstract [OutboundDataSource] that writes data of type [T] from *somewhere*
 */
interface OutboundDataSource<T> {
    /**
     * Puts data to *whatever* source, optionally as [optionalKey] and returns whether operation was successful or not
     *
     * @param optionalKey an optional key to use; subclasses of this class don't necessarily have to use
     * it and it can be null
     * @return whether the operation was successful or not
     */
    fun putData(optionalKey:String?, data: T): Boolean

    /**
     * Deletes data from *whatever* source, optionally using the [optionalKey] and returns whether operation was successful or not
     *
     * @param optionalKey an optional key to use; subclasses of this class don't necessarily have to use it
     * and it can be null
     * @return whether the operation was successful or not
     */
    fun deleteData(optionalKey: String?): Boolean
}
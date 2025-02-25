package com.nsoft.github.data

/**
 * Abstract [InboundDataSource] that fetches data of type [T] from *somewhere*
 */
interface InboundDataSource<T> {
    /**
     * Get data from *whatever* source, possibly represented by [optionalKey]
     *
     * @param optionalKey an optional key to get; subclasses of this class don't necessarily have to use
     * it and it can be null
     * @param defaultValue an optional 'default value' to use when there is no data to get; subclasses will
     * try to return this value when it makes sense to do so but don't necessarily have to
     * @return data of type [T] - new data, data represented by [optionalKey] or [defaultValue]
     */
    fun getData(optionalKey:String?, defaultValue: T): T
}
package com.nsoft.github.data.remote.adapters

import com.nsoft.github.domain.model.ResponseDomainData

/**
 * Base class for all Response Adapters, mapping the raw json response from the server into the
 * relevant domain object of type [DomainClass], which has to be a subtype of [ResponseDomainData]
 */
abstract class ResponseAdapter<out DomainClass: ResponseDomainData> {

    /**
     * Remaps the [rawJson] String into the [DomainClass]
     */
    abstract fun convert(rawJson: String): DomainClass

    inline fun <reified DomainClass> getResponseType(): Class<DomainClass> {
        return DomainClass::class.java
    }
}
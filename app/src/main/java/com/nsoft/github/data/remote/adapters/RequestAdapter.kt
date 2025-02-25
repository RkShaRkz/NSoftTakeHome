package com.nsoft.github.data.remote.adapters

import com.nsoft.github.data.remote.params.RequestParams

typealias QueryMap = Map<String, String>
typealias HeadersMap = Map<String, String>
typealias FieldsMap = Map<String, String>

/**
 * Base generic class for all [RequestAdapter]s which will convert the passed-in [RequestParams]
 * into a [HeadersMap] and [FieldsMap] parts to be attached to the request, which are just [Map]s
 * mapping header/value keys to their values
 */
abstract class RequestAdapter<in Params: RequestParams> {

    /**
     * Method for converting a concrete [RequestParams] into actual OkHttp [Call] parameters
     *
     * @return a [Triple] containing three maps, a HeaderMap containing all headers and ValuesMap containing
     * all JSON values to be put into the request, as well as a QueryMap containing all "query" parameters
     * to be "appended to" (or "replaced in") the endpoint itself
     */
    abstract fun convert(params: Params) : Triple<HeadersMap, FieldsMap, QueryMap>
}
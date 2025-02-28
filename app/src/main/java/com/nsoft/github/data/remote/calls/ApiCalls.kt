package com.nsoft.github.data.remote.calls

import javax.inject.Named

/**
 * Just names of API calls we'll be using.
 *
 * This class exists solely for the purpose of using a String in the @Named ([Named]) DI qualifier
 * because we may come to a situation of targetting the same endpoint with multiple usecases,
 * so we'd need a way to differentiate the [ApiCall]s provided with a name.
 */
object ApiCalls {
    const val SEARCH_REPOSITORIES = "SEARCH_REPOSITORIES"
    const val REPOSITORY_DETAILS = "REPOSITORY_DETAILS"
    const val GET_COLLABORATORS = "GET_COLLABORATORS"
}

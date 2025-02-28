package com.nsoft.github.data.remote.adapters.get_collaborators

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.nsoft.github.data.remote.adapters.ResponseAdapter
import com.nsoft.github.domain.model.GitCollaborator
import com.nsoft.github.domain.model.GitCollaboratorList
import javax.inject.Inject

class GetCollaboratorsResponseAdapter @Inject constructor(
    val gson: Gson
): ResponseAdapter<GitCollaboratorList>() {

    override fun convert(rawJson: String): GitCollaboratorList {
        val users = gson.fromJson(rawJson, Array<GitCollaboratorJsonModel>::class.java).toList()
        // Remap into actual GitCollaborators
        val collaborators = mutableListOf<GitCollaborator>()
        for (user in users) {
            collaborators.add(
                GitCollaborator(
                    avatarUrl = user.avatarUrl,
                    login = user.login
                )
            )
        }

        // return the massaged response
        return GitCollaboratorList(collaborators = collaborators)
    }

    private data class GitCollaboratorJsonModel(
        @SerializedName("login") val login: String,
        @SerializedName("id") val id: Int,
        @SerializedName("node_id") val nodeId: String,
        @SerializedName("avatar_url") val avatarUrl: String,
        @SerializedName("gravatar_id") val gravatarId: String,
        @SerializedName("url") val url: String,
        @SerializedName("html_url") val htmlUrl: String,
        @SerializedName("followers_url") val followersUrl: String,
        @SerializedName("following_url") val followingUrl: String,
        @SerializedName("gists_url") val gistsUrl: String,
        @SerializedName("starred_url") val starredUrl: String,
        @SerializedName("subscriptions_url") val subscriptionsUrl: String,
        @SerializedName("organizations_url") val organizationsUrl: String,
        @SerializedName("repos_url") val reposUrl: String,
        @SerializedName("events_url") val eventsUrl: String,
        @SerializedName("received_events_url") val receivedEventsUrl: String,
        @SerializedName("type") val type: String,
        @SerializedName("user_view_type") val userViewType: String,
        @SerializedName("site_admin") val siteAdmin: Boolean,
        @SerializedName("contributions") val contributions: Int
    )
}

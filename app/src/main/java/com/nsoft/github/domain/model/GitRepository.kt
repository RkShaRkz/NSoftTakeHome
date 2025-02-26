package com.nsoft.github.domain.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nsoft.github.data.local.room.REPOSITORIES_PRIMARY_KEY
import com.nsoft.github.data.local.room.REPOSITORIES_TABLE_NAME
import org.threeten.bp.Instant

/**
 * A class that models a git repository, containing just what we need to show
 */
@Entity(tableName = REPOSITORIES_TABLE_NAME)
data class GitRepository(
    @PrimaryKey
    @ColumnInfo(name = REPOSITORIES_PRIMARY_KEY)
    val id: Long,
    @Embedded(prefix = "owner_")
    val owner: GitRepositoryOwner,
    val repoName: String,
    val description: String,
    val language: String,
    val stargazersCount: Int,
    val forksCount: Int,
    val openIssues: Int,
    val watchersCount: Int,
    // Additional fields for SecondScreen here
    val defaultBranch: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val collaboratorsUrl: String,   // "suradnik" can really be both of these so fuck it
    val contributorsUrl: String,
    val htmlUrl: String
): ResponseDomainData()

data class GitRepositoryOwner(
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String
)

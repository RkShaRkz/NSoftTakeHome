package com.nsoft.github.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GitCollaborator(
    @PrimaryKey(autoGenerate = true) val _databaseId: Int = 0,
    val avatarUrl: String,
    val login: String
)

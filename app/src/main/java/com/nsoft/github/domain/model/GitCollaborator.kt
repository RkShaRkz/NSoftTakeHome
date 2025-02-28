package com.nsoft.github.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nsoft.github.data.local.room.COLLABORATORS_PRIMARY_KEY
import com.nsoft.github.data.local.room.COLLABORATORS_TABLE_NAME

@Entity(
    tableName = COLLABORATORS_TABLE_NAME,
    indices = [Index(value = [COLLABORATORS_PRIMARY_KEY], unique = true)]
)
data class GitCollaborator(
    @PrimaryKey(autoGenerate = true) val _databaseId: Int = 0,
    val avatarUrl: String,
    val login: String
)

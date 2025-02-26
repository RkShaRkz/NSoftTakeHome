package com.nsoft.github.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nsoft.github.data.local.room.FAVORITES_PRIMARY_KEY
import com.nsoft.github.data.local.room.FAVORITES_TABLE_NAME
import com.nsoft.github.data.local.room.REPOSITORIES_PRIMARY_KEY
import org.threeten.bp.Instant

@Entity(
    tableName = FAVORITES_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = GitRepository::class,
        parentColumns = [REPOSITORIES_PRIMARY_KEY], // Assuming GitRepository has a unique `id` from the API
        childColumns = [FAVORITES_PRIMARY_KEY],
        onDelete = ForeignKey.CASCADE // Delete favorite if repo is deleted
    )],
    indices = [Index(FAVORITES_PRIMARY_KEY, unique = true)] // Ensure a repo can't be favorited twice
)
data class Favorite(
    @PrimaryKey
    @ColumnInfo(name = FAVORITES_PRIMARY_KEY)
    val repoId: Long, // Matches GitRepository.id (from API)
    val favoritedAt: Instant = Instant.now()
)

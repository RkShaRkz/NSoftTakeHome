package com.nsoft.github.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nsoft.github.data.local.room.dao.CollaboratorsDao
import com.nsoft.github.data.local.room.dao.FavoriteRepositoryDao
import com.nsoft.github.data.local.room.dao.GitRepositoryDao
import com.nsoft.github.domain.model.FavoriteRepository
import com.nsoft.github.domain.model.GitCollaborator
import com.nsoft.github.domain.model.GitRepository

@Database(
    entities = [GitRepository::class, FavoriteRepository::class, GitCollaborator::class],
    version = DATABASE_VERSION
)
@TypeConverters(DatabaseConverters::class)
abstract class Database: RoomDatabase() {
    abstract fun gitRepositoryDao(): GitRepositoryDao
    abstract fun favoriteRepositoryDao(): FavoriteRepositoryDao
    abstract fun collaboratorsDao(): CollaboratorsDao
}

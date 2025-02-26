package com.nsoft.github.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nsoft.github.data.local.room.dao.GitRepositoryDao
import com.nsoft.github.domain.model.GitRepository

@Database(
    entities = [GitRepository::class],
    version = DATABASE_VERSION
)
@TypeConverters(DatabaseConverters::class)
abstract class Database: RoomDatabase() {
    abstract fun gitRepositoryDao(): GitRepositoryDao
}

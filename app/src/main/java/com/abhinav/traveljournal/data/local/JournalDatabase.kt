package com.abhinav.traveljournal.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [JournalEntity::class],
    version = 1
)
abstract class JournalDatabase: RoomDatabase(){
    abstract fun journalDao(): JournalDao
}
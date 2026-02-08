package com.abhinav.traveljournal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [JournalEntity::class],
    version =4 ,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class JournalDatabase: RoomDatabase(){
    abstract fun journalDao(): JournalDao
    companion object{
        @Volatile
        private var INSTANCE: JournalDatabase? = null
        val MIGRATION_1_2 = object: Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE journal_table ADD COLUMN imageUri TEXT"
                )
            }
        }
        val MIGRATION_2_3 = object: Migration(2,3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE journal_table ADD COLUMN audioUri TEXT"
                )
                database.execSQL(
                    "ALTER TABLE journal_table ADD COLUMN latitude REAL"
                )
                database.execSQL(
                    "ALTER TABLE journal_table ADD COLUMN longitude REAL"
                )
            }
        }

        val MIGRATION_3_4 = object: Migration(3,4){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE journal_table ADD COLUMN imageUris TEXT NOT NULL DEFAULT ''"
                )
                database.execSQL(
                    "UPDATE journal_table SET imageUris = imageUri WHERE imageUri IS NOT NULL"
                )
            }
        }

        fun getDatabase(context: Context): JournalDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JournalDatabase::class.java,
                    "journal_database"
                ).addMigrations(MIGRATION_1_2,MIGRATION_2_3,MIGRATION_3_4).build()

                INSTANCE = instance
                instance
            }
        }
    }

}
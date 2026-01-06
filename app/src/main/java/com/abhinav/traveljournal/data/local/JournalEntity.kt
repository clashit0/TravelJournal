package com.abhinav.traveljournal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_table")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int =0,
    val title:String,
    val content:String,
    val createdAt: Long,

    val imageUri:String? = null,
    val audioUri:String? = null,

    //location
    val latitude: Double? = null,
    val longitude: Double? = null

)
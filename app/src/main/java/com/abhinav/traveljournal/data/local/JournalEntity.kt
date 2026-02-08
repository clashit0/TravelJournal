package com.abhinav.traveljournal.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_table")
data class JournalEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val content: String,

    // 🔴 OLD COLUMN — MUST BE DECLARED
    @ColumnInfo(name = "imageUri")
    val legacyImageUri: String? = null,

    // ✅ NEW COLUMN — USED BY APP
    @ColumnInfo(name = "imageUris")
    val imageUri: List<String> = emptyList(),

    val audioUri: String?,

    val latitude: Double?,

    val longitude: Double?,

    val createdAt: Long
)

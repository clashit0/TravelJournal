package com.abhinav.traveljournal.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(list: List<String>): String{
        return list.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String):List<String>{
        return if(value.isEmpty()){
            emptyList()
            }else{
                value.split(",")
            }
    }
}
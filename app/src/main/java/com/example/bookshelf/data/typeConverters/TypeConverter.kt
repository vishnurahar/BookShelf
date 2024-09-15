package com.example.bookshelf.data.typeConverters

import androidx.room.TypeConverter
import com.example.bookshelf.data.Location

class TypeConverter {
    @TypeConverter
    fun fromLocation(location: Location): String {
        return "${location.country},${location.region}"
    }

    @TypeConverter
    fun toLocation(locationString: String): Location {
        val data = locationString.split(",")
        return Location(0, data[0], data[1])
    }
}
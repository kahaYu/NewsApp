package com.yurakolesnikov.newsapp.db

import androidx.room.TypeConverter
import com.yurakolesnikov.newsapp.models.Source

class Converters { // Transform custom object Source to primitive type
    @TypeConverter
    fun fromSource(source: Source) : String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String) : Source {
        return Source(name, name)
    }
}
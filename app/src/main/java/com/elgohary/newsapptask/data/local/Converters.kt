package com.elgohary.newsapptask.data.local

import androidx.room.TypeConverter

object Converters {
    @TypeConverter
    @JvmStatic
    fun booleanToInt(value: Boolean?): Int? = value?.let { if (it) 1 else 0 }

    @TypeConverter
    @JvmStatic
    fun intToBoolean(value: Int?): Boolean? = value?.let { it == 1 }
}
package org.weilbach.splitbills.data.local

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class Converter {

    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun stringToBigDecimal(value: String): BigDecimal {
        return BigDecimal(value)
    }

    @TypeConverter
    fun bigDecimalToString(value: BigDecimal): String {
        return value.setScale(SCALE, ROUNDING_MODE).toString()
    }

    companion object {
        private const val SCALE = 2
        private val ROUNDING_MODE = RoundingMode.UP
    }
}
package org.weilbach.splitbills.data.local

import androidx.room.TypeConverter
import org.weilbach.splitbills.ROUNDING_MODE
import org.weilbach.splitbills.SCALE
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
        val v = BigDecimal(value)
        v.setScale(SCALE, ROUNDING_MODE)
        return v
    }

    @TypeConverter
    fun bigDecimalToString(value: BigDecimal): String {
        return value.setScale(SCALE, ROUNDING_MODE).toString()
    }
}
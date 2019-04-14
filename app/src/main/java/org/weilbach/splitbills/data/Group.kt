package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "groups")
data class Group constructor(
        @PrimaryKey @ColumnInfo(name = "name") val name: String) {
}
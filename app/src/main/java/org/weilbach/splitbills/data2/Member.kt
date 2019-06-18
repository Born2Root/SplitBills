package org.weilbach.splitbills.data2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member constructor(
        @ColumnInfo(name = "name") val name: String,
        @PrimaryKey @ColumnInfo(name = "email") val email: String
) {
}
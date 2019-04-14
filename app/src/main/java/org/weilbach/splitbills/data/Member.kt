package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member  constructor(
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "email") val email: String
){
    @PrimaryKey @ColumnInfo(name = "id") var id = name + email
}
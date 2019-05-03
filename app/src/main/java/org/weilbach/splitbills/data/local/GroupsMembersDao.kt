package org.weilbach.splitbills.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import org.weilbach.splitbills.data.GroupMember

@Dao
interface GroupsMembersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupMember(groupMember: GroupMember)
}
/*
package org.weilbach.splitbills.data.local

import androidx.room.*
import org.weilbach.splitbills.data.MemberData

@Dao
interface MemberDao {

    @Query("SELECT * FROM members")
    fun getMembers(): List<MemberData>

    @Query("SELECT * FROM members WHERE email = :memberEmail")
    fun getMemberByEmail(memberEmail: String): MemberData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMember(memberData: MemberData)

    @Update
    fun updateMember(memberData: MemberData): Int

    @Query("DELETE FROM members WHERE email = :memberEmail")
    fun deleteMemberByEmail(memberEmail: String): Int

    @Query("DELETE FROM members")
    fun deleteMembers()
}*/

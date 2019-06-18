/*
package org.weilbach.splitbills.data.local

import androidx.room.*
import org.weilbach.splitbills.data.GroupData

*/
/**
 * Data Access Object for the groups table.
 *//*

@Dao
interface GroupsDao {

    */
/**
     * Select all groups from the groups table.
     *
     * @return all groups.
     *//*

    @Query("SELECT * FROM groups")
    fun getGroups(): List<GroupData>

    */
/**
     * Select a group by name.
     *
     * @param groupName the group name.
     * @return the group with groupName.
     *//*

    @Query("SELECT * FROM groups WHERE name = :groupName")
    fun getGroupByName(groupName: String): GroupData?

    */
/**
     * Insert a group in the database. If the group already exists, replace it.
     *
     * @param groupData the group to be inserted.
     *//*

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroup(group: GroupData)

    */
/**
     * Update a group.
     *
     * @param groupData group to be updated
     * @return the number of groups updated. This should always be 1.
     *//*

    @Update
    fun updateGroup(group: GroupData): Int

    */
/**
     * Delete a group by name.
     *
     * @return the number of groups deleted. This should always be 1.
     *//*

    @Query("DELETE FROM groups WHERE name = :groupName")
    fun deleteGroupByName(groupName: String): Int

    */
/**
     * Delete all groups.
     *//*

    @Query("DELETE FROM groups")
    fun deleteGroups()
}*/

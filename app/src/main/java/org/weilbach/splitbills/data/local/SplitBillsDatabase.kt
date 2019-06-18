/*
package org.weilbach.splitbills.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.weilbach.splitbills.data.*

@Database(
        version = 1,
        entities = [
            GroupData::class,
            MemberData::class,
            GroupMemberData::class,
            BillData::class,
            DebtorData::class,
            */
/*AmountData::class,*//*

            LastSendData::class,
            LastReceivedData::class
        ],
        exportSchema = false
)
abstract class SplitBillsDatabase : RoomDatabase() {

    abstract fun groupsDao(): GroupsDao

    abstract fun memberDao(): MemberDao

    abstract fun groupsMembersDao(): GroupsMembersDao

    abstract fun billDao(): BillDao

    abstract fun debtorsDao(): DebtorsDao

    */
/*abstract fun amountsDao(): AmountsDao*//*


    abstract fun lastSendDao(): LastSendDao

    abstract fun lastReceivedDao(): LastReceivedDao

    companion object {
        private var INSTANCE: SplitBillsDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): SplitBillsDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            SplitBillsDatabase::class.java, "SplitBills.db")
                            .build()
                }
                return INSTANCE!!
            }
        }
    }
}*/

package org.weilbach.splitbills.data2.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.weilbach.splitbills.data2.*

@Database(
        version = 1,
        entities = [
            Group::class,
            Member::class,
            GroupMember::class,
            Bill::class,
            Debtor::class
        ],
        exportSchema = false
)
abstract class SplitBillsDatabase : RoomDatabase() {

    abstract fun groupDao(): GroupDao

    abstract fun memberDao(): MemberDao

    abstract fun groupMemberDao(): GroupMemberDao

    abstract fun billDao(): BillDao

    abstract fun debtorDao(): DebtorDao

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
}
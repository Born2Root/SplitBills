package org.weilbach.splitbills.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.jraska.livedata.test
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.weilbach.splitbills.data.*
import org.weilbach.splitbills.data.local.SplitBillsDatabase

@RunWith(AndroidJUnit4::class)
class GroupDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: SplitBillsDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getContext(),
                SplitBillsDatabase::class.java).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertGroupAndGetByName() {
        database.groupDao().insert(DEFAULT_GROUP)

        val loaded = database.groupDao().getGroupByName(DEFAULT_GROUP.name)

        loaded
                .test()
                .awaitValue()
                .assertHasValue()
                .assertValue(DEFAULT_GROUP)
    }

    @Test
    fun insertGroupWithBillsWithDebtorsAndGetByName() {
        database.groupDao().insert(DEFAULT_GROUP)
        database.memberDao().insert(MEMBER1)
        database.memberDao().insert(MEMBER2)
        database.groupMemberDao().insert(GroupMember(DEFAULT_GROUP.name, MEMBER1.email))
        database.groupMemberDao().insert(GroupMember(DEFAULT_GROUP.name, MEMBER2.email))
        database.billDao().insert(BILL1)
        database.billDao().insert(BILL2)
        database.debtorDao().insert(BILL1_DEBTOR1)
        database.debtorDao().insert(BILL1_DEBTOR2)
        database.debtorDao().insert(BILL2_DEBTOR1)

        val loaded = database.groupDao().getGroupWithBillsWithDebtorsByName(DEFAULT_GROUP.name)

        loaded.test().awaitValue().assertHasValue().map { it.group }.assertValue(DEFAULT_GROUP)

        loaded.test().map { it.bills[0].bill }.assertValue(BILL1)
        loaded.test().map { it.bills[0].debtors[0] }.assertValue(BILL1_DEBTOR1)
        loaded.test().map { it.bills[0].debtors[1] }.assertValue(BILL1_DEBTOR2)

        loaded.test().map { it.bills[1].bill }.assertValue(BILL2)
        loaded.test().map { it.bills[1].debtors[0] }.assertValue(BILL2_DEBTOR1)
    }

    @Test
    fun insertGroupWithMembersAndBillsWithDebtorsAndGetByName() {
        database.groupDao().insert(DEFAULT_GROUP)
        database.memberDao().insert(MEMBER1)
        database.memberDao().insert(MEMBER2)
        database.groupMemberDao().insert(GROUP_MEMBER1)
        database.groupMemberDao().insert(GROUP_MEMBER2)
        database.billDao().insert(BILL1)
        database.billDao().insert(BILL2)
        database.debtorDao().insert(BILL1_DEBTOR1)
        database.debtorDao().insert(BILL1_DEBTOR2)
        database.debtorDao().insert(BILL2_DEBTOR1)

        val loaded = database.groupDao().getGroupWithMembersAndBillsWithDebtorsByName(DEFAULT_GROUP.name)

        loaded.test().awaitValue().assertHasValue().map { it.group }.assertValue(DEFAULT_GROUP)

        loaded.test().map { it.members[0] }.assertValue(GROUP_MEMBER1)
        loaded.test().map { it.members[1] }.assertValue(GROUP_MEMBER2)

        loaded.test().map { it.bills[0].bill }.assertValue(BILL1)
        loaded.test().map { it.bills[0].debtors[0] }.assertValue(BILL1_DEBTOR1)
        loaded.test().map { it.bills[0].debtors[1] }.assertValue(BILL1_DEBTOR2)

        loaded.test().map { it.bills[1].bill }.assertValue(BILL2)
        loaded.test().map { it.bills[1].debtors[0] }.assertValue(BILL2_DEBTOR1)
    }

    @Test
    fun insertGroupWithMembersAndBillsWithDebtorsAndDeleteGroup() {
        database.groupDao().insert(DEFAULT_GROUP)
        database.memberDao().insert(MEMBER1)
        database.memberDao().insert(MEMBER2)
        database.groupMemberDao().insert(GroupMember(DEFAULT_GROUP.name, MEMBER1.email))
        database.groupMemberDao().insert(GroupMember(DEFAULT_GROUP.name, MEMBER2.email))
        database.billDao().insert(BILL1)
        database.billDao().insert(BILL2)
        database.debtorDao().insert(BILL1_DEBTOR1)
        database.debtorDao().insert(BILL1_DEBTOR2)
        database.debtorDao().insert(BILL2_DEBTOR1)

        database.groupDao().delete(DEFAULT_GROUP)
        // TODO: ASSERT
    }

    companion object {
        private val DEFAULT_GROUP = Group("group")
        private val MEMBER1_MAIL = "mail1"
        private val MEMBER2_MAIL = "mail2"
        private val MEMBER1 = Member("name1", MEMBER1_MAIL)
        private val MEMBER2 = Member("name2", MEMBER2_MAIL)
        private val GROUP_MEMBER1 = GroupMember(DEFAULT_GROUP.name, MEMBER1.email)
        private val GROUP_MEMBER2 = GroupMember(DEFAULT_GROUP.name, MEMBER2.email)
        private val BILL1 = Bill("00:01", "bill1", "7", "EUR", MEMBER1_MAIL, DEFAULT_GROUP.name, true)
        private val BILL2 = Bill("00:02", "bill2", "2", "EUR", MEMBER2_MAIL, DEFAULT_GROUP.name, true)
        private val BILL1_DEBTOR1 = Debtor(BILL1.id, MEMBER1_MAIL)
        private val BILL1_DEBTOR2 = Debtor(BILL1.id, MEMBER2_MAIL)
        private val BILL2_DEBTOR1 = Debtor(BILL2.id, MEMBER1_MAIL)
    }
}
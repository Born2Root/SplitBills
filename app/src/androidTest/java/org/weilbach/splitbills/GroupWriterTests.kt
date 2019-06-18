/*
package org.weilbach.splitbills

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.IdlingRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.weilbach.splitbills.data.*
import org.weilbach.splitbills.data.local.*
import org.weilbach.splitbills.data.source.*
import org.weilbach.splitbills.util.AppExecutors
import org.weilbach.splitbills.util.EspressoIdlingResource
import org.weilbach.splitbills.utils.SingleExecutors


@RunWith(AndroidJUnit4::class)
class GroupWriterTests {

    */
/*private lateinit var database: SplitBillsDatabase
    private lateinit var membersRepository: MembersRepository
    private lateinit var groupsMembersRepository: GroupsMembersRepository
    private lateinit var billsRepository: BillsRepository
    private lateinit var debtorsRepository: DebtorsRepository
    private lateinit var amountsRepository: AmountsRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                SplitBillsDatabase::class.java)
                .build()

        MembersLocalDataSource.clearInstance()
        val membersLocalDataSource = MembersLocalDataSource.getInstance(SingleExecutors(), database.memberDao())
        MembersRepository.destroyInstance()
        membersRepository = MembersRepository.getInstance(membersLocalDataSource)

        GroupsMembersLocalDataSource.clearInstance()
        val groupsMembersLocalDataSource = GroupsMembersLocalDataSource.getInstance(SingleExecutors(), database.groupsMembersDao())
        GroupsMembersRepository.destroyInstance()
        groupsMembersRepository = GroupsMembersRepository.getInstance(groupsMembersLocalDataSource)

        BillsLocalDataSource.clearInstance()
        val billsLocalDataSource = BillsLocalDataSource.getInstance(SingleExecutors(), database.billDao())
        BillsRepository.destroyInstance()
        billsRepository = BillsRepository.getInstance(billsLocalDataSource)

        DebtorsLocalDataSource.clearInstance()
        val debtorsLocalDataSource = DebtorsLocalDataSource.getInstance(SingleExecutors(), database.debtorsDao())
        DebtorsRepository.destroyInstance()
        debtorsRepository = DebtorsRepository.getInstance(debtorsLocalDataSource)

        AmountsLocalDataSource.clearInstance()
        val amountsLocalDataSource = AmountsLocalDataSource.getInstance(SingleExecutors(), database.amountsDao())
        AmountsRepository.destroyInstance()
        amountsRepository = AmountsRepository.getInstance(amountsLocalDataSource)
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    //TODO: Fix, is not asserting
    @Test
    fun writeGroupToXml_validGroup_createCorrectFile() {
        createGroup()
        EspressoIdlingResource.increment()
        writeGroupToXml(GROUP,
                membersRepository,
                billsRepository,
                groupsMembersRepository,
                debtorsRepository,
                amountsRepository,
                AppExecutors(),
                object : GroupWriterCallback {
                    override fun onSuccess(xml: String) {
                        EspressoIdlingResource.decrement()
                        Assert.assertEquals(xml, VALID_XML)
                    }

                    override fun onFailure() {
                        EspressoIdlingResource.decrement()
                        Assert.fail("No xml created.")
                    }
                })
    }

    private fun createGroup() {
        with(database) {
            groupsDao().insertGroup(GROUP)

            memberDao().insertMember(MEMBER1)
            memberDao().insertMember(MEMBER2)
            memberDao().insertMember(MEMBER3)

            groupsMembersDao().insertGroupMember(GROUP_MEMBER1)
            groupsMembersDao().insertGroupMember(GROUP_MEMBER2)
            groupsMembersDao().insertGroupMember(GROUP_MEMBER3)

            billDao().insertBill(BILL1)
            billDao().insertBill(BILL2)
            billDao().insertBill(BILL3)

            debtorsDao().insertDebtor(DEBTOR1_BILL1)
            debtorsDao().insertDebtor(DEBTOR2_BILL1)
            debtorsDao().insertDebtor(DEBTOR3_BILL1)
            debtorsDao().insertDebtor(DEBTOR1_BILL2)
            debtorsDao().insertDebtor(DEBTOR1_BILL3)

            amountsDao().insertAmount(AMOUNT1_BILL1)
            amountsDao().insertAmount(AMOUNT2_BILL1)
            amountsDao().insertAmount(AMOUNT1_BILL2)
            amountsDao().insertAmount(AMOUNT1_BILL3)
        }
    }

    companion object {
        private const val DEFAULT_NAME1 = "default name1"
        private const val DEFAULT_EMAIL1 = "default1@mail.com"
        private const val DEFAULT_NAME2 = "default name2"
        private const val DEFAULT_EMAIL2 = "default2@mail.com"
        private const val DEFAULT_NAME3 = "default name3"
        private const val DEFAULT_EMAIL3 = "default3@mail.com"
        private const val DEFAULT_GROUP_NAME = "default group"
        private const val DATE1 = "date_time1"
        private const val DATE2 = "date_time2"
        private const val DATE3 = "date_time3"
        private const val DESCRIPTION1 = "description1"
        private const val DESCRIPTION2 = "description2"
        private const val DESCRIPTION3 = "description3"
        private const val VALID1 = true
        private const val VALID2 = true
        private const val VALID3 = false
        private const val AMOUNT1_AMOUNT1 = "22"
        private const val AMOUNT1_VALID1 = true
        private const val AMOUNT1_AMOUNT2 = "2"
        private const val AMOUNT1_VALID2 = false
        private const val AMOUNT2_AMOUNT1 = "1.4"
        private const val AMOUNT2_VALID1 = true
        private const val AMOUNT3_AMOUNT1 = "2.56"
        private const val AMOUNT3_VALID1 = true

        private val GROUP = GroupData(DEFAULT_GROUP_NAME)
        private val MEMBER1 = MemberData(DEFAULT_NAME1, DEFAULT_EMAIL1)
        private val MEMBER2 = MemberData(DEFAULT_NAME2, DEFAULT_EMAIL2)
        private val MEMBER3 = MemberData(DEFAULT_NAME3, DEFAULT_EMAIL3)

        private val GROUP_MEMBER1 = GroupMemberData(DEFAULT_GROUP_NAME, DEFAULT_EMAIL1)
        private val GROUP_MEMBER2 = GroupMemberData(DEFAULT_GROUP_NAME, DEFAULT_EMAIL2)
        private val GROUP_MEMBER3 = GroupMemberData(DEFAULT_GROUP_NAME, DEFAULT_EMAIL3)

        private val BILL1 = BillData(DATE1, DESCRIPTION1, DEFAULT_EMAIL1, DEFAULT_GROUP_NAME, VALID1)
        private val BILL2 = BillData(DATE2, DESCRIPTION2, DEFAULT_EMAIL1, DEFAULT_GROUP_NAME, VALID2)
        private val BILL3 = BillData(DATE3, DESCRIPTION3, DEFAULT_EMAIL3, DEFAULT_GROUP_NAME, VALID3)

        private val DEBTOR1_BILL1 = DebtorData(BILL1.id, DEFAULT_EMAIL1)
        private val DEBTOR2_BILL1 = DebtorData(BILL1.id, DEFAULT_EMAIL2)
        private val DEBTOR3_BILL1 = DebtorData(BILL1.id, DEFAULT_EMAIL3)

        private val AMOUNT1_BILL1 = AmountData(BILL1.id, AMOUNT1_AMOUNT1, AMOUNT1_VALID1)
        private val AMOUNT2_BILL1 = AmountData(BILL1.id, AMOUNT1_AMOUNT2, AMOUNT1_VALID2)

        private val DEBTOR1_BILL2 = DebtorData(BILL2.id, DEFAULT_EMAIL1)

        private val AMOUNT1_BILL2 = AmountData(BILL2.id, AMOUNT2_AMOUNT1, AMOUNT2_VALID1)

        private val DEBTOR1_BILL3 = DebtorData(BILL3.id, DEFAULT_EMAIL2)

        private val AMOUNT1_BILL3 = AmountData(BILL3.id, AMOUNT3_AMOUNT1, AMOUNT3_VALID1)

        private val VALID_XML = ""
    }*//*

}*/

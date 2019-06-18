/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.weilbach.splitbills

import android.content.Context
import org.weilbach.splitbills.data.local.*
import org.weilbach.splitbills.data.source.*

import org.weilbach.splitbills.util.AppExecutors

/**
 * Enables injection of mock implementations for
 * [GroupsDataSource] at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
object Injection {

    fun provideBillsRepository(context: Context): BillsRepository {
        val database = SplitBillsDatabase.getInstance(context)
        return BillsRepository.getInstance(
                BillsLocalDataSource.getInstance(AppExecutors(),
                        database.billDao()))
    }

    fun provideGroupsRepository(context: Context): GroupsRepository {
        val database = SplitBillsDatabase.getInstance(context)
        return GroupsRepository.getInstance(
                GroupsLocalDataSource.getInstance(AppExecutors(),
                        database.groupsDao()))
    }

/*    fun provideAmountsRepository(context: Context): AmountsRepository {
        val database = SplitBillsDatabase.getInstance(context)
        return AmountsRepository.getInstance(
                AmountsLocalDataSource.getInstance(AppExecutors(),
                        database.amountsDao()))
    }*/

    fun provideDebtorsRepository(context: Context): DebtorsRepository {
        val database = SplitBillsDatabase.getInstance(context)
        return DebtorsRepository.getInstance(
                DebtorsLocalDataSource.getInstance(AppExecutors(),
                        database.debtorsDao()))
    }

    fun provideMembersRepository(context: Context): MembersRepository {
        val database = SplitBillsDatabase.getInstance(context)
        return MembersRepository.getInstance(
                MembersLocalDataSource.getInstance(AppExecutors(),
                        database.memberDao()))
    }

    fun provideGroupsMembersRepository(context: Context): GroupsMembersRepository {
        val database = SplitBillsDatabase.getInstance(context)
        return GroupsMembersRepository.getInstance(
                GroupsMembersLocalDataSource.getInstance(AppExecutors(),
                        database.groupsMembersDao()))
    }
}

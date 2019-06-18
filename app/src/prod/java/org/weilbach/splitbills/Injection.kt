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
import org.weilbach.splitbills.data2.local.*
import org.weilbach.splitbills.data2.source.*
import org.weilbach.splitbills.util.AppExecutors

/**
 * Enables injection of production implementations for
 * [GroupsDataSource] at compile time.
 */
object Injection {

    fun provideBillRepository(context: Context): BillRepository {
        val database = SplitBillsDatabase.getInstance(context)
        return BillRepository.getInstance(
                BillLocalDataSource.getInstance(AppExecutors(),
                        database.billDao()))
    }

    fun provideGroupRepository(context: Context): GroupRepository {
        val database = SplitBillsDatabase.getInstance(context)
        return GroupRepository.getInstance(
                GroupLocalDataSource.getInstance(
                        AppExecutors(),
                        database.groupDao()))
    }

    fun provideDebtorRepository(context: Context): DebtorRepository {
        val database = SplitBillsDatabase.getInstance(context)
        return DebtorRepository.getInstance(
                DebtorLocalDataSource.getInstance(AppExecutors(),
                        database.debtorDao()))
    }

    fun provideMemberRepository(context: Context): MemberRepository {
        val database = SplitBillsDatabase.getInstance(context)
        return MemberRepository.getInstance(
                MemberLocalDataSource.getInstance(AppExecutors(),
                        database.memberDao()))
    }

    fun provideGroupMemberRepository(context: Context): GroupMemberRepository {
        val database = SplitBillsDatabase.getInstance(context)
        return GroupMemberRepository.getInstance(
                GroupMemberLocalDataSource.getInstance(AppExecutors(),
                        database.groupMemberDao()))
    }
}

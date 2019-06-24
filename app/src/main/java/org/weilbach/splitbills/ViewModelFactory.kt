/*
 *  Copyright 2017 Google Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.weilbach.splitbills

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.weilbach.splitbills.addeditbill.AddEditBillViewModel
import org.weilbach.splitbills.addeditgroup.AddEditGroupViewModel
import org.weilbach.splitbills.addmember.AddMemberViewModel
import org.weilbach.splitbills.balances.BalancesViewModel
import org.weilbach.splitbills.billdetail.BillDetailViewModel
import org.weilbach.splitbills.bills.BillsViewModel
import org.weilbach.splitbills.data.source.*
import org.weilbach.splitbills.group.GroupViewModel
import org.weilbach.splitbills.util.AppExecutors


/**
 * A creator is used to inject the product ID into the ViewModel
 *
 *
 * This creator is to showcase how to inject dependencies into ViewModels. It's not
 * actually necessary in this case, as the product ID can be passed in a public method.
 */
class ViewModelFactory private constructor(
        private val groupsRepository: GroupRepository,
        private val billsRepository: BillRepository,
        private val membersRepository: MemberRepository,
        private val groupsMembersRepository: GroupMemberRepository,
        private val debtorsRepository: DebtorRepository,
        private val appContext: Context
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(BillDetailViewModel::class.java) ->
                        BillDetailViewModel(
                                billsRepository,
                                membersRepository,
                                groupsMembersRepository,
                                appContext)
                    isAssignableFrom(BalancesViewModel::class.java) ->
                        BalancesViewModel(
                                billsRepository,
                                membersRepository,
                                groupsMembersRepository,
                                debtorsRepository,
                                groupsRepository)

                    isAssignableFrom(AddEditBillViewModel::class.java) ->
                        AddEditBillViewModel(
                                groupsRepository,
                                billsRepository,
                                debtorsRepository,
                                membersRepository,
                                groupsMembersRepository,
                                AppExecutors(),
                                appContext)

                    isAssignableFrom(BillsViewModel::class.java) ->
                        BillsViewModel(
                                billsRepository,
                                membersRepository,
                                debtorsRepository,
                                groupsMembersRepository,
                                groupsRepository,
                                appContext)

                    isAssignableFrom(AddMemberViewModel::class.java) ->
                        AddMemberViewModel(appContext)

                    isAssignableFrom(AddEditGroupViewModel::class.java) ->
                        AddEditGroupViewModel(
                                groupsRepository,
                                membersRepository,
                                groupsMembersRepository,
                                AppExecutors(),
                                appContext)

                    isAssignableFrom(GroupViewModel::class.java) ->
                        GroupViewModel(
                                groupsRepository,
                                membersRepository,
                                groupsMembersRepository,
                                billsRepository,
                                debtorsRepository,
                                AppExecutors(),
                                appContext)
                    else ->
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            } as T

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application) =
                INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                    INSTANCE ?: ViewModelFactory(
                            Injection.provideGroupRepository(application.applicationContext),
                            Injection.provideBillRepository(application.applicationContext),
                            Injection.provideMemberRepository(application.applicationContext),
                            Injection.provideGroupMemberRepository(application.applicationContext),
                            Injection.provideDebtorRepository(application.applicationContext),
                            application.applicationContext)
                            .also { INSTANCE = it }
                }


        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}

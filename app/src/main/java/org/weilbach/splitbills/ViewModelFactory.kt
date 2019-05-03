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
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.weilbach.splitbills.addeditgroup.AddEditGroupActivity
import org.weilbach.splitbills.addeditgroup.AddEditGroupViewModel
import org.weilbach.splitbills.addmember.AddMemberViewModel
import org.weilbach.splitbills.data.source.GroupsMembersRepository
import org.weilbach.splitbills.data.source.GroupsRepository
import org.weilbach.splitbills.data.source.MembersRepository
import org.weilbach.splitbills.group.GroupViewModel


/**
 * A creator is used to inject the product ID into the ViewModel
 *
 *
 * This creator is to showcase how to inject dependencies into ViewModels. It's not
 * actually necessary in this case, as the product ID can be passed in a public method.
 */
class ViewModelFactory private constructor(
        private val groupsRepository: GroupsRepository,
        private val membersRepository: MembersRepository,
        private val groupsMembersRepository: GroupsMembersRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
/*                    isAssignableFrom(StatisticsViewModel::class.java) ->
                        StatisticsViewModel(tasksRepository)
                    isAssignableFrom(TaskDetailViewModel::class.java) ->
                        TaskDetailViewModel(tasksRepository)
                    isAssignableFrom(AddEditTaskViewModel::class.java) ->
                        AddEditTaskViewModel(tasksRepository)*/
                    isAssignableFrom(AddMemberViewModel::class.java) ->
                        AddMemberViewModel()
                    isAssignableFrom(AddEditGroupViewModel::class.java) ->
                        AddEditGroupViewModel(groupsRepository,
                                membersRepository,
                                groupsMembersRepository)
                    isAssignableFrom(GroupViewModel::class.java) ->
                        GroupViewModel(groupsRepository)
                    else ->
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            } as T

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application) =
                INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                    INSTANCE ?: ViewModelFactory(
                            Injection.provideGroupsRepository(application.applicationContext),
                            Injection.provideMembersRepository(application.applicationContext),
                            Injection.provideGroupsMembersRepository(application.applicationContext))
                            .also { INSTANCE = it }
                }


        @VisibleForTesting fun destroyInstance() {
            INSTANCE = null
        }
    }
}

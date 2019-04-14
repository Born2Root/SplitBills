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
import org.weilbach.splitbills.data.local.SplitBillsDatabase
import org.weilbach.splitbills.data.local.GroupsLocalDataSource
import org.weilbach.splitbills.data.remote.GroupsRemoteDataSource
import org.weilbach.splitbills.data.source.GroupsRepository
import org.weilbach.splitbills.util.AppExecutors

/**
 * Enables injection of production implementations for
 * [GroupsDataSource] at compile time.
 */
object Injection {

    fun provideGroupsRepository(context: Context): GroupsRepository {
        val database = SplitBillsDatabase.getInstance(context)
        return GroupsRepository.getInstance(GroupsRemoteDataSource,
                GroupsLocalDataSource.getInstance(AppExecutors(), database.groupsDao()))
    }
}

package org.weilbach.splitbills.data2.source

import androidx.lifecycle.LiveData

interface BaseDataSource<T> {

    fun getAll(): LiveData<List<T>>

    fun save(item: T)

    fun deleteAll()

    fun refresh()
}
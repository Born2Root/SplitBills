/*
package org.weilbach.splitbills.util

class BillContainer {

    private val _items: LinkedHashMap<String, Bill> = LinkedHashMap()

    val items: MutableCollection<Bill>
        get() = _items.values

    val size: Int
        get() = _items.size

    fun contains(item: Bill) = _items.contains(item.id)

    fun add(item: Bill): Boolean {
        if (contains(item)) {
            return false
        }
        _items[item.id] = item
        return true
    }

    fun get(id: String) = _items[id]

    fun remove(id: String) = _items.remove(id)

    fun forEach(operation: (Bill) -> Unit) {
        _items.forEach {
            operation(it.value)
        }
    }
}*/

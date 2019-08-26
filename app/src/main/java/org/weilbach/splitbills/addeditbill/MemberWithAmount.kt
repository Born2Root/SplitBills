package org.weilbach.splitbills.addeditbill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import org.weilbach.splitbills.data.local.Converter
import org.weilbach.splitbills.util.Event
import java.math.BigDecimal

class MemberWithAmount(val name: String,
                       val email: String,
                       val parent: AddEditBillViewModel) {

    private val converter = Converter()

    var amount: BigDecimal = BigDecimal.ZERO
        set(value) {
            _amountWithCurrency.value = converter.bigDecimalToString(value) + " " + parent.currency.value?.symbol
            field = value
        }

    private val _changeAmountEvent = MutableLiveData<Event<MemberWithAmount>>()
    val changeAmountEvent: LiveData<Event<MemberWithAmount>>
        get() = _changeAmountEvent

    private val _removeMemberEvent = MutableLiveData<Event<String>>()
    val removeMemberEvent: LiveData<Event<String>>
        get() = _removeMemberEvent

    private val _amountWithCurrency = MutableLiveData<String>().apply {
        value = converter.bigDecimalToString(BigDecimal.ZERO) + " " + parent.currency.value?.symbol
    }
    val amountWithCurrency: LiveData<String>
        get() = _amountWithCurrency

    val amountButtonValid = MediatorLiveData<Boolean>().apply {
        // FIXME: not working
        addSource(parent.amount) { amount ->
            !amount.isNullOrBlank()
        }
    }

    fun onAmountButtonClicked() {
        if (parent.amount.value.isNullOrBlank()) {
            return
        }
        _changeAmountEvent.value = Event(this)
    }

    fun onRemoveButtonClicked() {
        _removeMemberEvent.value = Event(email)
    }
}
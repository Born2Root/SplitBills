package org.weilbach.splitbills.addeditbill

import org.weilbach.splitbills.MATH_CONTEXT
import org.weilbach.splitbills.ROUNDING_MODE
import org.weilbach.splitbills.SCALE
import org.weilbach.splitbills.data.Debtor
import org.weilbach.splitbills.data.Member
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

class DebtorItemContainer(
        private val listener: DebtorItemContainerListener,
        currency: Currency,
        splitMode: AddEditBillViewModel.SplitMode
) {

    private val debtorItems = LinkedList<DebtorItemViewModel>()
    private val debtorMap = HashMap<String, DebtorItemViewModel>()

    var splitMode = AddEditBillViewModel.SplitMode.ABSOLUTE
        set(value) {
            field = value
            setDebtorsSplitMode(value)
        }

    var amount = BigDecimal.ZERO
        set(value) {
            field = value
            isAmountValid = true
            setDebtorsAmount(value)
        }

    var isAmountValid = false
        set(value) {
            field = value
            setDebtorsAmountValid(value)
        }

    var currency = Currency.getInstance("EUR")
        set(value) {
            field = value
            setDebtorsCurrency(value)
        }

    private var amountToBalance = BigDecimal.ZERO

    init {
        this.currency = currency
        this.splitMode = splitMode
    }

    private fun setDebtorsCurrency(currency: Currency) {
        debtorItems.forEach { debtor ->
            debtor.currency.value = currency
        }
    }

    private fun setDebtorsSplitMode(splitMode: AddEditBillViewModel.SplitMode) {
        debtorItems.forEach { debtor ->
            debtor.splitMode.value = splitMode
        }
        setDebtorsAmount(amount)
    }

    private fun setDebtorsAmount(amount: BigDecimal) {
        when (splitMode) {
            AddEditBillViewModel.SplitMode.ABSOLUTE -> {
                setInitialAbsoluteAmountForDebtorItems(amount)
            }

            AddEditBillViewModel.SplitMode.PERCENTAGE -> {
                setInitialPercentageAmountForDebtorItems()
            }
        }
    }

    private fun setInitialAbsoluteAmountForDebtorItems(amount: BigDecimal) {
         val debtorAmount = if (debtorItems.size == 0) {
             BigDecimal.ZERO
         } else {
             amount.divide(BigDecimal(debtorItems.size), MATH_CONTEXT).setScale(SCALE, ROUNDING_MODE)
         }

        val amounts = LinkedList<BigDecimal>()
         debtorItems.forEach { _ ->
             amounts.add(debtorAmount)
         }
         val balancedAmounts = balanceAmounts(amount, amounts)

        var i = 0
        debtorItems.forEach { entry ->
            entry.amount.value = balancedAmounts[i]
            i++
        }
    }

    private fun setInitialPercentageAmountForDebtorItems() {
        val debtorPercentage = if (debtorItems.size == 0) {
            BigDecimal.ZERO
        } else {
             BigDecimal(100).divide(BigDecimal(debtorItems.size), MATH_CONTEXT).setScale(0, ROUNDING_MODE)
         }

         val percentages = LinkedList<BigDecimal>()
         debtorItems.forEach { _ ->
             percentages.add(debtorPercentage)
         }
        val balancedPercentages = balancePercentages(percentages)

        var i = 0
        debtorItems.forEach { entry ->
            entry.amount.value = balancedPercentages[i]
            i++
        }
    }

    private fun balanceAmounts(amount: BigDecimal, amounts: LinkedList<BigDecimal>): List<BigDecimal> {
        if (debtorItems.size == 0) return emptyList()

        val step = BigDecimal("0.01") // .setScale(SCALE)
        // TODO: Make this a injection
        val random = Random()

        balanceLoop@ while (true) {
            var amountSum = BigDecimal.ZERO
            amounts.forEach { a ->
                amountSum = amountSum.add(a)
            }
            // debtorAmountSum.setScale(SCALE, ROUNDING_MODE)

            val index = random.nextInt(debtorItems.size)
            when (amountSum.compareTo(amount)) {
                1 -> {
                    val a = amounts[index]
                    amounts[index] = a.subtract(step)
                }
                -1 -> {
                    val a = amounts[index]
                    amounts[index] = a.add(step)
                 }
                else -> {
                    break@balanceLoop
                }
            }
        }
        return amounts
    }

    private fun balancePercentages(percentages: LinkedList<BigDecimal>): List<BigDecimal> {
        if (debtorItems.size == 0) return emptyList()

        val step = BigDecimal(1) // .setScale(SCALE)
        // TODO: Make this a injection
        val random = Random()

        balanceLoop@ while (true) {
            var percentageSum = BigDecimal.ZERO
            percentages.forEach { a ->
                percentageSum = percentageSum.add(a)
            }
            // debtorAmountSum.setScale(SCALE, ROUNDING_MODE)

            val index = random.nextInt(debtorItems.size)
            when (percentageSum.compareTo(BigDecimal(100))) {
                1 -> {
                    val a = percentages[index]
                    percentages[index] = a.subtract(step)
                }
                -1 -> {
                    val a = percentages[index]
                    percentages[index] = a.add(step)
                }
                else -> {
                    break@balanceLoop
                }
            }
        }
        return percentages
    }



    private fun setDebtorsAmountValid(value: Boolean) {
        debtorItems.forEach { debtor ->
            debtor.isAmountValid.value = value
        }
    }

    fun add(member: Member) {
        val debtor = DebtorItemViewModel(member.name, member.email)
        debtor.amount.value = BigDecimal.ZERO
        debtor.splitMode.value = splitMode
        debtor.isAmountValid.value = isAmountValid
        debtor.currency.value = currency

        debtor.amount.observeForever {
            amountToBalance = amountToBalance()
            listener.onAmountToBalanceChanged(amountToBalance)
        }

        debtorItems.add(debtor)
        debtorMap[debtor.email] = debtor
        listener.onDebtorItemsChanged(debtorItems)
    }

    fun add(members: List<Member>) {
        members.forEach { member ->
            val debtor = DebtorItemViewModel(member.name, member.email)
            debtor.amount.value = BigDecimal.ZERO
            debtor.splitMode.value = splitMode
            debtor.isAmountValid.value = isAmountValid
            debtor.currency.value = currency

            debtor.amount.observeForever {
                amountToBalance = amountToBalance()
                listener.onAmountToBalanceChanged(amountToBalance)
            }

            debtorItems.add(debtor)
            debtorMap[debtor.email] = debtor
        }
        listener.onDebtorItemsChanged(debtorItems)
    }

    fun remove(debtorItem: DebtorItemViewModel) {
        // TODO: Remove observer
        debtorMap.remove(debtorItem.email)
        debtorItems.remove(debtorItem)
        amountToBalance = amountToBalance()
        listener.onDebtorItemsChanged(debtorItems)
        listener.onAmountToBalanceChanged(amountToBalance)
    }

    fun remove(email: String) {
        // TODO: Remove observer
        val debtorItem = debtorMap.remove(email)
        debtorItems.remove(debtorItem)
        amountToBalance = amountToBalance()
        listener.onDebtorItemsChanged(debtorItems)
        listener.onAmountToBalanceChanged(amountToBalance)
    }

    fun clear() {
        // TODO: Remove observer
        debtorMap.clear()
        debtorItems.clear()
        amountToBalance = amountToBalance()
        listener.onDebtorItemsChanged(debtorItems)
        listener.onAmountToBalanceChanged(amountToBalance)
    }

    private fun amountToBalance(): BigDecimal {
        var debtorAmounts = BigDecimal.ZERO
        debtorItems.forEach { debtor ->
            debtor.amount.value?.let {
                debtorAmounts = debtorAmounts.add(it)
            }
        }
        return  when (splitMode) {
            AddEditBillViewModel.SplitMode.ABSOLUTE -> amount.subtract(debtorAmounts)
            AddEditBillViewModel.SplitMode.PERCENTAGE -> BigDecimal(100).subtract(debtorAmounts)
        }
    }

    fun debtorItemsToDebtors(billId: String): List<Debtor> {
        return when (splitMode) {
            AddEditBillViewModel.SplitMode.ABSOLUTE -> {
                val debtors = debtorItems.map { debtor ->
                    val currentAmount = debtor.amount.value ?: BigDecimal.ZERO
                    Debtor(billId, debtor.email, currentAmount)
                }
                debtors
            }
            AddEditBillViewModel.SplitMode.PERCENTAGE -> {
                val amount = amount ?: BigDecimal.ZERO

                var amounts = debtorItems.map { debtor ->
                    val currentAmount = debtor.amount.value ?: BigDecimal.ZERO
                    amount.divide(BigDecimal(100), MATH_CONTEXT).multiply(currentAmount)
                }

                amounts = balanceAmounts(amount, LinkedList(amounts))
                val debtors = LinkedList<Debtor>()
                var i = 0
                debtorItems.forEach { debtorItemViewModel ->
                    debtors.add(Debtor(billId, debtorItemViewModel.email, amounts[i]))
                    i++
                }

                debtors
            }
        }
    }

    fun contains(email: String) = debtorMap.contains(email)
}
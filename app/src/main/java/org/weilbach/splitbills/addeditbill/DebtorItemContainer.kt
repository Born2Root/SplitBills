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
        currency: Currency
) {

    private val debtorItems = LinkedList<DebtorItemViewModel>()
    private val debtorMap = HashMap<String, DebtorItemViewModel>()


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
    private var percentageToBalance = BigDecimal.ZERO

    init {
        this.currency = currency
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
    }

    private fun setDebtorsAmount(amount: BigDecimal) {
        setDebtorsSplitMode(AddEditBillViewModel.SplitMode.PERCENTAGE)

        // reset debtors amount
        debtorItems.forEach { debtorItem ->
            debtorItem.amount.value = BigDecimal.ZERO
        }
        amountToBalance = amountToBalance()
        percentageToBalance = percentageToBalance()
        balancePercentagesOverNotChangedDebtorItems()
    }


    private fun balancePercentagesOverNotChangedDebtorItems() {
        val debtorItemsNotChanged = debtorItems.filter { debtorItem -> !debtorItem.changedByUser }

        if (debtorItemsNotChanged.isEmpty()) {
            return
        }

        val debtorItemsChangedWithPercent = debtorItems.filter { debtorItem ->
            debtorItem.changedByUser && debtorItem.splitMode.value == AddEditBillViewModel.SplitMode.PERCENTAGE
        }
        var remainingPercentage = BigDecimal.ZERO
        debtorItemsChangedWithPercent.forEach { debtorItem ->
            val currentAmount = debtorItem.amount.value ?: BigDecimal.ZERO
            remainingPercentage = remainingPercentage.add(currentAmount)
        }
        remainingPercentage = BigDecimal(100).subtract(remainingPercentage)

        val debtorPercentage = remainingPercentage
                .divide(BigDecimal(debtorItemsNotChanged.size), MATH_CONTEXT)
                .setScale(2, ROUNDING_MODE)

        val percentages = LinkedList<BigDecimal>()
        debtorItemsNotChanged.forEach { _ ->
            percentages.add(debtorPercentage)
        }

        val balancedPercentages = balancePercentages(percentages, remainingPercentage)

        var i = 0
        debtorItemsNotChanged.forEach { entry ->
            entry.amount.value = balancedPercentages[i]
            i++
        }
    }

    private fun balanceAmounts(amount: BigDecimal, amounts: LinkedList<BigDecimal>): List<BigDecimal> {
        if (amounts.size == 0) return emptyList()

        val step = BigDecimal("0.01")
        // TODO: Make this a injection
        val random = Random()

        balanceLoop@ while (true) {
            val index = random.nextInt(amounts.size)

            var amountSum = BigDecimal.ZERO
            amounts.forEach { a ->
                amountSum = amountSum.add(a)
            }

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

    private fun balancePercentages(percentages: LinkedList<BigDecimal>, limit: BigDecimal): List<BigDecimal> {
        if (percentages.size == 0) return emptyList()

        val step = BigDecimal("0.01")
        // TODO: Make this a injection
        val random = Random()

        balanceLoop@ while (true) {
            var percentageSum = BigDecimal.ZERO
            percentages.forEach { a ->
                percentageSum = percentageSum.add(a)
            }

            val index = random.nextInt(percentages.size)
            when (percentageSum.compareTo(limit)) {
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
        debtor.splitMode.value = AddEditBillViewModel.SplitMode.PERCENTAGE
        debtor.isAmountValid.value = isAmountValid
        debtor.currency.value = currency

        debtor.amount.observeForever {
            if (!debtor.changedByUser) return@observeForever

            amountToBalance = amountToBalance()
            percentageToBalance = percentageToBalance()

            balancePercentagesOverNotChangedDebtorItems()

            amountToBalance = amountToBalance()
            listener.onAmountToBalanceChanged(amountToBalance)
            percentageToBalance = percentageToBalance()
            listener.onPercentageToBalanceChanged(percentageToBalance)
        }

        debtor.splitMode.observeForever {
            if (!debtor.changedByUser) return@observeForever

            amountToBalance = amountToBalance()
            percentageToBalance = percentageToBalance()

            balancePercentagesOverNotChangedDebtorItems()

            amountToBalance = amountToBalance()
            listener.onAmountToBalanceChanged(amountToBalance)
            percentageToBalance = percentageToBalance()
            listener.onPercentageToBalanceChanged(percentageToBalance)
        }

        debtorItems.add(debtor)
        debtorMap[debtor.email] = debtor

        balancePercentagesOverNotChangedDebtorItems()

        amountToBalance = amountToBalance()
        percentageToBalance = percentageToBalance()

        listener.onAmountToBalanceChanged(amountToBalance)
        listener.onDebtorItemsChanged(debtorItems)
    }

    fun add(members: List<Member>) {
        members.forEach { member ->
            val debtor = DebtorItemViewModel(member.name, member.email)
            debtor.amount.value = BigDecimal.ZERO
            debtor.splitMode.value = AddEditBillViewModel.SplitMode.PERCENTAGE // splitMode
            debtor.isAmountValid.value = isAmountValid
            debtor.currency.value = currency

            debtor.amount.observeForever {
                if (!debtor.changedByUser) return@observeForever

                amountToBalance = amountToBalance()
                percentageToBalance = percentageToBalance()

                balancePercentagesOverNotChangedDebtorItems()

                amountToBalance = amountToBalance()
                listener.onAmountToBalanceChanged(amountToBalance)
                percentageToBalance = percentageToBalance()
                listener.onPercentageToBalanceChanged(percentageToBalance)
            }

            debtor.splitMode.observeForever {
                if (!debtor.changedByUser) return@observeForever

                amountToBalance = amountToBalance()
                percentageToBalance = percentageToBalance()

                balancePercentagesOverNotChangedDebtorItems()

                amountToBalance = amountToBalance()
                listener.onAmountToBalanceChanged(amountToBalance)
                percentageToBalance = percentageToBalance()
                listener.onPercentageToBalanceChanged(percentageToBalance)
            }

            debtorItems.add(debtor)
            debtorMap[debtor.email] = debtor
        }

        balancePercentagesOverNotChangedDebtorItems()

        amountToBalance = amountToBalance()
        percentageToBalance = percentageToBalance()

        listener.onAmountToBalanceChanged(amountToBalance)
        listener.onPercentageToBalanceChanged(percentageToBalance)
        listener.onDebtorItemsChanged(debtorItems)
    }

    fun remove(debtorItem: DebtorItemViewModel) {
        // TODO: Remove observer
        debtorMap.remove(debtorItem.email)
        debtorItems.remove(debtorItem)

        amountToBalance = amountToBalance()
        percentageToBalance = percentageToBalance()

        balancePercentagesOverNotChangedDebtorItems()

        amountToBalance = amountToBalance()
        percentageToBalance = percentageToBalance()

        listener.onDebtorItemsChanged(debtorItems)
        listener.onAmountToBalanceChanged(amountToBalance)
        listener.onPercentageToBalanceChanged(percentageToBalance)
    }

    fun remove(email: String) {
        // TODO: Remove observer
        val debtorItem = debtorMap.remove(email)
        debtorItems.remove(debtorItem)

        amountToBalance = amountToBalance()
        percentageToBalance = percentageToBalance()

        balancePercentagesOverNotChangedDebtorItems()

        amountToBalance = amountToBalance()
        percentageToBalance = percentageToBalance()

        listener.onDebtorItemsChanged(debtorItems)
        listener.onAmountToBalanceChanged(amountToBalance)
        listener.onPercentageToBalanceChanged(percentageToBalance)
    }

    fun clear() {
        // TODO: Remove observer
        debtorMap.clear()
        debtorItems.clear()

        amountToBalance = amountToBalance()
        percentageToBalance = percentageToBalance()

        balancePercentagesOverNotChangedDebtorItems()

        amountToBalance = amountToBalance()
        percentageToBalance = percentageToBalance()

        listener.onDebtorItemsChanged(debtorItems)
        listener.onAmountToBalanceChanged(amountToBalance)
        listener.onPercentageToBalanceChanged(percentageToBalance)
    }

    private fun amountToBalance(): BigDecimal {
        var debtorAmounts = BigDecimal.ZERO
        var debtorPercentages = BigDecimal.ZERO

        debtorItems.forEach { debtor ->
            when (debtor.splitMode.value) {
                AddEditBillViewModel.SplitMode.ABSOLUTE -> {
                    debtor.amount.value?.let {
                        debtorAmounts = debtorAmounts.add(it)
                    }
                }
                AddEditBillViewModel.SplitMode.PERCENTAGE -> {
                    debtor.amount.value?.let {
                        debtorPercentages = debtorPercentages.add(it)
                    }
                }
            }
        }
        val remainingAmount = amount.subtract(debtorAmounts)

        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return remainingAmount
        }

        return remainingAmount.subtract(
                debtorPercentages.divide(BigDecimal(100), MATH_CONTEXT).multiply(remainingAmount))
    }

    // needs to be always called after amountToBalance()
    private fun percentageToBalance(): BigDecimal {
        if (amountToBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO
        }

        var debtorAmounts = BigDecimal.ZERO
        debtorItems
                .filter { debtor -> debtor.splitMode.value == AddEditBillViewModel.SplitMode.PERCENTAGE }
                .forEach { debtor ->
                    debtor.amount.value?.let {
                        debtorAmounts = debtorAmounts.add(it)
                    }
                }
        return BigDecimal(100).subtract(debtorAmounts)
    }

    fun debtorItemsToDebtors(billId: String): List<Debtor> {
        val debtors = LinkedList<Debtor>()
        var debtorAmounts = BigDecimal.ZERO

        debtorItems
                .filter { debtorItem ->
                    debtorItem.splitMode.value == AddEditBillViewModel.SplitMode.ABSOLUTE
                }
                .forEach { debtorItem ->
                    val currentAmount = debtorItem.amount.value
                    if (!(currentAmount == null || currentAmount.compareTo(BigDecimal.ZERO) == 0)) {
                        debtors.add(Debtor(billId, debtorItem.email, currentAmount))
                        debtorAmounts = debtorAmounts.add(currentAmount)
                    }
                }

        val remainingAmount = amount.subtract(debtorAmounts)

        val amounts = LinkedList<BigDecimal>()

        val debtorItemsPercent = debtorItems.filter { debtorItem ->
            debtorItem.splitMode.value == AddEditBillViewModel.SplitMode.PERCENTAGE
        }

        debtorItemsPercent.forEach { debtorItem ->
            val currentAmount = debtorItem.amount.value ?: BigDecimal.ZERO
            amounts.add(remainingAmount.divide(BigDecimal(100), MATH_CONTEXT).multiply(currentAmount).setScale(SCALE, ROUNDING_MODE))
        }

        val balancedAmounts = balanceAmounts(remainingAmount, amounts)

        var i = 0
        debtorItemsPercent.forEach { debtorItemViewModel ->
            val amount = balancedAmounts[i]
            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                debtors.add(Debtor(billId, debtorItemViewModel.email, amount))
            }
            i++
        }

        return debtors
    }

    fun contains(email: String) = debtorMap.contains(email)
}
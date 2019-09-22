package org.weilbach.splitbills.addeditbill

import java.math.BigDecimal

interface DebtorItemContainerListener {
    fun onAmountToBalanceChanged(amountToBalance: BigDecimal)
    fun onPercentageToBalanceChanged(percentageToBalance: BigDecimal)
    fun onDebtorItemsChanged(debtorItems: List<DebtorItemViewModel>)
}
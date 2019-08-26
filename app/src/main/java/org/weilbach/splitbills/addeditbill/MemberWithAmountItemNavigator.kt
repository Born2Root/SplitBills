package org.weilbach.splitbills.addeditbill

import android.view.View
import org.weilbach.splitbills.MemberItemNavigator

interface MemberWithAmountItemNavigator: MemberItemNavigator {
    fun onAmountClicked(memberWithAmount: MemberWithAmount)
}
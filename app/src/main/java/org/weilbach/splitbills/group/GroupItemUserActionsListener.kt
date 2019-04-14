package org.weilbach.splitbills.group

import org.weilbach.splitbills.data.Group


interface GroupItemUserActionsListener {
    fun onGroupClicked(group: Group)
}
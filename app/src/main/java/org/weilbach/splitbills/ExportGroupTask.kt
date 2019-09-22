package org.weilbach.splitbills

import android.content.Context
import android.os.AsyncTask
import org.weilbach.splitbills.bills.GroupShare
import org.weilbach.splitbills.data.source.GroupMemberRepository
import org.weilbach.splitbills.data.source.GroupRepository
import org.weilbach.splitbills.util.getUser

class ExportGroupTask(
        private val groupName: String,
        private val groupRepository: GroupRepository,
        private val groupsMembersRepository: GroupMemberRepository,
        private val appContext: Context,
        private val postExecute: (GroupShare?) -> Unit
) : AsyncTask<String, Int, GroupShare?>() {

    override fun doInBackground(vararg params: String): GroupShare? {
        val user = getUser(appContext)
        val group = groupRepository.getGroupWithMembersAndBillsWithDebtorsByNameSync(groupName)
        val members = groupsMembersRepository.getGroupMembersMembersByGroupNameSync(groupName)
        val xml = writeGroupToXml(group, members)
        val subject = appContext.getString(R.string.email_subject, groupName)
        val content = appContext.getString(R.string.email_content, groupName)

        val addresses = members.filter { it.email != user.email }.map { member -> member.email }
        return GroupShare(groupName, subject, content, xml, addresses.toTypedArray())
    }

    override fun onPostExecute(result: GroupShare?) {
        postExecute(result)
    }

}
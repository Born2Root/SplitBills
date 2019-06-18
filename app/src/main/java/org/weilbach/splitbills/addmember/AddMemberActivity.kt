package org.weilbach.splitbills.addmember

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.util.obtainViewModel
import org.weilbach.splitbills.util.replaceFragmentInActivity
import org.weilbach.splitbills.util.setupActionBar

class AddMemberActivity : AppCompatActivity() {

    private lateinit var viewModel: AddMemberViewModel

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addmember)

        setupActionBar(R.id.act_add_member_toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        replaceFragmentInActivity(obtainViewFragment(), R.id.act_add_member_content_frame)

        viewModel = obtainViewModel().apply {
            saveMemberDataEvent.observe(this@AddMemberActivity, Observer<Event<Member>> { event ->
                event.getContentIfNotHandled()?.let { member ->
                    this@AddMemberActivity.saveMember(member)
                }
            })
        }
    }

    private fun saveMember(memberData: Member) {
        intent = Intent()
        intent.putExtra(RESULT_MEMBER_NAME, memberData.name)
        intent.putExtra(RESULT_MEMBER_EMAIL, memberData.email)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun obtainViewFragment() =
            supportFragmentManager.findFragmentById(R.id.act_add_member_content_frame)
                    ?: AddMemberFragment.newInstance()

    fun obtainViewModel(): AddMemberViewModel = obtainViewModel(AddMemberViewModel::class.java)

    companion object {
        const val REQUEST_CODE = 1
        const val RESULT_MEMBER_NAME = "RESULT_MEMBER_NAME"
        const val RESULT_MEMBER_EMAIL = "RESULT_MEMBER_EMAIL"
    }
}
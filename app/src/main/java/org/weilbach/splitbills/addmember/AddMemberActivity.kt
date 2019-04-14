package org.weilbach.splitbills.addmember

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.weilbach.splitbills.R
import org.weilbach.splitbills.util.obtainViewModel
import org.weilbach.splitbills.util.replaceFragmentInActivity
import org.weilbach.splitbills.util.setupActionBar

class AddMemberActivity : AppCompatActivity() {

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
    }

    private fun obtainViewFragment() =
            supportFragmentManager.findFragmentById(R.id.act_add_member_content_frame)
                    ?: AddMemberFragment.newInstance()

    fun obtainViewModel(): AddMemberViewModel = obtainViewModel(AddMemberViewModel::class.java)

    companion object {
        const val REQUEST_CODE = 1
    }
}
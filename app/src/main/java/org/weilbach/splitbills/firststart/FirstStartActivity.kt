package org.weilbach.splitbills.firststart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.weilbach.splitbills.R
import org.weilbach.splitbills.addmember.AddMemberActivity
import org.weilbach.splitbills.data2.Member
import org.weilbach.splitbills.group.GroupActivity
import org.weilbach.splitbills.util.setCurrency
import org.weilbach.splitbills.util.setFirstStart
import org.weilbach.splitbills.util.setUser
import java.util.*

class FirstStartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firststart)
    }

    fun onClick(view: View) {
        when(view.id) {
            R.id.act_firststart_button_start -> {
                val intent = Intent(this, AddMemberActivity::class.java)
                startActivityForResult(intent, AddMemberActivity.REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val name = data?.getStringExtra(AddMemberActivity.RESULT_MEMBER_NAME)
                val email = data?.getStringExtra(AddMemberActivity.RESULT_MEMBER_EMAIL)

                name?.let { name ->
                    email?.let { email ->
                        setUser(applicationContext, Member(name, email))
                        setCurrency(applicationContext, Currency.getInstance(Locale.getDefault()))
                        setFirstStart(applicationContext, false)
                        startActivity(Intent(this, GroupActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}
package org.weilbach.splitbills.addeditgroup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.weilbach.splitbills.util.Event
import org.weilbach.splitbills.R
import org.weilbach.splitbills.addmember.AddMemberActivity
import org.weilbach.splitbills.util.*

class AddEditGroupActivity : AppCompatActivity(), AddEditGroupNavigator {

    private lateinit var viewModel: AddEditGroupViewModel

    private val themeUtil = ThemeUtil()

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onGroupSaved() {
        setResult(ADD_EDIT_RESULT_OK)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeUtil.onCreate(this)
        setContentView(R.layout.activity_addeditgroup)

        setupActionBar(R.id.act_add_edit_group_toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        replaceFragmentInActivity(obtainViewFragment(), R.id.act_edit_group_content_frame)
        subscribeToNavigationChanges()

        viewModel = obtainViewModel().apply {
            addMember.observe(this@AddEditGroupActivity, Observer<Event<Unit>> { event ->
                event.getContentIfNotHandled()?.let {
                    this@AddEditGroupActivity.addMember()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        themeUtil.onResume(this)
    }

    private fun subscribeToNavigationChanges() {
        obtainViewModel().groupUpdatedEvent.observe(this, Observer {
            this@AddEditGroupActivity.onGroupSaved()
        })
    }

    private fun obtainViewFragment() =
            supportFragmentManager.findFragmentById(R.id.act_edit_group_content_frame)
                    ?: AddEditGroupFragment.newInstance().apply {
                        arguments = Bundle().apply {
                            putString(AddEditGroupFragment.ARGUMENT_EDIT_GROUP_NAME,
                                    intent.getStringExtra(AddEditGroupFragment.ARGUMENT_EDIT_GROUP_NAME))
                        }
                    }

    private fun addMember() {
        val intent = Intent(this, AddMemberActivity::class.java)
        startActivityForResult(intent, AddMemberActivity.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleActivityOnResult(requestCode, resultCode, data)
    }

    fun obtainViewModel(): AddEditGroupViewModel = obtainViewModel(AddEditGroupViewModel::class.java)

    companion object {
        const val REQUEST_CODE = 1
    }
}
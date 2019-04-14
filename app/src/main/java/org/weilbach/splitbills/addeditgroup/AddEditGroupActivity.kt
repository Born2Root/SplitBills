package org.weilbach.splitbills.addeditgroup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.R
import org.weilbach.splitbills.addmember.AddMemberActivity
import org.weilbach.splitbills.util.ADD_EDIT_RESULT_OK
import org.weilbach.splitbills.util.obtainViewModel
import org.weilbach.splitbills.util.replaceFragmentInActivity
import org.weilbach.splitbills.util.setupActionBar

class AddEditGroupActivity : AppCompatActivity(), AddEditGroupNavigator {

    private lateinit var viewModel: AddEditGroupViewModel

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
        setContentView(R.layout.activity_addeditgroup)

        setupActionBar(R.id.act_add_edit_group_toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        replaceFragmentInActivity(obtainViewFragment(), R.id.act_edit_group_content_frame)
        subscribeToNavigationChanges()

        viewModel = obtainViewModel().apply {
            addMemberEvent.observe(this@AddEditGroupActivity, Observer<Event<Unit>> { event ->
                event.getContentIfNotHandled()?.let {
                    this@AddEditGroupActivity.addMember()
                }
            })
        }
    }

    private fun subscribeToNavigationChanges() {
        // The activity observes the navigation events in the ViewModel
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

    fun addMember() {
        val intent = Intent(this, AddMemberActivity::class.java)
        startActivityForResult(intent, AddMemberActivity.REQUEST_CODE)
    }

    fun obtainViewModel(): AddEditGroupViewModel = obtainViewModel(AddEditGroupViewModel::class.java)

    companion object {
        const val REQUEST_CODE = 1
    }
}
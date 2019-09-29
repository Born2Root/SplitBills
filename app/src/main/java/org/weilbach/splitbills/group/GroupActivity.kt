package org.weilbach.splitbills.group

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.dialog_share_group.view.*
import org.weilbach.splitbills.util.Event
import org.weilbach.splitbills.R
import org.weilbach.splitbills.addeditbill.AddEditBillActivity
import org.weilbach.splitbills.addeditgroup.AddEditGroupActivity
import org.weilbach.splitbills.bills.BillsActivity
import org.weilbach.splitbills.bills.GroupShare
import org.weilbach.splitbills.firststart.FirstStartActivity
import org.weilbach.splitbills.settings.SettingsActivity
import org.weilbach.splitbills.util.*
import java.io.File
import java.io.FileWriter

class GroupActivity : AppCompatActivity(), GroupItemNavigator, GroupNavigator {

    private lateinit var viewModel: GroupViewModel

    private val themeUtil = ThemeUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeUtil.onCreate(this)
        setContentView(R.layout.activity_group)

        setupActionBar(R.id.act_group_toolbar) { }

        setupFab()
        setupViewFragment()

        viewModel = obtainViewModel().apply {
            openGroupEvent.observe(this@GroupActivity, Observer<Event<String>> { event ->
                event.getContentIfNotHandled()?.let {
                    openGroupDetails(it)

                }
            })

            newGroupEvent.observe(this@GroupActivity, Observer<Event<Unit>> { event ->
                event.getContentIfNotHandled()?.let {
                    this@GroupActivity.addNewGroup()
                }
            })

            newBillEvent.observe(this@GroupActivity, Observer<Event<Unit>> { event ->
                event.getContentIfNotHandled()?.let {
                    this@GroupActivity.addNewBill()
                }
            })

            itemsEmpty.observe(this@GroupActivity, Observer<Boolean> {
                if (it) {
                    act_group_fab_add_bill.hide()
                } else {
                    act_group_fab_add_bill.show()
                }
            })

            shareGroupEvent.observe(this@GroupActivity, Observer { event ->
                event.getContentIfNotHandled()?.let { content ->
                    startMailActivity(
                            this@GroupActivity,
                            content.groupName,
                            content.appendix,
                            content.content,
                            content.subject,
                            content.emails)
                }

            })
        }

        handleFirstStart()
    }

    override fun onResume() {
        super.onResume()
        themeUtil.onResume(this)
        obtainViewModel().handleIntent(intent)
    }

    private fun handleFirstStart() {
        if (getFirstStart(applicationContext)) {
            startActivity(Intent(this, FirstStartActivity::class.java))
            finish()
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> {
                val alert = AlertDialog.Builder(this)
                        .setMessage(R.string.really_remove_group)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            obtainViewModel().removeGroup(item)
                        }
                        .setNegativeButton(R.string.no) { dialog, _ ->
                            dialog.cancel()
                        }
                        .create()

                alert.setTitle(R.string.remove_group)
                alert.show()
                return false
            }
        }
        return obtainViewModel().onContextItemSelected(item)
    }

    private fun setupFab() {
        findViewById<FloatingActionButton>(R.id.act_group_fab_add_bill).apply {
            setOnClickListener {
                addNewBill()
            }
        }
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.act_group_content_frame)
                ?: replaceFragmentInActivity(GroupFragment.newInstance(),
                        R.id.act_group_content_frame)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.menu_frag_groups_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_frag_groups_import -> {
                    startImportGroupActivity()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AddEditBillActivity.REQUEST_CODE -> {
                if (resultCode == ADD_EDIT_RESULT_OK) {
                    data?.getStringExtra(AddEditBillActivity.EXTRA_GROUP_NAME)?.let {
                        showShareGroupDialog(it)
                    }
                }
            }
        }
    }

    private fun showShareGroupDialog(groupName: String) {
        if (!getShowShareGroupDialog(applicationContext)) {
            return
        }

        val alertLayout = layoutInflater.inflate(R.layout.dialog_share_group, null)
        val alert = AlertDialog.Builder(this)
        alert.setTitle(R.string.share_group)
        alert.setView(alertLayout)
        alert.setPositiveButton(R.string.yes) { _, _ ->
            setShowShareGroupDialog(applicationContext,
                    !alertLayout.dialog_share_group_check_box_show_again.isChecked)
            viewModel.shareGroup(groupName)
        }
        alert.setNegativeButton(R.string.no) { _, _ ->
            setShowShareGroupDialog(applicationContext,
                    !alertLayout.dialog_share_group_check_box_show_again.isChecked)
        }
        alert.create().show()
    }

    private fun startImportGroupActivity() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(Intent.createChooser(intent, ""),
                IMPORT_GROUP_REQUEST)
    }

    override fun openGroupDetails(groupId: String) {
        val intent = Intent(this, BillsActivity::class.java).apply {
            putExtra(BillsActivity.EXTRA_GROUP_NAME, groupId)
        }
        startActivity(intent)
    }

    override fun addNewGroup() {
        val intent = Intent(this, AddEditGroupActivity::class.java)
        startActivityForResult(intent, AddEditGroupActivity.REQUEST_CODE)
    }

    override fun addNewBill() {
        val intent = Intent(this, AddEditBillActivity::class.java)
        startActivityForResult(intent, AddEditBillActivity.REQUEST_CODE)
    }

    fun obtainViewModel(): GroupViewModel = obtainViewModel(GroupViewModel::class.java)

    companion object {
        const val IMPORT_GROUP_REQUEST = 97
    }
}
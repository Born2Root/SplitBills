package org.weilbach.splitbills.group

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_group.*
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.R
import org.weilbach.splitbills.addeditbill.AddEditBillActivity
import org.weilbach.splitbills.addeditgroup.AddEditGroupActivity
import org.weilbach.splitbills.bills.BillsActivity
import org.weilbach.splitbills.firststart.FirstStartActivity
import org.weilbach.splitbills.intro.IntroActivity
import org.weilbach.splitbills.settings.SettingsActivity
import org.weilbach.splitbills.util.getFirstStart
import org.weilbach.splitbills.util.obtainViewModel
import org.weilbach.splitbills.util.replaceFragmentInActivity
import org.weilbach.splitbills.util.setupActionBar

class GroupActivity : AppCompatActivity(), GroupItemNavigator, GroupNavigator {

    private lateinit var viewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        }

        /* FIXME: This should be handled in onResume but then we need to check if the intent was
           handled before */
        obtainViewModel().handleIntent(intent)

        handleFirstStart()
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
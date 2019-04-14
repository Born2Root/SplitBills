package org.weilbach.splitbills.group

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import org.weilbach.splitbills.R

import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.addeditgroup.AddEditGroupActivity
import org.weilbach.splitbills.util.obtainViewModel
import org.weilbach.splitbills.util.replaceFragmentInActivity
import org.weilbach.splitbills.util.setupActionBar

class GroupActivity : AppCompatActivity(), GroupItemNavigator, GroupNavigator {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var viewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        setupActionBar(R.id.act_group_toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        // setupNavigationDrawer()

        setupViewFragment()

        viewModel = obtainViewModel().apply {
            openGroupEvent.observe(this@GroupActivity, Observer<Event<String>> { event ->
                event.getContentIfNotHandled()?.let {
                    openGroupDetails(it)

                }
            })
            // Subscribe to "new group" event
            newGroupEvent.observe(this@GroupActivity, Observer<Event<Unit>> { event ->
                event.getContentIfNotHandled()?.let {
                    this@GroupActivity.addNewGroup()
                }
            })
        }
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.act_group_content_frame)
                ?: replaceFragmentInActivity(GroupFragment.newInstance(),
                        R.id.act_group_content_frame)
    }

    /*private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.act_group_drawer_layout))
                .apply {
                    setStatusBarBackground(R.color.colorPrimaryDark)
                }
        setupDrawerContent(findViewById(R.id.act_group_nav_view))
    }*/

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
/*                android.R.id.home -> {
                    // Open the navigation drawer when the home icon is selected from the toolbar.
                    drawerLayout.openDrawer(GravityCompat.START)
                    true
                } */
                else -> super.onOptionsItemSelected(item)
            }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                /*R.id.list_navigation_menu_item -> {
                    // Do nothing, we're already on that screen
                }*/
                /*R.id.statistics_navigation_menu_item -> {
                    val intent = Intent(this@TasksActivity, StatisticsActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    startActivity(intent)
                }*/
            }
            // Close the navigation drawer when an item is selected.
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       viewModel.handleActivityResult(requestCode, resultCode)
    }

    override fun openGroupDetails(groupId: String) {
/*        val intent = Intent(this, TaskDetailActivity::class.java).apply {
            putExtra(GroupDetailActivity.EXTRA_TASK_ID, taskId)
        }
        startActivityForResult(intent, AddEditGroupActivity.REQUEST_CODE*/

    }

    override fun addNewGroup() {
        val intent = Intent(this, AddEditGroupActivity::class.java)
        startActivityForResult(intent, AddEditGroupActivity.REQUEST_CODE)
    }

    fun obtainViewModel(): GroupViewModel = obtainViewModel(GroupViewModel::class.java)
}
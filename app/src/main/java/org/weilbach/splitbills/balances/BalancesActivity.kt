package org.weilbach.splitbills.balances

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import org.weilbach.splitbills.R
import org.weilbach.splitbills.util.obtainViewModel
import org.weilbach.splitbills.util.replaceFragmentInActivity
import org.weilbach.splitbills.util.setupActionBar

class BalancesActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var viewModel: BalancesViewModel

    private var groupName: String? = null

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balances)

        groupName = intent.getStringExtra(EXTRA_GROUP_NAME)

        setupActionBar(R.id.act_balances_toolbar) {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = groupName
        }

        setupViewFragment()
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.act_balances_content_frame)
                ?: replaceFragmentInActivity(BalancesFragment.newInstance(),
                        R.id.act_balances_content_frame)
    }

    fun obtainViewModel(): BalancesViewModel = obtainViewModel(BalancesViewModel::class.java)

    companion object {
        const val EXTRA_GROUP_NAME = "EXTRA_GROUP_NAME"
    }
}
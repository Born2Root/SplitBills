package org.weilbach.splitbills.bills

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.R
import org.weilbach.splitbills.util.obtainViewModel
import org.weilbach.splitbills.util.setupActionBar
import androidx.lifecycle.Observer
import org.weilbach.splitbills.addeditbill.AddEditBillActivity
import org.weilbach.splitbills.util.replaceFragmentInActivity

class BillsActivity : AppCompatActivity(), BillItemNavigator, BillNavigator {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var viewModel: BillsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bills)

        setupActionBar(R.id.act_bill_toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        setupViewFragment()

        viewModel = obtainViewModel().apply {
            openBillEvent.observe(this@BillsActivity, Observer<Event<String>> { event ->
                event.getContentIfNotHandled()?.let {
                    openBillDetails(it)

                }
            })
            // Subscribe to "new bill" event
            newBillEvent.observe(this@BillsActivity, Observer<Event<Unit>> { event ->
                event.getContentIfNotHandled()?.let {
                    this@BillsActivity.addNewBill()
                }
            })
        }
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.act_bill_content_frame)
                ?: replaceFragmentInActivity(BillsFragment.newInstance(),
                        R.id.act_bill_content_frame)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            super.onOptionsItemSelected(item)

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.handleActivityResult(requestCode, resultCode)
    }

    override fun openBillDetails(billId: String) {

    }

    override fun addNewBill() {
        val intent = Intent(this, AddEditBillActivity::class.java)
        startActivityForResult(intent, AddEditBillActivity.REQUEST_CODE)
    }

    fun obtainViewModel(): BillsViewModel = obtainViewModel(BillsViewModel::class.java)
}
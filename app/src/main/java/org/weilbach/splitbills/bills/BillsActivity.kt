package org.weilbach.splitbills.bills

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import org.weilbach.splitbills.R
import org.weilbach.splitbills.addeditbill.AddEditBillActivity
import org.weilbach.splitbills.addmember.AddMemberActivity
import org.weilbach.splitbills.balances.BalancesActivity
import org.weilbach.splitbills.billdetail.BillDetailActivity
import org.weilbach.splitbills.util.*
import java.io.File
import java.io.FileWriter

class BillsActivity : AppCompatActivity(), BillItemNavigator, BillNavigator {

    private lateinit var viewModel: BillsViewModel

    private var groupName: String? = null
    private val themeUtil = ThemeUtil()

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeUtil.onCreate(this)
        setContentView(R.layout.activity_bills)

        groupName = intent.getStringExtra(EXTRA_GROUP_NAME)

        setupActionBar(R.id.act_bill_toolbar) {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = groupName
        }

        setupViewFragment()

        viewModel = obtainViewModel().apply {
            openBillEvent.observe(this@BillsActivity, Observer<Event<String>> { event ->
                event.getContentIfNotHandled()?.let {
                    openBillDetails(it)

                }
            })
            newBillEvent.observe(this@BillsActivity, Observer<Event<Unit>> { event ->
                event.getContentIfNotHandled()?.let {
                    this@BillsActivity.addNewBill()
                }
            })

            openBalancesEvent.observe(this@BillsActivity, Observer { event ->
                event.getContentIfNotHandled()?.let {
                    groupName?.let {
                        openBalancesDetails(it)
                    }
                }
            })

            shareGroupEvent.observe(this@BillsActivity, Observer { event ->
                event.getContentIfNotHandled()?.let {
                    startMailActivity(
                            this@BillsActivity,
                            it.groupName,
                            it.appendix,
                            it.content,
                            it.subject,
                            it.emails)
                }
            })

            addMemberEvent.observe(this@BillsActivity, Observer { event ->
                event.getContentIfNotHandled()?.let {
                    openAddMemberActivity()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        themeUtil.onCreate(this)
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.act_bill_content_frame)
                ?: replaceFragmentInActivity(BillsFragment.newInstance(),
                        R.id.act_bill_content_frame)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> {
                val alert = AlertDialog.Builder(this)
                        .setMessage(R.string.really_remove_bill)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            obtainViewModel().removeBill(item)
                        }
                        .setNegativeButton(R.string.no) { dialog, _ ->
                            dialog.cancel()
                        }
                        .create()

                alert.setTitle(R.string.remove_bill)
                alert.show()
                return false
            }
        }
        return obtainViewModel().onContextItemSelected(item)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleActivityResult(requestCode, resultCode, data)
    }

    override fun openBillDetails(billId: String) {
        val intent = Intent(this, BillDetailActivity::class.java).apply {
            putExtra(BillDetailActivity.EXTRA_BILL_ID, billId)
        }
        startActivity(intent)
    }

    private fun openBalancesDetails(groupName: String) {
        val intent = Intent(this, BalancesActivity::class.java).apply {
            putExtra(BalancesActivity.EXTRA_GROUP_NAME, groupName)
        }
        startActivity(intent)
    }

    private fun openAddMemberActivity() {
        val intent = Intent(this, AddMemberActivity::class.java)
        startActivityForResult(intent, AddMemberActivity.REQUEST_CODE)
    }

    override fun addNewBill() {
        val intent = Intent(this, AddEditBillActivity::class.java)
        startActivityForResult(intent, AddEditBillActivity.REQUEST_CODE)
    }

    fun obtainViewModel(): BillsViewModel = obtainViewModel(BillsViewModel::class.java)

    companion object {
        const val REQUEST_CODE = 1
        const val EXTRA_GROUP_NAME = "EXTRA_GROUP_NAME"
    }
}

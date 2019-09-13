package org.weilbach.splitbills.addeditbill

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.Bill
import org.weilbach.splitbills.util.*

class AddEditBillActivity : AppCompatActivity(), AddEditBillNavigator {

    private val themeUtil = ThemeUtil()

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBillSaved(bill: Bill) {
        val intent = Intent()
        intent.putExtra(EXTRA_GROUP_NAME, bill.groupName)
        intent.putExtra(EXTRA_BILL_ID, bill.id)
        setResult(ADD_EDIT_RESULT_OK, intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeUtil.onCreate(this)
        setContentView(R.layout.activity_addeditbill)

        setupActionBar(R.id.act_add_edit_bill_toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        replaceFragmentInActivity(obtainViewFragment(), R.id.act_add_edit_bill_content_frame)
        subscribeToNavigationChanges()
        subscribeToCreditorChanges()
        subscribeToAddDebtorChanges()
        subscribeToSplitModeChanges()

        // Needed since LiveData will otherwise not return value
        obtainViewModel().availableDebtors.observe(this, Observer{ })
    }

    override fun onResume() {
        super.onResume()
        themeUtil.onResume(this)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return obtainViewModel().onContextItemSelected(item)
    }

    private fun subscribeToCreditorChanges() {
        obtainViewModel().changeCreditor.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                showChooseCreditorDialog()
            }
        })
    }

    private fun subscribeToAddDebtorChanges() {
        obtainViewModel().addDebtor.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                showAddDebtorDialog()
            }
        })
    }

    private fun subscribeToSplitModeChanges() {
        obtainViewModel().changeSplitModeEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { splitMode ->
                changeSplitMode(splitMode)
            }
        })
    }

    private fun changeSplitMode(currentSplitMode: AddEditBillViewModel.SplitMode) {
        when (currentSplitMode) {
            AddEditBillViewModel.SplitMode.ABSOLUTE -> obtainViewModel().splitMode.value = AddEditBillViewModel.SplitMode.PERCENTAGE
            AddEditBillViewModel.SplitMode.PERCENTAGE -> obtainViewModel().splitMode.value = AddEditBillViewModel.SplitMode.ABSOLUTE
        }
    }

    private fun showChooseCreditorDialog() {
        val memberItems = ArrayList<String>()
        obtainViewModel().availableMembers.value?.let { members ->

            members.forEach {
                memberItems.add(it.name)
            }

            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.alert_dialog_creditor_title))
                    .setItems(memberItems.toTypedArray()) { _, which ->
                        obtainViewModel().creditor.value = members[which]
                    }
                    .create()
                    .show()
        }
    }

    private fun showAddDebtorDialog() {
        val memberItems = ArrayList<String>()
        obtainViewModel().availableDebtors.value?.let { debtors ->
            debtors.forEach { debtor ->
                memberItems.add(debtor.name)
            }

            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.alert_dialog_debtor_title))
                    .setItems(memberItems.toTypedArray()) { _, which ->
                        obtainViewModel().debtorAdded(debtors[which])
                    }
                    .create()
                    .show()
        }
    }

    private fun subscribeToNavigationChanges() {
        obtainViewModel().billUpdatedEvent.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                this@AddEditBillActivity.onBillSaved(it)
            }
        })
    }

    private fun obtainViewFragment() =
            supportFragmentManager.findFragmentById(R.id.act_add_edit_bill_content_frame)
                    ?: AddEditBillFragment.newInstance().apply {
                        arguments = Bundle().apply {
                            putString(AddEditBillFragment.ARGUMENT_EDIT_BILL_ID,
                                    intent.getStringExtra(AddEditBillFragment.ARGUMENT_EDIT_BILL_ID))
                        }
                    }

    fun obtainViewModel(): AddEditBillViewModel = obtainViewModel(AddEditBillViewModel::class.java)

    companion object {
        private const val TAG = "AddEditBillActivity"
        const val REQUEST_CODE = 2
        const val EXTRA_GROUP_NAME = "EXTRA_GROUP_NAME"
        const val EXTRA_BILL_ID = "EXTRA_BILL_ID"
    }
}
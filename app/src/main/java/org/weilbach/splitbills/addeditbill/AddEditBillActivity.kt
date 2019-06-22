package org.weilbach.splitbills.addeditbill

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.weilbach.splitbills.R
import org.weilbach.splitbills.util.*

class AddEditBillActivity : AppCompatActivity(), AddEditBillNavigator {

    private val themeUtil = ThemeUtil()

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBillSaved() {
        setResult(ADD_EDIT_RESULT_OK)
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
            showChooseCreditorDialog()
        })
    }

    private fun subscribeToAddDebtorChanges() {
        obtainViewModel().addDebtor.observe(this, Observer {
            showAddDebtorDialog()
        })
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
        obtainViewModel().billUpdatedEvent.observe(this, Observer {
            this@AddEditBillActivity.onBillSaved()
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
    }
}
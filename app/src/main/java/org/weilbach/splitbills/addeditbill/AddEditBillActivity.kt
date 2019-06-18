package org.weilbach.splitbills.addeditbill

import android.database.DataSetObserver
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ListAdapter
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.weilbach.splitbills.R
import org.weilbach.splitbills.util.ADD_EDIT_RESULT_OK
import org.weilbach.splitbills.util.obtainViewModel
import org.weilbach.splitbills.util.replaceFragmentInActivity
import org.weilbach.splitbills.util.setupActionBar

class AddEditBillActivity : AppCompatActivity(), AddEditBillNavigator {

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
        setContentView(R.layout.activity_addeditbill)

        setupActionBar(R.id.act_add_edit_bill_toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        replaceFragmentInActivity(obtainViewFragment(), R.id.act_add_edit_bill_content_frame)
        subscribeToNavigationChanges()
        subscribeToCreditorChanges()
        subscribeToAddDebtorChanges()

        /*viewModel = obtainViewModel().apply {
            addMember.observe(this@AddEditGroupActivity, Observer<Event<Unit>> { event ->
                event.getContentIfNotHandled()?.let {
                    this@AddEditGroupActivity.addMember()
                }
            })
        }*/
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
        obtainViewModel().availableMembers.value!!.forEach {
            memberItems.add(it.name)
        }

        AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_dialog_creditor_title))
                .setItems(memberItems.toTypedArray()) { dialog, which ->
                    obtainViewModel().creditor.value = obtainViewModel().availableMembers.value!![which]
                }
                .create()
                .show()
    }

    private fun showAddDebtorDialog() {
        val memberItems = ArrayList<String>()
        obtainViewModel().availableMembers.value!!.forEach {
            memberItems.add(it.name)
        }
        Log.d(TAG, "memberDataItems: $memberItems")

        AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_dialog_debtor_title))
                /*.setAdapter(object : BaseAdapter() {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun getItem(position: Int): Any {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun getItemId(position: Int): Long {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun getCount(): Int {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                }) { dialog, which ->  }*/
                .setItems(memberItems.toTypedArray()) { dialog, which ->
                    obtainViewModel().debtorAdded(obtainViewModel().availableMembers.value!![which])
                }
                .create()
                .show()
    }

    private fun subscribeToNavigationChanges() {
        // The activity observes the navigation events in the ViewModel
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

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.handleActivityOnResult(requestCode, resultCode, data)
    }*/

    fun obtainViewModel(): AddEditBillViewModel = obtainViewModel(AddEditBillViewModel::class.java)

    companion object {
        private const val TAG = "AddEditBillActivity"
        const val REQUEST_CODE = 2
    }
}
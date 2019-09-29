package org.weilbach.splitbills.billdetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_billdetail.*
import org.weilbach.splitbills.R
import org.weilbach.splitbills.util.ThemeUtil
import org.weilbach.splitbills.util.obtainViewModel
import org.weilbach.splitbills.util.replaceFragmentInActivity
import org.weilbach.splitbills.util.setupActionBar

class BillDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: BillDetailViewModel

    private var billId: String? = null

    private val themeUtil = ThemeUtil()

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeUtil.onCreate(this)
        setContentView(R.layout.activity_billdetail)

        billId = intent.getStringExtra(EXTRA_BILL_ID)

        viewModel = obtainViewModel()

        setupActionBar(R.id.activity_bill_detail_toolbar) {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)

            // Maybe not necessary
            title = viewModel.description.value
            activity_bill_detail_text_view_amount.text = viewModel.amount.value
            activity_bill_detail_text_view_paid_by.text = viewModel.paidBy.value
        }

        setupViewFragment()

        with(viewModel) {
            description.observe(this@BillDetailActivity, Observer { description ->
                activity_bill_detail_toolbar.title = description
            })

            amount.observe(this@BillDetailActivity, Observer { amount ->
                activity_bill_detail_text_view_amount.text = amount
            })

            paidBy.observe(this@BillDetailActivity, Observer { paidBy ->
                activity_bill_detail_text_view_paid_by.text = paidBy
            })
        }
    }

    override fun onResume() {
        super.onResume()
        themeUtil.onResume(this)
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.act_bill_detail_content_frame)
                ?: replaceFragmentInActivity(BillDetailFragment.newInstance(),
                        R.id.act_bill_detail_content_frame)
    }

    fun obtainViewModel(): BillDetailViewModel = obtainViewModel(BillDetailViewModel::class.java)

    companion object {
        const val EXTRA_BILL_ID = "EXTRA_BILL_ID"
    }
}
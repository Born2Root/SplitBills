package org.weilbach.splitbills.billdetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.weilbach.splitbills.R
import org.weilbach.splitbills.util.obtainViewModel
import org.weilbach.splitbills.util.replaceFragmentInActivity
import org.weilbach.splitbills.util.setupActionBar

class BillDetailActivity : AppCompatActivity() {

    private var billId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billdetail)

        billId = intent.getStringExtra(EXTRA_BILL_ID)

        setupViewFragment()
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
package org.weilbach.splitbills.addeditbill

import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.dialog_debtor_change_amount.view.*
import org.weilbach.splitbills.R
import org.weilbach.splitbills.ROUNDING_MODE
import org.weilbach.splitbills.SCALE
import org.weilbach.splitbills.databinding.DebtorItemBinding
import org.weilbach.splitbills.prettyPrintNum
import java.math.BigDecimal

class DebtorItemViewModelAdapter(
        private var debtorItems: List<DebtorItemViewModel>,
        private val parent: AddEditBillFragment
) : BaseAdapter() {

    private val viewmodel = (parent.activity as AddEditBillActivity).obtainViewModel()

    fun replaceData(debtorItems: List<DebtorItemViewModel>) {
        setList(debtorItems)
    }

    override fun getItem(position: Int): Any {
        return debtorItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return debtorItems.size
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: DebtorItemBinding
        binding = if (view == null) {
            val inflater = LayoutInflater.from(viewGroup.context)
            DebtorItemBinding.inflate(inflater, viewGroup, false)
        } else {
            DataBindingUtil.getBinding(view) ?: throw IllegalStateException()
        }

        val debtorItem = debtorItems[position]

        debtorItem.changeAmountEvent.observe(parent, Observer { event ->
            event.getContentIfNotHandled()?.let { content ->
                showChangeAmountDialog(content)
            }
        })

        debtorItem.removeEvent.observe(parent, Observer { event ->
            event.getContentIfNotHandled()?.let { content ->
                viewmodel.removeDebtor(content.email)
            }
        })

        with(binding) {
            lifecycleOwner = parent.viewLifecycleOwner
            viewmodel = debtorItem
            executePendingBindings()
        }

        return binding.root
    }

    private fun showChangeAmountDialog(debtorItem: DebtorItemViewModel) {
        val amount = debtorItem.amount.value ?: return

        val alertLayout = parent.layoutInflater.inflate(R.layout.dialog_debtor_change_amount, null)
        val editTextAmount = alertLayout.dialog_change_amount_absolute_edit_text_amount
        val textViewBalance = alertLayout.dialog_change_amount_absolute_text_view_balance
        val buttonFillUp = alertLayout.dialog_change_amount_absolute_button_fill_up
        val textViewCurrency = alertLayout.dialog_change_amount_absolute_text_view_currency
        val radioGroupSplitModes = alertLayout.dialog_change_amount_radio_group_split_modes

        debtorItem.changedByUser = true

        editTextAmount.setText(prettyPrintNum(amount))

        viewmodel.amountToBalancePretty.observe(parent, Observer {
            textViewBalance.text = it
        })

        viewmodel.isAmountToBalance.observe(parent, Observer {
            val show = it || viewmodel.isPercentageToBalance.value ?: false
            if (show) {
                buttonFillUp.visibility = View.VISIBLE
                textViewBalance.visibility = View.VISIBLE
            } else {
                buttonFillUp.visibility = View.GONE
                textViewBalance.visibility = View.GONE
            }
        })

        viewmodel.isPercentageToBalance.observe(parent, Observer {
            val show = it || viewmodel.isAmountToBalance.value ?: false
            if (show) {
                buttonFillUp.visibility = View.VISIBLE
                textViewBalance.visibility = View.VISIBLE
            } else {
                buttonFillUp.visibility = View.GONE
                textViewBalance.visibility = View.GONE
            }

            when (debtorItem.splitMode.value) {
                AddEditBillViewModel.SplitMode.ABSOLUTE -> {

                }
                AddEditBillViewModel.SplitMode.PERCENTAGE -> {

                }
            }
        })

        viewmodel.amountToBalance.observe(parent, Observer { balance ->
            if (debtorItem.splitMode.value != AddEditBillViewModel.SplitMode.ABSOLUTE) {
                return@Observer
            }
            buttonFillUp.setOnClickListener {
                val debtorItemAmount = debtorItem.amount.value ?: BigDecimal.ZERO
                val fill = balance.add(debtorItemAmount).setScale(SCALE, ROUNDING_MODE)
                if (fill.compareTo(BigDecimal.ZERO) == -1) {
                    editTextAmount.setText("0")
                } else {
                    val fillString = fill.toString()
                    editTextAmount.setText(fillString)
                }
            }
        })

        viewmodel.percentageToBalance.observe(parent, Observer { balance ->
            if (debtorItem.splitMode.value != AddEditBillViewModel.SplitMode.PERCENTAGE) {
                return@Observer
            }
            buttonFillUp.setOnClickListener {
                val debtorItemAmount = debtorItem.amount.value ?: BigDecimal.ZERO
                val fill = balance.add(debtorItemAmount).setScale(SCALE, ROUNDING_MODE)
                if (fill.compareTo(BigDecimal.ZERO) == -1) {
                    editTextAmount.setText("0")
                } else {
                    val fillString = fill.toString()
                    editTextAmount.setText(fillString)
                }
            }
        })

        debtorItem.splitMode.observe(parent, Observer {
            val unitSymbol = when (debtorItem.splitMode.value) {
                AddEditBillViewModel.SplitMode.ABSOLUTE -> {
                    debtorItem.currency.value?.symbol
                }
                AddEditBillViewModel.SplitMode.PERCENTAGE -> {
                    "%"
                }
                else -> null
            }
            textViewCurrency.text = unitSymbol
        })

        when (debtorItem.splitMode.value) {
            AddEditBillViewModel.SplitMode.ABSOLUTE -> {
                radioGroupSplitModes.check(R.id.dialog_change_amount_radio_button_absolute)
            }

            AddEditBillViewModel.SplitMode.PERCENTAGE -> {
                radioGroupSplitModes.check(R.id.dialog_change_amount_radio_button_percentual)
            }
        }

        // Trigger the events above to set up everything correctly
        debtorItem.amount.value = debtorItem.amount.value

        radioGroupSplitModes.setOnCheckedChangeListener { _, checkedId ->
            val editTextAmountText = editTextAmount.text.toString()
            val newAmount = if (editTextAmountText.isBlank()) {
                BigDecimal.ZERO
            }
            else {
                BigDecimal(editTextAmountText)
            }

            when (checkedId) {
                R.id.dialog_change_amount_radio_button_absolute -> {
                    debtorItem.splitMode.value = AddEditBillViewModel.SplitMode.ABSOLUTE
                    debtorItem.amount.value = newAmount
                }
                R.id.dialog_change_amount_radio_button_percentual -> {
                    debtorItem.splitMode.value = AddEditBillViewModel.SplitMode.PERCENTAGE
                    debtorItem.amount.value = newAmount
                }
            }
        }

        editTextAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let { editable ->
                    var newAmount = editable.toString()
                    if (newAmount.isBlank()) {
                        newAmount = "0"
                    }
                    debtorItem.amount.value = BigDecimal(newAmount)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        val alert = AlertDialog.Builder(parent.context)
        alert.setTitle(viewmodel.appContext.getString(R.string.change_amount))
        alert.setView(alertLayout)
        alert.setPositiveButton(R.string.done) { _, _ -> }
        val dialog = alert.create()
        dialog.show()
    }

    private fun setList(debtorItems: List<DebtorItemViewModel>) {
        this.debtorItems = debtorItems
        notifyDataSetChanged()
    }
}
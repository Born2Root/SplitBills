package org.weilbach.splitbills.addeditbill

import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.dialog_change_amount_absolute.view.*
import org.weilbach.splitbills.MemberItemNavigator
import org.weilbach.splitbills.R
import org.weilbach.splitbills.ROUNDING_MODE
import org.weilbach.splitbills.SCALE
import org.weilbach.splitbills.data.local.Converter
import org.weilbach.splitbills.databinding.MemberWithAmountItemBinding
import org.weilbach.splitbills.util.fromHtml
import java.math.BigDecimal

class MemberWithAmountAdapter(
        private val memberWithAmountItemNavigator: MemberItemNavigator,
        private var members: LinkedHashMap<String, MemberWithAmount>,
        private val parent: AddEditBillFragment
) : BaseAdapter() {

    private val viewmodel = (parent.activity as AddEditBillActivity).obtainViewModel()

    fun replaceData(members: LinkedHashMap<String, MemberWithAmount>) {
        setList(members)
    }

    override fun getItem(position: Int): Any {
        return members.values.toList()[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return members.size
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: MemberWithAmountItemBinding
        binding = if (view == null) {
            val inflater = LayoutInflater.from(viewGroup.context)
            MemberWithAmountItemBinding.inflate(inflater, viewGroup, false)
        } else {
            DataBindingUtil.getBinding(view) ?: throw IllegalStateException()
        }

        val member = members.values.toList()[position]

        member.changeAmountEvent.observe(parent, Observer { event ->
            // TODO: add other split cases
            event.getContentIfNotHandled()?.let { content ->
                when (viewmodel.splitMode.value) {
                    AddEditBillViewModel.SplitMode.ABSOLUTE -> {
                        showChangeAmountAbsoluteDialog(content)
                    }
                    else -> {
                        Log.w("MemberWithAmountAdapter", "Unhandled split mode")
                    }
                }
            }
        })

        member.removeMemberEvent.observe(parent, Observer { event ->
            event.getContentIfNotHandled()?.let { content ->
                viewmodel.removeDebtor(content)
            }
        })

        with(binding) {
            viewmodel = member
            executePendingBindings()
        }

        return binding.root
    }

    private fun showChangeAmountAbsoluteDialog(content: MemberWithAmount) {
        // FIXME: cleanup
        val amount = viewmodel.amount.value!!
        val currencySymbol = viewmodel.currency.value!!.symbol
        val alertLayout = parent.layoutInflater.inflate(R.layout.dialog_change_amount_absolute, null)
        val editTextAmount = alertLayout.dialog_change_amount_absolute_edit_text_amount
        val textViewBalance = alertLayout.dialog_change_amount_absolute_text_view_balance
        val buttonFillUp = alertLayout.dialog_change_amount_absolute_button_fill_up
        val textViewCurrency = alertLayout.dialog_change_amount_absolute_text_view_currency
        editTextAmount.setText(Converter().bigDecimalToString(content.amount))
        textViewCurrency.text = viewmodel.currencySymbol.value

        viewmodel.setBalanceAmountDebtors { balance, currencySymbol ->
            textViewBalance.text =
                    fromHtml(viewmodel.appContext.getString(R.string.balance_debtors, balance, currencySymbol))

            if (balance.compareTo(BigDecimal.ZERO) != 0) {
                buttonFillUp.setOnClickListener {
                    val fill = balance.add(content.amount).setScale(SCALE, ROUNDING_MODE)
                    if (fill.compareTo(BigDecimal.ZERO) == -1) {
                        editTextAmount.setText("0")
                    } else {
                        val fillString = fill.toString()
                        editTextAmount.setText(fillString)
                    }
                }
                buttonFillUp.visibility = View.VISIBLE
                textViewBalance.visibility = View.VISIBLE
            } else {
                buttonFillUp.visibility = View.GONE
                textViewBalance.visibility = View.GONE
            }
        }

        editTextAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let { editable ->
                    var newAmount = editable.toString()
                    if (newAmount.isBlank()) {
                        newAmount = "0"
                    }
                    var amountSum = BigDecimal.ZERO
                    viewmodel.debtorItems.value?.forEach { entry ->
                        amountSum = if (entry.value.email != content.email) {
                            amountSum.add(entry.value.amount)
                        } else {
                            amountSum.add(BigDecimal(newAmount))
                        }
                    }
                    val balance = BigDecimal(amount)
                            .subtract(amountSum)
                            .setScale(SCALE, ROUNDING_MODE)
                    viewmodel.isAmountToBalance.value = balance.compareTo(BigDecimal.ZERO) != 0
                    val balanceString = balance.toString()
                    textViewBalance.text =
                            fromHtml(viewmodel.appContext.getString(R.string.balance_debtors, balanceString, currencySymbol))
                    viewmodel.amountToBalance.value =
                            fromHtml(viewmodel.appContext.getString(R.string.balance_debtors, balanceString, currencySymbol))

                    if (balance.compareTo(BigDecimal.ZERO) != 0) {
                        buttonFillUp.setOnClickListener {
                            val fill = balance.add(BigDecimal(newAmount)).setScale(SCALE, ROUNDING_MODE)
                            if (fill.compareTo(BigDecimal.ZERO) == -1) {
                                editTextAmount.setText("0")
                            } else {
                                val fillString = fill.toString()
                                editTextAmount.setText(fillString)
                            }
                        }
                        buttonFillUp.visibility = View.VISIBLE
                        textViewBalance.visibility = View.VISIBLE
                    } else {
                        buttonFillUp.visibility = View.GONE
                        textViewBalance.visibility = View.GONE
                    }
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
        alert.setPositiveButton(viewmodel.appContext.getString(R.string.done)) { _, _ ->
            // Do nothing here, since overwrite
        }
        val dialog = alert.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val newAmount = editTextAmount.text.toString()
            if (newAmount.isBlank()) {
                editTextAmount.error = viewmodel.appContext.getString(R.string.amount_can_not_empty)
                return@setOnClickListener
            }
            content.amount = BigDecimal(newAmount).setScale(SCALE, ROUNDING_MODE)
            // FIXME: Maybe there is a better solution
            replaceData(members)
            dialog.dismiss()
        }
    }

    private fun setList(members: LinkedHashMap<String, MemberWithAmount>) {
        this.members = members
        notifyDataSetChanged()
    }
}
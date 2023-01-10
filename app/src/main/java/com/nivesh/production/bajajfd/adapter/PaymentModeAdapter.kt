package com.nivesh.production.bajajfd.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nivesh.production.bajajfd.R
import com.nivesh.production.bajajfd.model.GetCodes

class PaymentModeAdapter(
    private val bankList: List<GetCodes>?,
    private val selectedAccount: String? = null
) : RecyclerView.Adapter<PaymentModeAdapter.BankListViewHolder>() {
    inner class BankListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bankSelector: ImageView = itemView.findViewById(R.id.bankSelector)
        val tvBankName: TextView = itemView.findViewById(R.id.tvBankName)
        val tvUpiMsg: TextView = itemView.findViewById(R.id.tvUpiMsg)
    }

    private var checkedPosition: Int = -2


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BankListViewHolder {
        return BankListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_payment_list_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BankListViewHolder, position: Int) {
        val bankList = bankList?.get(position)
        if (bankList != null) {
            holder.itemView.apply {
                holder.tvBankName.text = bankList.Value
                if (bankList.Value.equals("UPI")){
                    holder.tvUpiMsg.text = context.getString(R.string.upto1LakhOnly)
                }else{
                    holder.tvUpiMsg.text = ""
                }

                if (selectedAccount == bankList.Value && checkedPosition == -2
                ) {
                    holder.bankSelector.setBackgroundResource(R.drawable.ic_select_green)
                    checkedPosition = holder.adapterPosition
                } else if (checkedPosition == -1) {
                    holder.bankSelector.setBackgroundResource(R.drawable.ic_select_outline)
                } else if (checkedPosition == holder.adapterPosition) {
                    holder.bankSelector.setBackgroundResource(R.drawable.ic_select_green)
                } else {
                    holder.bankSelector.setBackgroundResource(R.drawable.ic_select_outline)
                }

                setOnClickListener {
                    onItemClickListener?.let {
                        it(bankList)

                        holder.bankSelector.setBackgroundResource(R.drawable.ic_select_green)
                        if (checkedPosition != holder.adapterPosition) {
                            notifyItemChanged(checkedPosition)
                            checkedPosition = holder.adapterPosition

                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return bankList?.size!!
    }

    private var onItemClickListener: ((GetCodes) -> Unit)? = null

    fun setOnItemClickListener(listener: (GetCodes) -> Unit) {
        onItemClickListener = listener
    }


    fun getSelected(): GetCodes? {
        return if (checkedPosition != -1) {
            bankList?.get(checkedPosition)
        } else null
    }

}
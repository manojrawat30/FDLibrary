package com.nivesh.production.bajajfd.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nivesh.production.bajajfd.R
import com.nivesh.production.bajajfd.model.ClientBanklist

class BankListAdapter(
    private val bankList: List<ClientBanklist>?,
    private val selectedAccount: String? = null,
    private val width: Double?
) : RecyclerView.Adapter<BankListAdapter.BankListViewHolder>() {
    inner class BankListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bankSelector: ImageView = itemView.findViewById(R.id.bankSelector)
        val tvBankName: TextView = itemView.findViewById(R.id.tvBankName)
        val tvBankAccountNumber: TextView = itemView.findViewById(R.id.tvBankAccountNumber)
        val tvBankIFSC: TextView = itemView.findViewById(R.id.tvBankIFSC)
    }

    private var checkedPosition: Int = -2

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BankListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bank_list_preview, parent, false);
        view.layoutParams = width?.div(1.35)
            ?.let { ViewGroup.LayoutParams(it.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT) }
        return BankListViewHolder(view)
    }

    override fun onBindViewHolder(holder: BankListViewHolder, position: Int) {
        val bankList = bankList?.get(position)
        if (bankList != null) {
            holder.itemView.apply {
                holder.tvBankName.text = bankList.BankName
                holder.tvBankIFSC.text = bankList.IFSCCode
                holder.tvBankAccountNumber.text = bankList.AccountNumber

                if (selectedAccount == bankList.AccountNumber && (checkedPosition == -2)) {
                    holder.bankSelector.setBackgroundResource(R.drawable.ic_select_green)
                    checkedPosition = holder.adapterPosition
                } else if (checkedPosition == -1) {
                    holder.bankSelector.setBackgroundResource(R.drawable.ic_select_outline)
                } else if (checkedPosition == holder.adapterPosition) {
                    holder.bankSelector.setBackgroundResource(R.drawable.ic_select_green)
                } else {
                    holder.bankSelector.setBackgroundResource(R.drawable.ic_select_outline)
                }

                holder.itemView.setOnClickListener {
                    holder.bankSelector.setBackgroundResource(R.drawable.ic_select_green)
                    if (checkedPosition != holder.adapterPosition) {
                        notifyItemChanged(checkedPosition)
                        checkedPosition = holder.adapterPosition
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return bankList?.size!!
    }

    private var onItemClickListener: ((ClientBanklist) -> Unit)? = null

    fun setOnItemClickListener(listener: (ClientBanklist) -> Unit) {
        onItemClickListener = listener
    }

    fun getSelected(): ClientBanklist? {
        return if (checkedPosition != -1) {
            bankList?.get(checkedPosition)
        } else null
    }

}
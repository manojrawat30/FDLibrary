package com.nivesh.production.bajajfd.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.nivesh.production.bajajfd.R
import com.nivesh.production.bajajfd.model.GetCodes

class CustomerListAdapter(
    private val customerList: MutableList<GetCodes>?
) : RecyclerView.Adapter<CustomerListAdapter.BankListViewHolder>() {
    inner class BankListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCustomerName: SwitchMaterial = itemView.findViewById(R.id.tvCustomerName)
        val txtLabel: TextView = itemView.findViewById(R.id.txtLabel)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BankListViewHolder {
        return BankListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_customer_list_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BankListViewHolder, position: Int) {
        val cList = customerList?.get(position)
        if (cList != null) {
            holder.txtLabel.text = cList.Label
            holder.tvCustomerName.setOnCheckedChangeListener { _, isChecked ->
                cList.isSelected = isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return customerList?.size!!
    }

}
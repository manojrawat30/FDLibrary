package com.nivesh.production.bajajfd.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nivesh.production.bajajfd.R
import com.nivesh.production.bajajfd.model.Bank

class RecommendedBankListAdapter(
    private val bankList: List<Bank>
) : RecyclerView.Adapter<RecommendedBankListAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtYear: TextView

        init {
            txtYear = view.findViewById(R.id.txtYear)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_bank_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bank: Bank = bankList[position]
        holder.txtYear.text = bank.BankName

    }

    override fun getItemCount(): Int {
        return bankList.size
    }

}
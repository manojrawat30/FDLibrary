package com.nivesh.production.bajajfd.adapter

import ROIDataList
import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.nivesh.production.bajajfd.R
import com.nivesh.production.bajajfd.adapter.HorizontalRecyclerViewAdapter.HistoryAdapterViewHolder2
import com.nivesh.production.bajajfd.interfaces.OnClickListener


class HorizontalRecyclerViewAdapter(
    private val activity: Activity,
    dropdownList: MutableList<ROIDataList>,
    onClickListener: OnClickListener
) : RecyclerView.Adapter<HistoryAdapterViewHolder2>() {
    private var dropdownList: MutableList<ROIDataList>
    private var rowIndex = -1
    private var onClickListener: OnClickListener

    inner class HistoryAdapterViewHolder2(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        var txtYear: TextView = itemView.findViewById(R.id.txtYear)
        var txtInterestRate: TextView = itemView.findViewById(R.id.txtInterestRate)
        var rlParent: LinearLayout = itemView.findViewById(R.id.rlParent)

    }

    init {
        this.dropdownList = dropdownList
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapterViewHolder2 {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_dropdown, parent, false)
        return HistoryAdapterViewHolder2(itemView)
    }

    override fun onBindViewHolder(
        holder: HistoryAdapterViewHolder2,
        @SuppressLint("RecyclerView") position: Int
    ) {
        if (dropdownList.isNotEmpty()) {
            val roiDataList: ROIDataList = dropdownList[position]
            getYear(holder.txtYear, roiDataList, holder)
            holder.txtInterestRate.text = roiDataList.ROI.toString().plus(" % ")
            holder.rlParent.setOnClickListener {
                rowIndex = position
                notifyDataSetChanged()
            }
            if (rowIndex == position) {
                onClickListener.onclickCategory(position)
                holder.txtYear.background =
                    ResourcesCompat.getDrawable(
                        activity.resources,
                        R.drawable.rounded_corner_green_fill,
                        null
                    )
                holder.txtYear.setTextColor(ContextCompat.getColor(activity, R.color.white))
            } else {
                holder.txtYear.background =
                    ResourcesCompat.getDrawable(
                        activity.resources,
                        R.drawable.rounded_corner_with_line,
                        null
                    )
                holder.txtYear.setTextColor(
                    ContextCompat.getColor(activity, R.color.black)
                )
            }
        }
    }

    private fun getYear(txtYear: TextView, option: ROIDataList, holder: HistoryAdapterViewHolder2) {
        when (option.Tenure) {
            "12" -> {
                holder.rlParent.visibility = View.VISIBLE
                txtYear.text = activity.getString(R.string.OneYear)
            }
            "24" -> {
                holder.rlParent.visibility = View.VISIBLE
                txtYear.text = activity.getString(R.string.TwoYears)
            }
            "36" -> {
                holder.rlParent.visibility = View.VISIBLE
                txtYear.text = activity.getString(R.string.ThreeYears)
            }
            "48" -> {
                holder.rlParent.visibility = View.VISIBLE
                txtYear.text = activity.getString(R.string.FourYears)
            }
            "60" -> {
                holder.rlParent.visibility = View.VISIBLE
                txtYear.text = activity.getString(R.string.FiveYears)
            }
            else -> {
                holder.rlParent.visibility = View.GONE
            }

        }
    }

    override fun getItemCount(): Int {
        return dropdownList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun refresh() {
        rowIndex = -1
        notifyDataSetChanged()
    }
}
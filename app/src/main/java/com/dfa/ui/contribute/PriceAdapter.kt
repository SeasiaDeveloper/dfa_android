package com.dfa.ui.contribute

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dfa.R

class PriceAdapter(
    var context: ContributeActivity,
    var priceList: ArrayList<String>
) :
    RecyclerView.Adapter<PriceAdapter.ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_price_layout, viewGroup, false)
        return ViewHolder(v)
    }

    fun setData(list: ArrayList<String>) {
        priceList = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PriceAdapter.ViewHolder, position: Int) {
        var number=position+1
        holder.price!!.setText(""+number+" Prize: "+priceList.get(position))
    }

    override fun getItemCount(): Int {
        return priceList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var price: TextView? = null

        init {
            price = itemView.findViewById(R.id.tvPrice)
        }
    }
}

package com.dfa.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dfa.R
import com.dfa.ui.home.fragments.marketplace.MarketPlaceFragment

class PhoneAdapter(
    var context: MarketPlaceFragment,
    var phoneList: ArrayList<String>
) :
    RecyclerView.Adapter<PhoneAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_phone_number, viewGroup, false)
        return ViewHolder(v)
    }

    fun setData(list: ArrayList<String>) {
        phoneList = list
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
 holder.phoneNumber!!.setText(phoneList.get(position))

        holder.parentLayout!!.setOnClickListener {
            context.call(phoneList.get(position))
        }
}


    override fun getItemCount(): Int {
        return phoneList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var phoneNumber: TextView? = null
        var parentLayout: RelativeLayout? = null

        init {

            phoneNumber = itemView.findViewById(R.id.tvPhoneNumber)
            parentLayout = itemView.findViewById(R.id.parentLayout)

        }
    }
}

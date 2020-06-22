package com.dfa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dfa.R
import com.dfa.databinding.ItemStatusBinding
import com.dfa.listeners.OnCaseItemClickListener
import com.dfa.pojo.response.GetStatusDataBean
import kotlinx.android.synthetic.main.item_status.view.*

class StatusAdapter(
    var context: Context,
    var mList: MutableList<GetStatusDataBean>,
    private var listener: OnCaseItemClickListener
) :
    RecyclerView.Adapter<StatusAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_status,
            parent,
            false
        ) as ItemStatusBinding

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this, context, mList.get(position), position, listener)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var index: Int? = null
        var radioBtn: RadioButton? = null
        fun bind(
            adapter: StatusAdapter,
            context: Context,
            item: GetStatusDataBean,
            index: Int,
            listener: OnCaseItemClickListener
        ) {
            radioBtn = itemView.radioBtn
            this.index = index

            itemView.radioBtn.text = item.name
            itemView.radioBtn.setOnClickListener {

                listener.onStatusClick(item.id)

                for (element in adapter.mList) {
                    if (element.id == item.id) {
                        element.isChecked = true
                    } else {
                        element.isChecked = false
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            if (item.isChecked) {
                itemView.radioBtn.isChecked = true
            } else {
                itemView.radioBtn.isChecked = false
            }
        }
    }
}
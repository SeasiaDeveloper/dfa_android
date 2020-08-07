package com.dfa.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dfa.R
import com.dfa.databinding.RowPoliceOfficerBinding
import com.dfa.pojo.response.PoliceOfficerResponse
import com.dfa.ui.generalpublic.PoliceOfficerActivity
import kotlinx.android.synthetic.main.row_police_officer.view.*

class PoliceOfficerAdapter(
   var context: PoliceOfficerActivity,
  var  officerList: ArrayList<PoliceOfficerResponse.Data>?

) : RecyclerView.Adapter<PoliceOfficerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoliceOfficerAdapter.ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_police_officer,
            parent,
            false
        ) as RowPoliceOfficerBinding

        return PoliceOfficerAdapter.ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
       return officerList!!.size
    }

    fun setData( list: ArrayList<PoliceOfficerResponse.Data>){
        officerList=list
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: PoliceOfficerAdapter.ViewHolder, position: Int) {
        holder.bind(this, context, officerList!!, position)
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            adapter: PoliceOfficerAdapter,
            context: PoliceOfficerActivity,
            item: ArrayList<PoliceOfficerResponse.Data>,
            index: Int
        ) {

            var selectedId=""

            if(item.get(index).isSelected.equals("false")){
                itemView.selectedImg.setImageResource(R.drawable.ic_unselected)
            }else{
                itemView.selectedImg.setImageResource(R.drawable.ic_selected)
            }
            itemView.selectedImg.setOnClickListener {
                selectedId=item.get(index).police_officer_id!!
                if(item.get(index).isSelected.equals("true")){
                    item.get(index).isSelected="false"
                    selectedId=""
                    } else{
                    for (i in 0..item.size-1 ){
                        item.get(i).isSelected="false"
                    }

                    item.get(index).isSelected="true"
                }
                context.officerId(selectedId)
                adapter.notifyDataSetChanged()


            }
            itemView.officerName.setText(item.get(index).officer_name)

        }
    }
}
package com.dfa.ui.ngo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.dfa.R
import com.dfa.listeners.OnListItemClickListener
import com.dfa.pojo.response.GetComplaintsResponse
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.general_complaints_listing_items.view.*


class GeneralComplaintsListAdapter(
    var context: Context,
    var mList: MutableList<GetComplaintsResponse.Data>,
    private var listener: OnListItemClickListener,
    private var type: Int
) :
    RecyclerView.Adapter<GeneralComplaintsListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //  val v = LayoutInflater.from(parent.context).inflate(R.layout.general_complaints_listing_items, parent, false)

        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.general_complaints_listing_items,
            parent,
            false
        ) as com.dfa.databinding.GeneralComplaintsListingItemsBinding

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, mList.get(position), position, listener, type)

    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var index: Int? = null


        fun bind(
            context: Context,
            item: GetComplaintsResponse.Data,
            index: Int,
            listener: OnListItemClickListener,
            type: Int
        ) {
            this.index = index
            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
            //  Glide.with(context).load(item.image).apply(options).into(itemView.imgCrime)
            itemView.layoutListItem.setOnClickListener {
                listener.onItemClick(item, "full")
            }
            itemView.location.setOnClickListener {
                listener.onItemClick(item, "location")
            }

            if (item.status.isNullOrEmpty())
                itemView.status.visibility = View.GONE
            else itemView.status.visibility = View.VISIBLE

            if (item.police_comment!=null && !item.police_comment.equals("")) itemView.layoutPoliceComment.visibility = View.VISIBLE
            itemView.expandable_comment.text = item.police_comment


            if (type == 2) {

                itemView.location.visibility = View.VISIBLE
                itemView.layoutContact.visibility = View.VISIBLE



                if (item.forwarded == 0) {
                    itemView.forward.visibility = View.VISIBLE
                    itemView.status.visibility = View.GONE
                } else {
                    itemView.forward.visibility = View.GONE
                    itemView.status.visibility = View.VISIBLE
                }


            } else {
                itemView.location.visibility = View.GONE
                itemView.layoutContact.visibility = View.GONE

            }
            // itemView.imgCrime.visibility=View.GONE
            itemView.userNameLayout.visibility = View.GONE
            itemView.layoutCrime.visibility = View.GONE
            var name = "Anonymous"
            if (!(item.name.isNullOrEmpty() || item.name.equals(""))) name = item.name
            itemView.tvUsername.text = name


            itemView.tvCrime.text = item.crime
            itemView.status.text = item.status.toString().toUpperCase()
            itemView.expandable_contactNo.text = item.phone.toString()
            itemView.expandable_Date.text = Utilities.dateFormat(item.created_at!!)
            itemView.expandable_Level.text = "Level " + item.level
            val splited = item.created_at.split(" ")
            val time = splited[1]
            itemView.expandable_Time.text = Utilities.dateFormatFromDate(time)
            itemView.expandable_DescriptionNgo.text = item.description.toString()
            itemView.imgExpandable.setOnClickListener {
                if (itemView.childExpandable.isVisible) {
                    itemView.childExpandable.visibility = View.GONE
                } else {
                    itemView.childExpandable.visibility = View.VISIBLE
                }
            }

        }
    }
}


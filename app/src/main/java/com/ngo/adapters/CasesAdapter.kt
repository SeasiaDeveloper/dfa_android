package com.ngo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.ngo.R
import com.ngo.databinding.ItemCaseBinding
import com.ngo.listeners.OnCaseItemClickListener
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.item_case.view.*

class CasesAdapter(
    var context: Context,
    var mList: MutableList<GetCasesResponse.Data>,
    private var listener: OnCaseItemClickListener,
    private var type: Int
) :
    RecyclerView.Adapter<CasesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_case,
            parent,
            false
        ) as ItemCaseBinding

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
            item: GetCasesResponse.Data,
            index: Int,
            listener: OnCaseItemClickListener,
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

            /*    if (item.status.isNullOrEmpty())
                    itemView.status.visibility = View.GONE
                else itemView.status.visibility = View.VISIBLE*/

            /*  if (item.police_comment!=null && !item.police_comment.equals("")) itemView.layoutPoliceComment.visibility = View.VISIBLE
              itemView.expandable_comment.text = item.police_comment*/

            /* if (type == 2) {
                 itemView.location.visibility = View.VISIBLE
                 itemView.layoutContact.visibility = View.VISIBLE*/

            /* if (item.forwarded == 0) {
                 itemView.forward.visibility = View.VISIBLE
                 itemView.status.visibility = View.GONE
             } else {
                 itemView.forward.visibility = View.GONE
                 itemView.status.visibility = View.VISIBLE
             }*/
            /*  } else {
                  itemView.location.visibility = View.GONE
                  itemView.layoutContact.visibility = View.GONE

              }*/
            // itemView.imgCrime.visibility=View.GONE
            /* itemView.userNameLayout.visibility = View.GONE
             itemView.layoutCrime.visibility = View.GONE
             var name = "Anonymous"
            // if (!(item.name.isNullOrEmpty() || item.name.equals(""))) name = item.name
             itemView.tvUsername.text = name*/


            //   itemView.tvCrime.text = item.crime
            itemView.status.text = item.status.toString().toUpperCase()
            //itemView.expandable_contactNo.text = item.phone.toString()

            itemView.expandable_Date.text = Utilities.changeDateFormat(item.report_data!!)
            itemView.expandable_Level.text = "Level " + item.urgency
            itemView.expandable_Time.text = item.report_time
            itemView.expandable_DescriptionNgo.text = item.info.toString()
            itemView.imgExpandable.setOnClickListener {
                if (itemView.childExpandable.isVisible) {
                    itemView.childExpandable.visibility = View.GONE
                } else {
                    itemView.childExpandable.visibility = View.VISIBLE
                }
            }
            if (item.showDelete == 1) {
                itemView.btnDelete.visibility == View.VISIBLE
            } else {
                itemView.btnDelete.visibility == View.GONE
            }

            //like:
            itemView.layout_like.setOnClickListener {
                if(itemView.img_like_red.visibility == View.GONE){
                    itemView.img_like_red.visibility = View.VISIBLE
                    itemView.img_like.visibility = View.GONE
                }
                else{
                    itemView.img_like.visibility = View.VISIBLE
                    itemView.img_like_red.visibility = View.GONE
                }
            }
        }
    }
}
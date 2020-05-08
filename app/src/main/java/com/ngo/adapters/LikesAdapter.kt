package com.ngo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ngo.R
import com.ngo.databinding.ItemCommentBinding
import com.ngo.pojo.response.GetCommentsResponse
import kotlinx.android.synthetic.main.item_comment.view.*

class LikesAdapter(
    var context: Context,
    var mList: MutableList<GetCommentsResponse.CommentData>
) :
    RecyclerView.Adapter<LikesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_comment,
            parent,
            false
        ) as ItemCommentBinding

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, mList.get(position), position)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var index: Int? = null

        fun bind(
            context: Context,
            item: GetCommentsResponse.CommentData,
            index: Int) {
            this.index = index
            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)

                itemView.txtName.text = item.first_name + " " + item.last_name
                itemView.expandable_comment.text = item.comment

                if (item.profile_pic != null  && item.profile_pic.isNotEmpty()) {
                  try{  Glide.with(context).load(item.profile_pic).into(itemView.imgProfile)}catch (e:Exception){
                      e.printStackTrace()
                  }
                }

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
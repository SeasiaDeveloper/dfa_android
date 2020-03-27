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
import com.ngo.databinding.ItemCaseBinding
import com.ngo.listeners.OnCaseItemClickListener
import com.ngo.pojo.response.Data
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.pojo.response.SignupResponse
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.item_case.view.*

class CasesAdapter(
    var context: Context,
    var mList: MutableList<GetCasesResponse.DataBean>,
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
            item: GetCasesResponse.DataBean,
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

            //in case of post:
            if (item.type == "1") {
                itemView.layout_post.visibility = View.VISIBLE
                itemView.layoutListItem.visibility = View.GONE

                val userDetail: Data = item.userDetail!!
                itemView.txtUserNameForPost.text =
                    userDetail.first_name + " " + userDetail.last_name
                itemView.txtDateForPost.text =
                    Utilities.changeDateFormat(item.report_data!!) + " " + item.report_time
                itemView.txtPostInfo.text = item.info

                if (item.media_list!!.isNotEmpty()) {
                    val mediaUrl: String = item.media_list[0]
                    Glide.with(context).load(mediaUrl).into(itemView.imgMediaPost)
                }

                //profile img:
                val profileImgUrl: String = userDetail.profile_pic
                Glide.with(context).load(profileImgUrl).into(itemView.imgPostProfile)

                //btnDelete visibility
                if (item.showDelete == 1) {
                    itemView.btnDeletePost.visibility = View.VISIBLE
                } else {
                    itemView.btnDeletePost.visibility = View.GONE
                }

                itemView.btnDeletePost.setOnClickListener {
                    //api to delete post
                    listener.onDeleteItem(item)
                }


                //like:
                itemView.txtPostLikeNo.text = item.like_count
                itemView.txtPostCommentNo.text = item.comment_count

                if (item.is_liked == 0) {
                    itemView.img_like_post.visibility = View.VISIBLE
                    itemView.img_like_red_post.visibility = View.GONE
                } else {
                    itemView.img_like_red_post.visibility = View.VISIBLE
                    itemView.img_like_post.visibility = View.GONE
                }

                itemView.layout_like_post.setOnClickListener {
                    if (itemView.img_like_red_post.visibility == View.GONE) {
                        itemView.img_like_red_post.visibility = View.VISIBLE
                        itemView.img_like_post.visibility = View.GONE
                        item.is_liked = 1 //the post is liked
                    } else {
                        itemView.img_like_post.visibility = View.VISIBLE
                        itemView.img_like_red_post.visibility = View.GONE
                        item.is_liked = 0 //the post is disliked
                    }

                    listener.changeLikeStatus(item)
                }

            }
            else {
                //in case of complaint:
                itemView.layout_post.visibility = View.GONE
                itemView.layoutListItem.visibility = View.VISIBLE

                itemView.status.text = item.status.toString().toUpperCase()
                itemView.expandable_Date.text = Utilities.changeDateFormat(item.report_data!!)
                itemView.expandable_Level.text = "Level " + item.urgency
                itemView.expandable_Time.text = item.report_time
                itemView.expandable_DescriptionNgo.text = item.info.toString()

                val userDetail: Data = item.userDetail!!
                itemView.expandable_contactNo.text = userDetail.username
                itemView.expandable_username.text = userDetail.first_name + " "+ userDetail.last_name

                if (item.media_list!!.isNotEmpty()) {
                    val mediaUrl: String = item.media_list[0]
                    Glide.with(context).load(mediaUrl).into(itemView.imgComplaintMedia)
                }

                itemView.imgExpandable.setOnClickListener {
                    if (itemView.childExpandable.isVisible) {
                        itemView.childExpandable.visibility = View.GONE
                    } else {
                        itemView.childExpandable.visibility = View.VISIBLE
                    }
                }
                if (item.showDelete == 1) {
                    itemView.btnDelete.visibility = View.VISIBLE
                } else {
                    itemView.btnDelete.visibility = View.GONE
                }

                itemView.btnDelete.setOnClickListener {
                    //hit api to delete complaint
                    listener.onDeleteItem(item)
                }

                //like:
                itemView.txtLikeNo.text = item.like_count
                itemView.txtCommentNo.text = item.comment_count

                if (item.is_liked == 0) {
                    itemView.img_like.visibility = View.VISIBLE
                    itemView.img_like_red.visibility = View.GONE
                } else {
                    itemView.img_like_red.visibility = View.VISIBLE
                    itemView.img_like.visibility = View.GONE
                }


                itemView.layout_like.setOnClickListener {
                    if (itemView.img_like_red.visibility == View.GONE) {
                        itemView.img_like_red.visibility = View.VISIBLE
                        itemView.img_like.visibility = View.GONE
                        item.is_liked = 1 //the post is liked
                    } else {
                        itemView.img_like.visibility = View.VISIBLE
                        itemView.img_like_red.visibility = View.GONE
                        item.is_liked = 0 //the post is disliked
                    }

                    listener.changeLikeStatus(item)
                }
            }
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
        //itemView.expandable_contactNo.text = item.phone.toString()

    }
}
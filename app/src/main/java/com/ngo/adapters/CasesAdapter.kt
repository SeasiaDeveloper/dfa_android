package com.ngo.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ngo.R
import com.ngo.databinding.ItemCaseBinding
import com.ngo.listeners.AlertDialogListener
import com.ngo.listeners.OnCaseItemClickListener
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.ui.comments.CommentsActivity
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.item_case.view.*

class CasesAdapter(
    var context: Context,
    var mList: MutableList<GetCasesResponse.Data>,
    private var listener: OnCaseItemClickListener,
    private var type: Int, private var alertDialogListener: AlertDialogListener
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
        holder.bind(context, mList.get(position), position, listener, type, alertDialogListener)

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setList(mList: MutableList<GetCasesResponse.Data>) {
        this.mList = mList
        notifyDataSetChanged()
    }

    fun String.intOrString(): Any {
        val v = toIntOrNull()
        return when (v) {
            null -> this
            else -> v
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var index: Int? = null


        fun bind(
            context: Context,
            item: GetCasesResponse.Data,
            index: Int,
            listener: OnCaseItemClickListener,
            type: Int, alertDialogListener: AlertDialogListener
        ) {
            this.index = index

            val userDetail: GetCasesResponse.Data.UserDetail = item.userDetail!!

            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)

            itemView.layoutListItem.setOnClickListener {
                listener.onItemClick(item, "full")
            }
            itemView.location.setOnClickListener {
                listener.onItemClick(item, "location")
            }

            //in case of post:
            if (userDetail.profile_pic != null) {
                Glide.with(context).load(userDetail.profile_pic).apply(options)
                    .into(itemView.imgPostProfile)

                itemView.imgPostProfile.setOnClickListener {
                    //show enlarged image
                    DisplayLargeImage(context, userDetail, options)
                }
            }
            if (item.type == "1") {
                itemView.layout_post.visibility = View.VISIBLE
                itemView.layoutListItem.visibility = View.GONE


                itemView.txtUserNameForPost.text =
                    userDetail.first_name + " " + userDetail.last_name
                itemView.txtDateForPost.text =
                    Utilities.changeDateFormat(item.report_data!!) + " " + item.report_time
                itemView.txtPostInfo.text = item.info

                if (item.media_list!!.isNotEmpty()) {
                    val mediaUrl: String = item.media_list[0]
                    Glide.with(context).load(mediaUrl).into(itemView.imgMediaPost)
                }

                //btnDelete visibility
                if (item.showDelete == 1) {
                    itemView.btnDeletePost.visibility = View.VISIBLE
                } else {
                    itemView.btnDeletePost.visibility = View.GONE
                }

                itemView.btnDeletePost.setOnClickListener {
                    Utilities.displayDialog(
                        context,
                        context.getString(R.string.delete_post_title),
                        context.getString(R.string.delete_post_heading),
                        item, alertDialogListener
                    )
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

                itemView.layout_post.setOnClickListener {
                    listener.onItemClick(item, "full")
                }

                itemView.layout_share.setOnClickListener {
                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                }

                itemView.layoutCommentPost.setOnClickListener {
                    val intent = Intent(context, CommentsActivity::class.java)
                    intent.putExtra("id", item.id)
                    context.startActivity(intent)
                }

            } else {
                //in case of complaint:
                if (userDetail.profile_pic != null) {
                    Glide.with(context).load(userDetail.profile_pic).apply(options)
                        .into(itemView.imgCrime)

                    itemView.imgCrime.setOnClickListener {
                        //show enlarged image
                        DisplayLargeImage(context, userDetail, options)
                    }
                }
                itemView.layout_post.visibility = View.GONE
                itemView.layoutListItem.visibility = View.VISIBLE

                //   itemView.status.text = item.status.toString().toUpperCase()
                itemView.expandable_Date.text = Utilities.changeDateFormat(item.report_data!!)
                itemView.expandable_Level.text = "Level " + item.urgency
                itemView.expandable_Time.text = item.report_time
                itemView.expandable_DescriptionNgo.text = item.info.toString()

                itemView.expandable_contactNo.text = userDetail.username
                itemView.expandable_username.text =
                    userDetail.first_name + " " + userDetail.last_name

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
                    Utilities.displayDialog(
                        context,
                        context.getString(R.string.delete_case_heading),
                        context.getString(R.string.delete_case_message),
                        item, alertDialogListener
                    )
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


                itemView.layoutComment.setOnClickListener {
                    val intent = Intent(context, CommentsActivity::class.java)
                    intent.putExtra("id", item.id)
                    context.startActivity(intent)
                }

                itemView.layout_share.setOnClickListener {
                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                }
            }

            //in case of NGO and police
            if ((type == 1) || (type == 2)) {
                itemView.location.visibility = View.VISIBLE
                itemView.layoutContact.visibility = View.VISIBLE
                itemView.action_complaint.visibility = View.VISIBLE
                itemView.location.setOnClickListener {
                    listener.onItemClick(item, "location")
                }
                itemView.action_complaint.setOnClickListener {
                    listener.onItemClick(item, "action")
                }

                itemView.layoutCrimeType.visibility = View.VISIBLE
                itemView.layoutStatus.visibility = View.VISIBLE
                itemView.txtCrimeType.text = item.crime_type
                itemView.txtStatus.text = item.status
                itemView.txtUrgencyTitle.text = context.getString(R.string.urgency)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    itemView.txtUrgencyTitle.setTextColor(
                        context.resources.getColor(
                            (R.color.colorDarkGreen),
                            context.getTheme()
                        )
                    )
                    itemView.expandable_Level.setTextColor(
                        context.resources.getColor(
                            (R.color.colorDarkGreen),
                            context.getTheme()
                        )
                    )
                } else {
                    itemView.txtUrgencyTitle.setTextColor(context.resources.getColor(R.color.colorDarkGreen))
                    itemView.expandable_Level.setTextColor(context.resources.getColor(R.color.colorDarkGreen))
                }


                if (item.latitude != null) {
                    val string = item.latitude
                    var numeric = true

                    try {
                        val num = string?.toDouble()
                    } catch (e: NumberFormatException) {
                        numeric = false
                    }

                    if (numeric)
                        itemView.location.setText(
                            Utilities.calculateDistance(
                                item.latitude,
                                item.longitude,
                                context
                            ).toString() + " KM away"
                        )
                }

                //to show action button in case of Police
                if (type == 2) {
                    if (item.is_assigned.equals("1")) {
                        itemView.action_complaint.visibility = View.VISIBLE
                    } else {
                        itemView.action_complaint.visibility = View.GONE
                    }
                }

            } else {
                //in case of general public/general user
                itemView.location.visibility = View.GONE
                itemView.layoutContact.visibility = View.GONE
                itemView.action_complaint.visibility = View.GONE
                itemView.layoutCrimeType.visibility = View.GONE
                itemView.layoutStatus.visibility = View.GONE
                itemView.txtUrgencyTitle.text = context.getString(R.string.urgency_level_title)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    itemView.txtUrgencyTitle.setTextColor(
                        context.resources.getColor(
                            (R.color.black),
                            context.getTheme()
                        )
                    )
                    itemView.expandable_Level.setTextColor(
                        context.resources.getColor(
                            (R.color.black),
                            context.getTheme()
                        )
                    )
                } else {
                    itemView.txtUrgencyTitle.setTextColor(context.resources.getColor(R.color.black))
                    itemView.expandable_Level.setTextColor(context.resources.getColor(R.color.black))
                }
            }
        }

        fun String.intOrString(): Any {
            val v = toIntOrNull()
            return when (v) {
                null -> this
                else -> v
            }
        }

        fun DisplayLargeImage(
            context: Context,
            userDetail: GetCasesResponse.Data.UserDetail,
            options: RequestOptions
        ) {
            //show enlarged image
            val binding =
                DataBindingUtil.inflate<ViewDataBinding>(
                    LayoutInflater.from(context),
                    R.layout.alert_image_view,
                    null,
                    false
                )

            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(binding.root)

            val imageView = (dialog.findViewById(R.id.imgView) as ImageView)
            Glide.with(context).load(userDetail.profile_pic).apply(options).into(imageView)
            dialog.show()
        }
    }
}
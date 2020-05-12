package com.ngo.adapters

import android.app.Activity
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
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ngo.R
import com.ngo.databinding.ItemCaseBinding
import com.ngo.listeners.AlertDialogListener
import com.ngo.listeners.OnCaseItemClickListener
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.pojo.response.UpdateStatusSuccess
import com.ngo.ui.commentlikelist.CommentLikeUsersList
import com.ngo.ui.comments.CommentsActivity
import com.ngo.ui.contactus.ContactUsActivity
import com.ngo.ui.profile.ProfileActivity
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.item_case.view.*


class CasesAdapter(
    var context: Context,
    var mList: MutableList<GetCasesResponse.Data>,
    private var listener: OnCaseItemClickListener,
    private var type: Int, private var alertDialogListener: AlertDialogListener,
    var activity: Activity
) :
    RecyclerView.Adapter<CasesAdapter.ViewHolder>() {
    //private val isLoadingAdded = false
    private val mCurrentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_case,
            parent,
            false
        ) as ItemCaseBinding

        return ViewHolder(binding.root)
    }

/*    fun updateParticularPosition(position: Int,GetCasesResponse.Data) {

        notifyItemChanged(position)
    }*/

    fun notifyActionData(listItems: Array<UpdateStatusSuccess.Data>) {
        val data = listItems[0]
        var position: Int? = null
        for (i in 0..this.mList.size - 1) {
            if (listItems[0].id.equals(this.mList.get(i).id)) {
                position = i
                break
            }
        }
        this.mList.get(position!!).status = data.status
        notifyItemChanged(position)
    }

    //to add comment
    fun notifyParticularItemWithComment(
        complaintId: String,
        data: List<GetCasesResponse.Data>,
        commentsCounts: Int
    ) {
        var commentCount: String? = ""
        for (i in 0..this.mList.size - 1) {
            if (complaintId.equals(this.mList.get(i).id)) {
                for (j in 0..data.size - 1) {
                    if (complaintId.equals(data.get(j).id)) {
                        commentCount = data.get(j).comment_count!!.toString()
                    }
                }
                if (commentCount.equals("")) {
                    this.mList.get(i).comment_count =
/* (this.mList.get(i).comment_count?.toInt()!! + 1)*/commentsCounts.toString()
                } else {
                    this.mList.get(i).comment_count = commentCount.toString()
                }

                notifyItemChanged(i)
                //notifyItemRangeChanged(i, getItemCount())
                break
            }
        }
    }

    //for delete
    fun removeAt(position: Int) {
        this.mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.mList.size)
    }

    //for like
    fun notifyParticularItem(complaintId: String, data: List<GetCasesResponse.Data>) {
        var likeCount: String? = ""
        for (i in 0..this.mList.size - 1) {
            if (complaintId.equals(this.mList.get(i).id)) {
                for (j in 0..data.size - 1) {
                    if (complaintId.equals(data.get(j).id)) {
                        likeCount = data.get(j).like_count!!.toString()
                    }
                }
                if (likeCount.equals("")) {
                    if (this.mList.get(i).is_liked!!.equals(0)) {
                        this.mList.get(i).like_count =
                            (this.mList.get(i).like_count?.toInt()!! - 1).toString()
                    } else {
                        this.mList.get(i).like_count =
                            (this.mList.get(i).like_count?.toInt()!! + 1).toString()
                    }
                } else {
                    this.mList.get(i).like_count = likeCount.toString()
                }

                notifyItemChanged(i)
                //notifyItemRangeChanged(i, getItemCount())
                break
            }
        }
    }

    fun addDataInMyCases(
        mLayoutManager: LinearLayoutManager,
        listItems: MutableList<GetCasesResponse.Data>
    ) {
        var size = this.mList.size
        this.mList.addAll(listItems)
        //this.mList.addAll(mLayoutManager.getItemCount(),listItems)
        var sizeNew = this.mList.size
        notifyDataSetChanged()
        // notifyItemRangeChanged(size, sizeNew)
    }

    fun clear() {
        val size: Int = mList.size
        mList.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, mList.get(position), position, listener, type, alertDialogListener,activity)

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
            type: Int, alertDialogListener: AlertDialogListener,activity :Activity
        ) {
            this.index = index

            val userDetail: GetCasesResponse.Data.UserDetail = item.userDetail!!

            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)

            itemView.layoutListItem.setOnClickListener {
                listener.onItemClick(item, "full", adapterPosition)
            }
            /*itemView.location.setOnClickListener {
                listener.onItemClick(item, "location")
            }*/
            itemView.view_fir.setOnClickListener {
                listener.onItemClick(item, "webview", adapterPosition)
            }

            //in case of post:
            if (userDetail.profile_pic != null) {
                try {
                    Glide.with(context).load(userDetail.profile_pic).apply(options)
                        .into(itemView.imgPostProfile)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

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
                    Utilities.changeDateFormat(item.report_data!!) + " " + Utilities.changeTimeFormat(
                        item.report_time!!
                    )
                itemView.txtPostInfo.text = item.info

                if (item.media_list!=null && item.media_list.isNotEmpty()) {
                    itemView.imgMediaPost.visibility = View.VISIBLE
                    val mediaUrl: String = item.media_list[0]
                    try {
                        Glide.with(context).load(mediaUrl).into(itemView.imgMediaPost)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    itemView.imgMediaPost.visibility = View.GONE
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
                        item, alertDialogListener, adapterPosition
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

                itemView.layout_like_post.setOnLongClickListener {
                    val intent = Intent(context, CommentLikeUsersList::class.java)
                    intent.putExtra("for", "liked")
                    intent.putExtra("id", item.id)
                    context.startActivity(intent)
                    true
                }

                itemView.layout_post.setOnClickListener {
                    listener.onItemClick(item, "full", adapterPosition)
                }

                itemView.layout_share.setOnClickListener {
                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain" //http://pingnpaark.app.link/U43Z7CV0Q3

                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "DFA")
                    sharingIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Hi, Your friend " + "Akash" + " " + "Gharu" +
                                "  sent you a post Click here app\n www.dfa.com/home?id=" + "1" + ""
                    )
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"))

                }
                itemView.layout_share_post.setOnClickListener {
                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain" //http://pingnpaark.app.link/U43Z7CV0Q3

                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "DFA")
                    sharingIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Hi, Your friend " + "Akash" + " " + "Gharu" +
                                "  sent you a post. Click here app\n www.dfa.com/home?id=" + "1" + ""
                    )
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"))

                }
                itemView.layoutCommentPost.setOnClickListener {
                    listener.onItemClick(item, "comment", adapterPosition)
                }

                itemView.layoutCommentPost.setOnLongClickListener {
                    val intent = Intent(context, CommentLikeUsersList::class.java)
                    intent.putExtra("for", "commented")
                    intent.putExtra("id", item.id)
                    context.startActivity(intent)
                    true
                }


            } else {
                //in case of complaint:

                itemView.layout_post.visibility = View.GONE
                itemView.layoutListItem.visibility = View.VISIBLE

                //   itemView.status.text = item.status.toString().toUpperCase()
                itemView.expandable_Date.text =
                    Utilities.changeDateFormat(item.report_data!!) + " " + Utilities.changeTimeFormat(
                        item.report_time!!
                    )
                itemView.expandable_Level.text = "Level " + item.urgency
                //  itemView.expandable_Time.text =
                if (!item.info.toString().isEmpty() && item.info != null) {
                    itemView.layout_info.visibility = View.VISIBLE
                    itemView.expandable_DescriptionNgo.visibility = View.VISIBLE
                    itemView.expandable_DescriptionNgo.text = item.info.toString()
                } else {
                    itemView.layout_info.visibility = View.GONE
                }

                itemView.expandable_contactNo.text = userDetail.username
                itemView.expandable_username.text =
                    userDetail.first_name + " " + userDetail.last_name

                if (item.media_list!!.isNotEmpty()) {
                    val mediaUrl: String = item.media_list[0]
                    try {
                        Glide.with(context).load(mediaUrl).into(itemView.imgComplaintMedia)
                    } catch (e: Exception) {
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
                if (item.showDelete == 1) {
                    itemView.btnDelete.visibility = View.VISIBLE
                } else {
                    itemView.btnDelete.visibility = View.INVISIBLE
                }

                itemView.btnDelete.setOnClickListener {
                    Utilities.displayDialog(
                        context,
                        context.getString(R.string.delete_case_heading),
                        context.getString(R.string.delete_case_message),
                        item, alertDialogListener, adapterPosition
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


                itemView.layout_like.setOnLongClickListener {
                    val intent = Intent(context, CommentLikeUsersList::class.java)
                    intent.putExtra("for", "liked")
                    intent.putExtra("id", item.id)
                    context.startActivity(intent)
                    true
                }

                itemView.layoutComment.setOnClickListener {
                    val intent = Intent(context, CommentsActivity::class.java)
                    intent.putExtra("id", item.id)
                    context.startActivity(intent)
                }

                itemView.layoutComment.setOnLongClickListener {
                    val intent = Intent(context, CommentLikeUsersList::class.java)
                    intent.putExtra("for", "commented")
                    intent.putExtra("id", item.id)
                    context.startActivity(intent)
                    true
                }



                itemView.layout_share.setOnClickListener {
                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                }

                //if NGO show profile image
                if(type == 1)
                {  if (userDetail.profile_pic != null) {
                    try {
                        Glide.with(context).load(userDetail.profile_pic).apply(options)
                            .into(itemView.imgCrime)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }}

                    itemView.imgCrime.setOnClickListener {
                        //show enlarged image
                        DisplayLargeImage(context, userDetail, options)
                    }

                    itemView.expandable_username.setOnClickListener{
                        val intent = Intent(activity,ProfileActivity::class.java)
                        intent.putExtra("id",item.userDetail.id)
                        context.startActivity(intent)
                    }

                }
                //in case of General public or police
                else{
                    Glide.with(context).load(getImage(context,"app_icon")).apply(options).into(itemView.imgCrime)
                    itemView.expandable_username.text = context.resources.getString(R.string.drug_free_arunachal)
                    itemView.expandable_username.setOnClickListener{
                        context.startActivity(Intent(activity, ContactUsActivity::class.java))
                      //  Toast.makeText(context,context.resources.getString(R.string.contact_ngo_message),Toast.LENGTH_LONG).show()
                    }
                }

            }

            //in case of NGO and police
            if ((type == 1) || (type == 2)) {
                //itemView.location.visibility = View.VISIBLE
                itemView.layoutContact.visibility = View.VISIBLE
                itemView.action_complaint.visibility = View.VISIBLE
                /* itemView.location.setOnClickListener {
                     listener.onItemClick(item, "location")
                 }*/


                itemView.layoutCrimeType.visibility = View.VISIBLE
                itemView.layoutStatus.visibility = View.VISIBLE
                itemView.txtCrimeType.text = item.crime_type
                itemView.txtStatus.text = item.status
                itemView.txtUrgencyTitle.text = context.getString(R.string.urgency)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    itemView.txtUrgencyTitle.setTextColor(
                        context.resources.getColor(
                            (R.color.green),
                            context.getTheme()
                        )
                    )
                    itemView.expandable_Level.setTextColor(
                        context.resources.getColor(
                            (R.color.green),
                            context.getTheme()
                        )
                    )
                } else {
                    itemView.txtUrgencyTitle.setTextColor(context.resources.getColor(R.color.green))
                    itemView.expandable_Level.setTextColor(context.resources.getColor(R.color.green))
                }

                //to show action button in case of Police
                if (type == 2) {
                    itemView.action_complaint.setOnClickListener {
                        listener.onItemClick(item, "action", adapterPosition)
                    }
                    if (item.is_assigned.equals("1")) {
                        itemView.action_complaint.visibility = View.VISIBLE
                    } else {
                        itemView.action_complaint.visibility = View.GONE
                    }
                    itemView.layoutContact.visibility = View.GONE
                  //  itemView.view_fir.visibility = View.VISIBLE
                    itemView.imgComplaintMedia.visibility = View.GONE

                    /* else {
                         if (itemView.childExpandable.isVisible) {
                             itemView.view_fir.visibility = View.GONE
                             itemView.imgComplaintMedia.visibility = View.VISIBLE
                         }*/
                } else {
                    //in case of NGO
                  //  itemView.view_fir.visibility = View.GONE
                    itemView.imgComplaintMedia.visibility = View.VISIBLE
                    itemView.action_complaint.setText(item.status)
                }

            } else {
                //in case of general public/general user
                itemView.imgComplaintMedia.visibility = View.VISIBLE
                // itemView.location.visibility = View.GONE
                itemView.layoutContact.visibility = View.GONE
                itemView.action_complaint.visibility = View.GONE
                itemView.layoutCrimeType.visibility = View.GONE
                itemView.layoutStatus.visibility = View.GONE
                itemView.txtUrgencyTitle.text = context.getString(R.string.urgency_level_title)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    itemView.txtUrgencyTitle.setTextColor(
                        context.resources.getColor(
                            (R.color.green),
                            context.getTheme()
                        )
                    )
                    itemView.expandable_Level.setTextColor(
                        context.resources.getColor(
                            (R.color.green),
                            context.getTheme()
                        )
                    )
                } else {
                    itemView.txtUrgencyTitle.setTextColor(context.resources.getColor(R.color.green))
                    itemView.expandable_Level.setTextColor(context.resources.getColor(R.color.green))
                }
            }


            if(!(item.status.equals("Unassigned"))){
                itemView.view_fir.visibility = View.VISIBLE
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
            try {
                Glide.with(context).load(userDetail.profile_pic).apply(options).into(imageView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dialog.show()
        }

       fun getImage(context:Context, imageName: String):Int {
            val drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName())
            return drawableResourceId
        }

    }


}
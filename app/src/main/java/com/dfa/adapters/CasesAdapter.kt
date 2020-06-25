package com.dfa.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.view.*
import android.widget.ImageView
import android.widget.MediaController
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuCompat

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

import com.dfa.R
import com.dfa.application.MyApplication
import com.dfa.databinding.ItemCaseBinding
import com.dfa.listeners.AlertDialogListener
import com.dfa.listeners.OnCaseItemClickListener
import com.dfa.pojo.response.FirImageResponse
import com.dfa.pojo.response.GetCasesResponse
import com.dfa.pojo.response.UpdateStatusSuccess
import com.dfa.ui.commentlikelist.CommentLikeUsersList
import com.dfa.ui.comments.CommentsActivity
import com.dfa.ui.contactus.ContactUsActivity

import com.dfa.ui.generalpublic.VideoPlayerActivity
import com.dfa.ui.generalpublic.view.GeneralPublicHomeFragment
import com.dfa.ui.home.fragments.home.view.HomeActivity
import com.dfa.ui.mycases.MyCasesActivity
import com.dfa.ui.mycases.MyCasesActivity.Companion.PERMISSION_READ_STORAGE
import com.dfa.ui.mycases.MyCasesActivity.Companion.REQUEST_PERMISSIONS
import com.dfa.ui.profile.ProfileActivity
import com.dfa.utils.CheckRuntimePermissions
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.activity_public.*
import kotlinx.android.synthetic.main.item_case.view.*


class CasesAdapter(
    var context: Context,
    var mList: MutableList<GetCasesResponse.Data>,
    private var listener: OnCaseItemClickListener,
    private var type: Int, private var alertDialogListener: AlertDialogListener,
    var activity: Activity,
    var fragment: Fragment,
    var isGeneralPublicFragment: Boolean

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

    fun updateList(list: MutableList<GetCasesResponse.Data>) {
        this.mList = list
        notifyDataSetChanged()
    }

    fun performSearch(searchedText: String) {
        var searchedList = mutableListOf<GetCasesResponse.Data>()
        for (i in 0..this.mList.size - 1) {
            if (this.mList.get(i).id!!.contains(searchedText)) {
                searchedList.add(this.mList.get(i))
            }
        }
        this.mList = ArrayList()
        this.mList.addAll(searchedList)
        notifyDataSetChanged()
    }

    fun notifyPublicHomeActionData(listItems: Array<UpdateStatusSuccess.Data>, statusId: String) {
        val data = listItems[0]
        var position: Int? = null
        for (i in 0..this.mList.size - 1) {
            if (listItems[0].id.equals(this.mList.get(i).id)) {
                position = i
                break
            }
        }
        this.mList.get(position!!).status = data.status

        if (statusId == "6")
            notifyItemRemoved(position)
        else
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
                        break
                    }
                }
                if (commentCount.equals("")) {
                    this.mList.get(i).comment_count = commentsCounts.toString()
                } else {
                    this.mList.get(i).comment_count = commentCount.toString()
                }

                notifyItemChanged(i)
                break
            }
        }
    }

    fun notifyFirImageData(position: Int?, response: FirImageResponse, complaintId: String) {
        //GeneralPublicHomeFragment.isApiHit = true
        var requiredPosition: Int? = null
        if (response.image!!.isNotEmpty()) {
            for (i in 0..this.mList.size - 1) {
                if (this.mList.get(i).id.equals(complaintId)) {
                    requiredPosition = i
                    break
                }
            }
            this.mList.get(requiredPosition!!).fir_image = response.image
            this.mList.get(requiredPosition).isApiHit = true
            notifyItemChanged(requiredPosition)
        }


        /* if (item.fir_image!!.isNotEmpty()) {
             try {
                 Glide.with(context).asBitmap().load(item.fir_image).apply(options)
                     .into(itemView.imgFirMedia)
             } catch (e: Exception) {
                 e.printStackTrace()
             }
         }*/
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
                        break
                    }
                }
                if (likeCount.equals("")) {
                    if (this.mList.get(i).is_liked!!.equals(0)) {
                        this.mList.get(i).like_count =
                            (this.mList.get(i).like_count?.toInt()!! + 1).toString()
                    } else {
                        this.mList.get(i).like_count =
                            (this.mList.get(i).like_count?.toInt()!! - 1).toString()
                    }
                } else {
                    this.mList.get(i).like_count = likeCount.toString()
                }

                notifyItemChanged(i)
                break
            }
        }
    }

    fun notifyParticularItem(complaintId: String) {
        var likeCount: String? = ""
        for (i in 0..this.mList.size - 1) {
            if (complaintId.equals(this.mList.get(i).id)) {
                /*   for (j in 0..data.size - 1) {
                       if (complaintId.equals(data.get(j).id)) {
                           likeCount = data.get(j).like_count!!.toString()
                           break
                       }
                   }*/
                if (this.mList.get(i).is_liked!!.equals(0)) {
                    this.mList.get(i).like_count =
                        (this.mList.get(i).like_count?.toInt()!! + 1).toString()
                } else {
                    this.mList.get(i).like_count =
                        (this.mList.get(i).like_count?.toInt()!! - 1).toString()
                }
                /* else {
                     this.mList.get(i).like_count = likeCount.toString()
                 }*/

                notifyItemChanged(i)
                break
            }
        }
    }

    fun addDataInMyCases(
        mLayoutManager: LinearLayoutManager,
        listItems: MutableList<GetCasesResponse.Data>
    ) {
        this.mList.addAll(listItems)
        notifyDataSetChanged()
    }

    fun clear() {
        val size: Int = mList.size
        mList.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            context,
            mList.get(position),
            position,
            listener,
            type,
            alertDialogListener,
            activity,
            fragment,
            position,
            isGeneralPublicFragment
        )

    }

    override fun getItemCount(): Int {
        return mList.size
    }


    fun setList(mList: MutableList<GetCasesResponse.Data>) {
        this.mList = mList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var index: Int? = null

        fun bind(
            context: Context,
            item: GetCasesResponse.Data,
            index: Int,
            listener: OnCaseItemClickListener,
            type: Int, alertDialogListener: AlertDialogListener, activity: Activity,
            fragment: Fragment,
            position: Int,
            isGeneralPublicFragment: Boolean
        ) {

            this.index = index

            val userDetail: GetCasesResponse.Data.UserDetail = item.userDetail!!
            val username =
                PreferenceHandler.readString(context, PreferenceHandler.USER_FULLNAME, "")
            val token =
                PreferenceHandler.readString(context, PreferenceHandler.AUTHORIZATION, "")

            var popup1 = PopupMenu(activity, itemView.iv_menu)
            if (isGeneralPublicFragment) {
                popup1.inflate(R.menu.delete_menu)
            } else {
                popup1.inflate(R.menu.news_feed_menu)
            }

            MenuCompat.setGroupDividerEnabled(popup1.menu, true);

            itemView.iv_menu!!.setOnClickListener {
                popup1.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item1: MenuItem): Boolean {

                        when (item1.getItemId()) {
                            R.id.delete -> {

                                Utilities.displayDialog(
                                    context,
                                    context.getString(R.string.delete_post_title),
                                    context.getString(R.string.delete_post_heading),
                                    item, alertDialogListener, adapterPosition
                                )

                            }
                            R.id.hide -> {
                                Utilities.displayDialog(
                                    context,
                                    context.getString(R.string.hide_case_heading),
                                    context.getString(R.string.hide_case_message),
                                    item, alertDialogListener, adapterPosition
                                )

                            }
                        }
                        return false
                    }
                })
                popup1.show()
            }



            itemView.layoutListItem.setOnClickListener {
                listener.onItemClick(item, "full", adapterPosition)
            }
            itemView.view_fir.setOnClickListener {
                listener.onItemClick(item, "webview", adapterPosition)
            }

            val options = RequestOptions()
                /* .centerCrop()*/
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
            //in case of post:
            if (userDetail.profile_pic != null) {
                try {
                    Glide.with(context).asBitmap().load(userDetail.profile_pic).apply(options)
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

                val options = RequestOptions()
                    /* .centerCrop()*/
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage)


               if(item.media_type.equals("photos")){
                   if (item.media_list != null && item.media_list.isNotEmpty()) {
                       itemView.imgMediaPost.visibility = View.VISIBLE
                       itemView.videoThumbNialParent.visibility = View.GONE
                       val mediaUrl: String = item.media_list[0]
                       try {
                           Glide.with(context).asBitmap().load(mediaUrl).apply(options)
                               .into(itemView.imgMediaPost)
                       } catch (e: Exception) {
                           e.printStackTrace()
                       }
                   } else {
                       itemView.imgMediaPost.visibility = View.GONE
                   }

               } else if(item.media_type.equals("videos")){
                   if (item.media_list != null && item.media_list.isNotEmpty()) {
                       itemView.imgMediaPost.visibility = View.GONE
                       itemView.videoThumbNialParent.visibility = View.VISIBLE
                       val options = RequestOptions()
                       val mediaUrl: String = item.media_list[0]
//                       Glide.with(context)
//                           .asBitmap()
//                           .load(mediaUrl)
//                           .apply(options)
//                           .into(itemView.videoThumbNial);
                      /* Glide.with(context)
                           .asBitmap()
                           .load(itemView.imgMediaPost)
                           .into(itemView.videoThumbNial);*/


                       Glide.with(context)
                           .asBitmap()
                           .load(mediaUrl)
                           .timeout(60000)
                           .diskCacheStrategy(DiskCacheStrategy.ALL)
                           .placeholder(R.drawable.camera_placeholder)
                           .error(R.drawable.camera_placeholder)
                           .into(itemView.videoThumbNial);


                       itemView.videoThumbNialParent.setOnClickListener {
                           val mediaUrl= item!!.media_list?.get(0)
                           var intent=Intent(context,VideoPlayerActivity::class.java)
                           intent.putExtra("videoPath",mediaUrl)
                           intent.putExtra("documentId",item.id)
                           context.startActivity(intent)
                       }

                   }
               }
                //btnDelete visibility
                if (item.showDelete == 1) {
                    itemView.btnDeletePost.visibility = View.VISIBLE
                    //  itemView.iv_menu.visibility=View.
                } else {
                    itemView.btnDeletePost.visibility = View.GONE
                    // itemView.iv_menu.visibility=View.GONE
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
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Drug Free Arunachal")
                    sharingIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Hi, Your friend $username sent you a complaint Click here app\n www.dfa.com/home?id=" + item.id + ""
                    )
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"))

                }
                itemView.layout_share_post.setOnClickListener {
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Drug Free Arunachal")
                    sharingIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Hi, Your friend $username sent you a post. Click here app\n www.dfa.com/home?id=" + item.id + ""
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

                itemView.expandable_Date.text =
                    Utilities.changeDateFormat(item.report_data!!) + " " + Utilities.changeTimeFormat(
                        item.report_time!!
                    )
                itemView.expandable_Level.text = /*"Level " + */item.urgency

                if (item.status.equals("Unassigned") && !item.info.toString().isEmpty() && item.info != null) {
                    itemView.layout_info.visibility = View.VISIBLE
                    itemView.expandable_DescriptionNgo.visibility = View.VISIBLE
                    itemView.expandable_DescriptionNgo.text = item.info.toString()
                } else {
                    itemView.layout_info.visibility = View.GONE
                }

                itemView.expandable_contactNo.text = userDetail.username
                itemView.expandable_username.text =
                    userDetail.first_name + " " + userDetail.last_name

                val options = RequestOptions()
                    /* .centerCrop()*/
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage)


                if(item.media_type.equals("photos")) {
                    if (item.media_list!!.isNotEmpty()) {
                        val mediaUrl: String = item.media_list[0]
                        try {
                            Glide.with(context).asBitmap().load(mediaUrl).apply(options)
                                .into(itemView.imgComplaintMedia)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                else if(item.media_type.equals("videos")){
                    if (item.media_list != null && item.media_list.isNotEmpty()) {
                        itemView.imgComplaintMedia.visibility = View.GONE
                        itemView.videoThumbNialParent.visibility = View.VISIBLE
                        val mediaUrl: String = item.media_list[0]
                        val options = RequestOptions()
//                        Glide.with(context)
//                            .asBitmap()
//                            .load(mediaUrl).apply(options).into(itemView.videoThumbNial);
                        Glide.with(context)
                            .asBitmap()
                            .load(mediaUrl)
                            .timeout(60000)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.camera_placeholder)
                            .error(R.drawable.camera_placeholder)
                            .into(itemView.videoThumbNial)

                    }
                }
                itemView.videoThumbNialParent.setOnClickListener {
                        val mediaUrl= item!!.media_list?.get(0)
                        var intent=Intent(context,VideoPlayerActivity::class.java)
                        intent.putExtra("videoPath",mediaUrl)
                        intent.putExtra("documentId",item.id)
                        context.startActivity(intent)
                }

                if (item.showDelete == 1) {
                    itemView.btnDelete.visibility = View.GONE
                    itemView.iv_menu.visibility = View.VISIBLE
                } else {
                    itemView.btnDelete.visibility = View.GONE
                    itemView.iv_menu.visibility = View.GONE
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
                    if (!token!!.isEmpty()) {
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
                    } else {
                        com.dfa.utils.alert.AlertDialog.guesDialog(context)
                    }


                }

                itemView.layout_like.setOnLongClickListener {
                    if (!token!!.isEmpty()) {
                        val intent = Intent(context, CommentLikeUsersList::class.java)
                        intent.putExtra("for", "liked")
                        intent.putExtra("id", item.id)
                        context.startActivity(intent)
                    } else {
                        com.dfa.utils.alert.AlertDialog.guesDialog(context)
                    }

                    true
                }

                itemView.layoutComment.setOnClickListener {
                    if (!token!!.isEmpty()) {
                        val intent = Intent(context, CommentsActivity::class.java)
                        intent.putExtra("id", item.id)
                        context.startActivity(intent)
                    } else {
                        com.dfa.utils.alert.AlertDialog.guesDialog(context)
                    }

                }

                itemView.layoutComment.setOnLongClickListener {
                    if (!token!!.isEmpty()) {
                        val intent = Intent(context, CommentLikeUsersList::class.java)
                        intent.putExtra("for", "commented")
                        intent.putExtra("id", item.id)
                        context.startActivity(intent)
                    } else {

                        com.dfa.utils.alert.AlertDialog.guesDialog(context)
                    }

                    true
                }

                itemView.layout_share.setOnClickListener {
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"

                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Drug Free Arunachal")
                    sharingIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Hi, Your friend $username shared you FIR complaint from Drug Free Arunachal app. To see detail, open\n www.drugfreearunachal.org/home?id=" + item.id + "" //"Hi, Your friend $username sent you a complaint. Click here app\n www.dfa.com/home?id=" + item.id + ""
                    )
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"))
                }

                val options1 = RequestOptions()
                    /* .centerCrop()*/
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)

                //if NGO show profile image
                if (type == 1) {

                    itemView.ll_gp.visibility=View.VISIBLE
                    itemView.ll_ngo.visibility=View.GONE
                    if (userDetail.profile_pic != null) {
                        try {
                            Glide.with(context).asBitmap().load(userDetail.profile_pic)
                                .apply(options1)
                                .into(itemView.imgCrime)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    itemView.imgCrime.setOnClickListener {
                        //show enlarged image
                        DisplayLargeImage(context, userDetail, options)
                    }

                    itemView.expandable_username.setOnClickListener {
                        val intent = Intent(activity, ProfileActivity::class.java)
                        intent.putExtra("id", item.userDetail.id)
                        intent.putExtra("fromWhere", "userProfile")
                        context.startActivity(intent)
                    }
                }
                //in case of General public or police
                else {
                    itemView.ll_gp.visibility=View.GONE
                    itemView.ll_ngo.visibility=View.VISIBLE

                    val options1 = RequestOptions()
                        /* .centerCrop()*/
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)

                    Glide.with(context).asBitmap().load(getImage(context, "app_icon"))
                        .apply(options1)
                        .into(itemView.imgCrime)
                    itemView.expandable_username.text =
                        context.resources.getString(R.string.drug_free_arunachal)
                    itemView.expandable_username.setOnClickListener {
                        context.startActivity(Intent(activity, ContactUsActivity::class.java))
                    }
                }
            }

            //in case of NGO and police
            if ((type == 1) || (type == 2)) {
                if (type == 2) {
                    itemView.layoutContact.visibility = View.GONE
                } else {
                    itemView.layoutContact.visibility = View.VISIBLE
                }

                itemView.action_complaint.visibility = View.VISIBLE

                itemView.layoutCrimeType.visibility = View.VISIBLE
                itemView.layoutStatus.visibility = View.VISIBLE
                itemView.txtCrimeType.text = item.crime_type
                itemView.txtStatus.text = item.status
                if(activity is HomeActivity) setColor(itemView.txtStatus,item.status.toString().toLowerCase())
                itemView.txtUrgencyTitle.text = context.getString(R.string.urgency_level)
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
                    //common in Ngo and Police, action button will appear in the list in case of Ngo also
                    itemView.action_complaint.setOnClickListener {
                        listener.onItemClick(item, "action", adapterPosition)
                    }
                    if (item.is_assigned.equals("1")) {
                        itemView.action_complaint.visibility = View.VISIBLE
                    } else {
                        itemView.action_complaint.visibility = View.GONE
                    }
                    itemView.layoutContact.visibility = View.GONE
                    //itemView.imgComplaintMedia.visibility = View.VISIBLE

                } else {
                    //in case of NGO
                    itemView.action_complaint.setOnClickListener {
                        listener.onItemClick(item, "action", adapterPosition)
                    }
                    itemView.action_complaint.visibility = View.VISIBLE

                    //itemView.imgComplaintMedia.visibility = View.VISIBLE
                    //itemView.action_complaint.setText(item.status)
                }

                itemView.location.visibility = View.VISIBLE

                /* var kmInDouble: Double = 0.0
                 try {
                     kmInDouble =
                         item.fir_km!!.toDouble()
                 } catch (e: NumberFormatException) {
                 }

                 val kmValue = String.format("%.2f", kmInDouble)*/
                var distance =
                    Utilities.calculateDistance(item.latitude, item.longitude, context)
                itemView.location.setText("" + distance + "KM away").toString()
                itemView.location.setOnClickListener {
                    val gmmIntentUri =
                        Uri.parse("google.navigation:q=" + item.latitude + "," + item.longitude + "")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                }

                itemView.gpu_case_layout.visibility = View.GONE
                itemView.ngo_case_layout.visibility = View.VISIBLE
                itemView.ngo_case_layout.visibility = View.VISIBLE
                itemView.case_no_ngo.setText(item.id).toString()

            } else {
                //in case of general public/general user
                itemView.gpu_case_layout.visibility = View.VISIBLE
                itemView.ngo_case_layout.visibility = View.GONE
                itemView.case_no.setText(item.id).toString()


                if(item.media_type.equals("videos")){
                    itemView.imgComplaintMedia.visibility = View.GONE
                    itemView.videoThumbNialParent.visibility = View.VISIBLE
                }else{
                    itemView.imgComplaintMedia.visibility = View.VISIBLE
                    itemView.videoThumbNialParent.visibility = View.GONE
                }

                itemView.layoutContact.visibility = View.GONE
                itemView.action_complaint.visibility = View.GONE
                itemView.layoutCrimeType.visibility = View.GONE
                itemView.layoutStatus.visibility = View.GONE
                itemView.txtUrgencyTitle.text = context.getString(R.string.urgency_level)
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


            /*itemView.imgFirMedia.setOnClickListener {
                //show enlarged image
                displayLargeImageofFir(context, item, options)
            }*/

            itemView.imgComplaintMedia.setOnClickListener {
                //show enlarged image
                DisplayLargeImageOfMedia(context, item)
            }

            if (!(item.status.equals("Unassigned"))) {  //change
                // itemView.view_fir.visibility = View.VISIBLE
                if (item.fir_image != null) {
                    itemView.imgFirMedia.visibility = View.VISIBLE

                    val options = RequestOptions()
                        /* .centerCrop()*/
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.noimage)
                        .error(R.drawable.noimage)

                    try {
                        Glide.with(context).asBitmap().load(item.fir_image).apply(options)
                            .into(itemView.imgFirMedia)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    itemView.imgFirMedia.visibility = View.GONE
                }
            } else {
                itemView.imgFirMedia.visibility = View.GONE
            }

            if (!item.status.equals("Unassigned")) {
                item.id
                if (item.isApiHit) {
                    itemView.childExpandable.visibility = View.VISIBLE
                    itemView.imgExpandable.setImageResource(R.drawable.ic_expand_less_black_24dp)
                    itemView.moreLess.setText(R.string.less)
                    if(item.media_type.equals("videos")){
                        itemView.imgComplaintMedia.visibility = View.GONE
                        itemView.videoThumbNialParent.visibility = View.VISIBLE
                    }else{
                        itemView.imgComplaintMedia.visibility = View.VISIBLE
                        itemView.videoThumbNialParent.visibility = View.GONE
                    }
                } else {
                    itemView.childExpandable.visibility = View.GONE
                    itemView.imgExpandable.setImageResource(R.drawable.ic_expand_more_black_24dp)
                    itemView.moreLess.setText(R.string.more)
                }

                itemView.imgExpandable_linear_layout.setOnClickListener {
                        //1st entry
                        if (!item.isApiHit) {
                            //call api:
                            if (isGeneralPublicFragment) {
                                val callMethod = fragment as GeneralPublicHomeFragment
                                callMethod.callFirImageApi(item.id!!, adapterPosition)
                            } else {
                                val myCasesActivity = activity as MyCasesActivity
                                myCasesActivity.callFirImageApi(item.id!!, adapterPosition)
                            }

                        } else {
                            if (itemView.childExpandable.visibility == View.VISIBLE) {
                                itemView.childExpandable.visibility = View.GONE
                                itemView.imgExpandable.setImageResource(R.drawable.ic_expand_more_black_24dp)
                                item.isApiHit = false
                                itemView.moreLess.setText(R.string.more)
                            }
                        }
                }

                //added in case of more or less
               /* itemView.moreLess.setOnClickListener {
                        //1st entry
                        if (!item.isApiHit) {
                            //call api:
                            if (isGeneralPublicFragment) {
                                val callMethod = fragment as GeneralPublicHomeFragment
                                callMethod.callFirImageApi(item.id!!, adapterPosition)
                            } else {
                                val myCasesActivity = activity as MyCasesActivity
                                myCasesActivity.callFirImageApi(item.id!!, adapterPosition)
                            }

                        } else {
                            if (itemView.childExpandable.visibility == View.VISIBLE) {
                                itemView.childExpandable.visibility = View.GONE
                                itemView.imgExpandable.setImageResource(R.drawable.ic_expand_more_black_24dp)
                                item.isApiHit = false
                                itemView.moreLess.setText(R.string.more)
                            }
                        }
                }*/

            } else {
                itemView.imgExpandable_linear_layout.setOnClickListener {
                        if (itemView.childExpandable.visibility == View.VISIBLE) {
                            itemView.childExpandable.visibility = View.GONE
                            itemView.imgExpandable.setImageResource(R.drawable.ic_expand_more_black_24dp)
                            itemView.moreLess.setText(R.string.more)
                        } else {
                            itemView.childExpandable.visibility = View.VISIBLE
                            if(item.media_type.equals("videos")){
                                itemView.imgComplaintMedia.visibility = View.GONE
                                itemView.videoThumbNialParent.visibility = View.VISIBLE
                            }else{
                                itemView.imgComplaintMedia.visibility = View.VISIBLE
                                itemView.videoThumbNialParent.visibility = View.GONE
                            }
                            itemView.imgExpandable.setImageResource(R.drawable.ic_expand_less_black_24dp)
                            itemView.moreLess.setText(R.string.less)
                        }
                }

                //added for less or more
               /* itemView.moreLess.setOnClickListener {
                        if (itemView.childExpandable.visibility == View.VISIBLE) {
                            itemView.childExpandable.visibility = View.GONE
                            itemView.imgExpandable.setImageResource(R.drawable.ic_expand_more_black_24dp)
                            itemView.moreLess.setText(R.string.more)
                        } else {
                            if(item.media_type.equals("videos")){
                                itemView.imgComplaintMedia.visibility = View.GONE
                                itemView.videoThumbNialParent.visibility = View.VISIBLE
                            }else{
                                itemView.imgComplaintMedia.visibility = View.VISIBLE
                                itemView.videoThumbNialParent.visibility = View.GONE
                            }
                            itemView.childExpandable.visibility = View.VISIBLE
                            itemView.imgExpandable.setImageResource(R.drawable.ic_expand_less_black_24dp)
                            itemView.moreLess.setText(R.string.less)
                        }
                }*/
            }
        }

        fun DisplayLargeImageOfMedia(
            context: Context,
            userDetail: GetCasesResponse.Data
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
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
            val imageView = (dialog.findViewById(R.id.imgView) as ImageView)
            val mediaUrl: String = userDetail.media_list?.get(0)!!
            val options = RequestOptions()
                /* .centerCrop()*/
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)

            try {
                Glide.with(context).asBitmap().load(mediaUrl).apply(options).into(imageView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dialog.show()
        }

        fun displayLargeImageofFir(
            context: Context,
            userDetail: GetCasesResponse.Data,
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
                Glide.with(context).asBitmap().load(userDetail.fir_image).apply(options)
                    .into(imageView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dialog.show()
        }

        fun setColor( textView:com.dfa.customviews.CustomtextView,data:String)
        {
           when(data)
           {
               "unassigned"->textView.setTextColor(MyApplication.instance.resources.getColor(R.color.red));
               "approved"->textView.setTextColor(MyApplication.instance.resources.getColor(R.color.yellow));
               "accepted"->textView.setTextColor(MyApplication.instance.resources.getColor(R.color.blue));
               "accept"->textView.setTextColor(MyApplication.instance.resources.getColor(R.color.blue));
               "resolved"->textView.setTextColor(MyApplication.instance.resources.getColor(R.color.green));
               "unauthentic"->textView.setTextColor(MyApplication.instance.resources.getColor(R.color.grey));




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
            val options = RequestOptions()
                /* .centerCrop()*/
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)

            try {
                Glide.with(context).asBitmap().load(userDetail.profile_pic).apply(options)
                    .into(imageView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dialog.show()
        }

        fun getImage(context: Context, imageName: String): Int {
            val drawableResourceId = context.getResources()
                .getIdentifier(imageName, "drawable", context.getPackageName())
            return drawableResourceId
        }
    }
}
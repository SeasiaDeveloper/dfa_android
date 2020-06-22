package com.dfa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dfa.R
import com.dfa.pojo.response.GetPhotosResponse
import com.dfa.ui.home.fragments.photos.view.OnClickOfVideoAndPhoto
import com.dfa.utils.PreferenceHandler
import kotlinx.android.synthetic.main.adapter_photos.view.*

class PhotosAdapter(
    var context: Context,
    var mList: MutableList<GetPhotosResponse.Data>,
    var listener: OnClickOfVideoAndPhoto
) :
    RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_photos, viewGroup, false)
        return ViewHolder(v)
    }

    fun changeList(newList: MutableList<GetPhotosResponse.Data>) {
        mList = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, mList.get(position), position, listener)
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var index: Int? = null
        var ivPhoto: ImageView


        init {
            ivPhoto = itemView.findViewById<View>(R.id.ivPhoto) as ImageView

        }

        fun bind(
            context: Context,
            item: GetPhotosResponse.Data,
            index: Int,
            listener: OnClickOfVideoAndPhoto
        ) {
            this.index = index
            var authorizationToken =
                PreferenceHandler.readString(context, PreferenceHandler.AUTHORIZATION, "")
            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
            try {
                Glide.with(context).load(item.url).apply(options).into(itemView.ivPhoto)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            ivPhoto.setOnClickListener {
                if (!authorizationToken!!.isEmpty()) {
                    listener.getComplaintId(item.complaint_id)
                } else {
                    com.dfa.utils.alert.AlertDialog.guesDialog(context)
                }
            }
        }
    }
}

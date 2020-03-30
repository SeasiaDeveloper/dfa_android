package com.ngo.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ngo.R
import com.ngo.pojo.response.GetPhotosResponse
import com.ngo.ui.home.fragments.photos.view.OnClickOfVideoAndPhoto
import kotlinx.android.synthetic.main.adapter_photos.view.*


class VideosAdapter(
    var context: Context,
    var mList: MutableList<GetPhotosResponse.Data>,
    var listener: OnClickOfVideoAndPhoto
) :
    RecyclerView.Adapter<VideosAdapter.ViewHolder>() {

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
        var ivVideo: ImageView

        init {
            ivPhoto = itemView.findViewById<View>(R.id.ivPhoto) as ImageView
            ivVideo = itemView.findViewById<View>(R.id.ivVideoIcon) as ImageView
        }

        fun bind(
            context: Context,
            item: GetPhotosResponse.Data,
            index: Int,
            listener: OnClickOfVideoAndPhoto
        ) {
            ivVideo.visibility = View.VISIBLE
            this.index = index

            val requestOptions = RequestOptions()
            requestOptions.isMemoryCacheable
            Glide.with(context).setDefaultRequestOptions(requestOptions).load(item.url.toString())
                .into(itemView.ivPhoto)

            ivPhoto.setOnClickListener {
                listener.getComplaintId(item.complaint_id)
            }
        }
    }
}
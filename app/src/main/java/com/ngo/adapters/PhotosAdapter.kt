package com.ngo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ngo.R
import com.ngo.pojo.response.GetPhotosResponse
import kotlinx.android.synthetic.main.adapter_photos.view.*

class PhotosAdapter(var context: Context, var mList: MutableList<GetPhotosResponse.Data>) :
    RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_photos, viewGroup, false)
        return ViewHolder(v)
    }

    fun changeList(newList:MutableList<GetPhotosResponse.Data>) {
        mList = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, mList.get(position), position)
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
                index: Int
            ) {
                this.index = index

                val options = RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage)
                Glide.with(context).load(item.url).apply(options).into(itemView.ivPhoto)
            }
    }
}

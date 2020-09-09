package com.dfa.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dfa.R
import com.dfa.pojo.response.AdvertisementResponse
import com.dfa.ui.generalpublic.view.GeneralPublicHomeFragment
import com.dfa.ui.home.fragments.AdvertiseMentFragment
import jp.wasabeef.glide.transformations.BlurTransformation


class AdvertisementAdapter(
    var context: GeneralPublicHomeFragment,
    var responseObject: ArrayList<AdvertisementResponse.Data>
) :
    RecyclerView.Adapter<AdvertisementAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_advertisement, viewGroup, false)
        return ViewHolder(v)
    }

    fun setData(list: ArrayList<AdvertisementResponse.Data>) {
        responseObject=list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.bind(context, mList.get(position), position)
        if(responseObject!!.get(position).path!=null){
            Glide.with(context).load(responseObject!!.get(position).path).placeholder(R.drawable.noimage).into(holder.ivPhoto)

            Glide.with(context).load(responseObject!!.get(position).path)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(7, 5)))
                .into(holder.ivAddsBackground);
        }



        holder.ivPhoto.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(responseObject!!.get(position).external_link))
            context.startActivity(browserIntent)

        }

    }

    override fun getItemCount(): Int {
        return responseObject!!.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var index: Int? = null
        var ivPhoto: ImageView
        var ivAddsBackground: ImageView

        init {
            ivPhoto = itemView.findViewById<ImageView>(R.id.ivAdds)
            ivAddsBackground = itemView.findViewById<ImageView>(R.id.ivAddsBackground)

        }
    }
}

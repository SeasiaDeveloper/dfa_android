package com.ngo.ui.police.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.ngo.R
import com.ngo.databinding.NgoDetailsToPoliceListingBinding
import com.ngo.listeners.OnMarkStatusClickListener
import com.ngo.pojo.response.PoliceFormData
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.ngo_details_to_police_listing.view.*
import androidx.annotation.Nullable
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener


class NgoDetailsForPoliceAdapter(
    private var context: Context,
    private var mDetailsList: List<PoliceFormData>,
    private var listener: OnMarkStatusClickListener
) :
    RecyclerView.Adapter<NgoDetailsForPoliceAdapter.ListingHolder>() {

    private lateinit var distance: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.ngo_details_to_police_listing,
            parent,
            false
        ) as NgoDetailsToPoliceListingBinding
        return ListingHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return mDetailsList.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ListingHolder, position: Int) {
        val ngoData = mDetailsList[position]
        holder.txtName.text = ngoData.name
        holder.txtCrime.text = ngoData.crime
        holder.txtStatus.text = ngoData.status


        holder.txtStatusMark.setOnClickListener {
            listener.onMarkClick(
                ngoData.forward_id,
                ngoData.status,
                ngoData.police_comment.toString()
            )
        }
        holder.txtLocation.setOnClickListener {
            listener.onLocationClick(ngoData.lat, ngoData.lng)
        }


        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        val options = RequestOptions()
            .centerCrop()
            .placeholder(circularProgressDrawable)
            .error(R.drawable.noimage)

        try {
            Glide.with(context)
                .load("http://stgsp.appsndevs.com:9041/Complaint/api/v1/showcomplaint/" + ngoData.id.toInt())
                .apply(options)
                .into(holder.imgViewPhoto)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        holder.txtViewPhoto.setOnClickListener {
            // listener.onPhotoClick(ngoData.id.toInt())
            listener.onDescPhotoClick(ngoData.image)
        }
        if (ngoData.lat.isNotEmpty() && ngoData.lng.isNotEmpty()) {
            getDistance(ngoData.lat.toDouble(), ngoData.lng.toDouble())
            holder.txtLocation.text = "$distance KM away"
        }
        holder.txtLevel.text = "Level " + ngoData.level

        if (ngoData.level.toInt() < 6) {
            holder.txtLevel.setTextColor(context.getColor(R.color.colorGreen))
            holder.tvUrgency.setTextColor(context.getColor(R.color.colorGreen))

        } else {
            holder.txtLevel.setTextColor(context.getColor(R.color.colorRed))
            holder.tvUrgency.setTextColor(context.getColor(R.color.colorRed))

        }
        val splited = ngoData.created_at.split(" ")
        holder.txtViewDate.text =
            Utilities.dateFormat(ngoData.created_at) + ", at " + Utilities.dateFormatFromDate(
                splited[1]
            )
        val time = splited[1]
        // holder.txtTime.text = Utilities.dateFormatFromDate(time)


        holder.expandable_DescriptionNgo.text = ngoData.description


        holder.img.setOnClickListener {
            if (holder.layout.isVisible) {
                holder.layout.visibility = View.GONE
            } else {
                holder.layout.visibility = View.VISIBLE
            }
        }


    }


    private fun getDistance(lat2: Double, lon2: Double): String {
        val loc1 = Location("A")
        loc1.latitude = lat2
        loc1.longitude = lon2
        val loc2 = Location("B")
        val lat = PreferenceHandler.readString(context, PreferenceHandler.LAT, "")
        val lng = PreferenceHandler.readString(context, PreferenceHandler.LNG, "")
        if (lat!!.isNotEmpty() && lng!!.isNotEmpty()) {
            loc2.latitude = lat.toDouble()
            loc2.longitude = lng.toDouble()
        }
        val distanceInMeters = loc1.distanceTo(loc2)
        val mile = distanceInMeters / 1609.34f
        distance = String.format("%.2f", mile)
        return distance
    }

    private fun milesTokm(distanceInMiles: Double): Double {
        return distanceInMiles * 1.60934
    }

    fun setData(detailsList: List<PoliceFormData>) {
        mDetailsList = detailsList
        notifyDataSetChanged()
    }

    class ListingHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: AppCompatTextView = itemView.tvUsername
        val txtCrime: AppCompatTextView = itemView.tvCrime
        val txtStatus: AppCompatTextView = itemView.tvStatus
        val userImg: CircleImageView = itemView.imgCrime
        val txtStatusMark: AppCompatTextView = itemView.tvMarkStatus
        val txtViewPhoto: AppCompatTextView = itemView.tvViewPhoto
        val imgViewPhoto: ImageView = itemView.imgPhoto
        val txtLocation: AppCompatTextView = itemView.tvLocation
        val txtViewDate: TextView = itemView.expandable_Date
        val txtLevel: TextView = itemView.expandable_Level
        val img: ImageView = itemView.imgExpand
        val layout: LinearLayout = itemView.expandableLayout
        val tvUrgency: TextView = itemView.tvUrgency
        val expandable_DescriptionNgo: TextView = itemView.expandable_DescriptionNgo

    }
}
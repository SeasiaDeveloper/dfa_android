package com.ngo.adapters

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.ngo.R
import com.ngo.pojo.response.GetEmergencyDetailsResponse


class EmergencyDetailsAdapter(
    var context: Activity,
    var mList: ArrayList<GetEmergencyDetailsResponse.Details>
) :
    RecyclerView.Adapter<EmergencyDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_emergency, viewGroup, false)
        return ViewHolder(v)
    }

    fun changeList(newList: ArrayList<GetEmergencyDetailsResponse.Details>) {
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
        var txtName: TextView
        var txtCall: TextView
        var txtNumber: TextView
        var txtDistance: TextView

        init {
            txtName = itemView.findViewById<View>(R.id.txtName) as TextView
            txtCall = itemView.findViewById<View>(R.id.txtCall) as TextView
            txtNumber = itemView.findViewById<View>(R.id.txtNumber) as TextView
            txtDistance = itemView.findViewById<View>(R.id.txtDistance) as TextView

        }

        fun bind(
            context: Activity,
            item: GetEmergencyDetailsResponse.Details,
            index: Int
        ) {
            this.index = index
            txtName.setText(item.name)
            txtNumber.setText(item.contact_number)

            txtCall.setOnClickListener {

                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.contact_number))
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@setOnClickListener
                }
                context.startActivity(intent)
            }
            txtDistance.setOnClickListener {
                //val gmmIntentUri = Uri.parse("geo:27.0846389,93.6042366")
                val gmmIntentUri =
                    Uri.parse("http://maps.google.com/maps?&daddr=27.0846389,93.6042366")
                //
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent)
                }
            }
        }
    }
}

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
import androidx.recyclerview.widget.RecyclerView
import com.ngo.R

class PhoneNumberAdapter (
    var context: Activity,
    var mList:ArrayList<String>
) :
    RecyclerView.Adapter<PhoneNumberAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.phone_number_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, mList.get(position), position)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var index: Int? = null
        var txtCall: TextView
        var txtNumber: TextView
       // var contactsSpinner: Spinner

        init {
            txtCall = itemView.findViewById<View>(R.id.txtCall) as TextView
            txtNumber = itemView.findViewById<View>(R.id.text_number) as TextView
          //  contactsSpinner=itemView.findViewById<View>(R.id.phoneNumberDropDown) as Spinner
        }

        fun bind(
            context: Activity,
            item: String,
            index: Int
        ) {
            this.index = index
            txtNumber.setText(item)

            txtCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item))
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
        }
    }
}

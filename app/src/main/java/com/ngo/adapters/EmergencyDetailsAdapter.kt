package com.ngo.adapters

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.ngo.R
import com.ngo.pojo.response.EmergencyDataResponse
import com.ngo.pojo.response.GetEmergencyDetailsResponse
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_signup.*
import java.sql.Array
import kotlin.random.Random


class EmergencyDetailsAdapter(
    var context: Activity,
    var mList: ArrayList<EmergencyDataResponse.Data>
) :
    RecyclerView.Adapter<EmergencyDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_emergency, viewGroup, false)
        return ViewHolder(v)
    }

    fun changeList(newList: ArrayList<EmergencyDataResponse.Data>) {
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
        var contactsSpinner: Spinner

        init {
            txtName = itemView.findViewById<View>(R.id.txtName) as TextView
            txtCall = itemView.findViewById<View>(R.id.txtCall) as TextView
            txtNumber = itemView.findViewById<View>(R.id.txtNumber) as TextView
            txtDistance = itemView.findViewById<View>(R.id.txtDistance) as TextView
            contactsSpinner=itemView.findViewById<View>(R.id.phoneNumberDropDown) as Spinner
        }

        fun bind(
            context: Activity,
            item: EmergencyDataResponse.Data,
            index: Int
        ) {
            this.index = index
            txtName.setText(item.name)
           // txtNumber.setText(item.mobile)
            val min = 20
            val max = 30
            val random: Int = Random.nextInt(max - min + 1) + min
            var distance =Utilities.calculateDistance(item.latitude,item.longitude,context)
            txtDistance.text = distance.toString()+" "+context.getString(R.string.km_away)

            // Initializing an ArrayAdapter
            val contactsList = ArrayList<String>()
            for (i in 0..item.mobile!!.size-1) {
                contactsList.add(item.mobile.get(i))
            }
            val contactArray = contactsList.toArray(arrayOfNulls<String>(contactsList.size))
            val adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_item, // Layout
                contactArray // Array
            )

            // Set the drop down view resource
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

            // Finally, data bind the spinner object with dapter
            contactsSpinner.adapter = adapter

            // Set an on item selected listener for spinner object
            var selectedData:String=""
            contactsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    // Display the selected item text on text view
                    "Spinner selected : ${parent.getItemAtPosition(position)}"
                    selectedData=contactsList.get(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Another interface callback
                }
            }

            txtCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + selectedData))
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
                    Uri.parse("http://maps.google.com/maps?&daddr="+item.latitude+","+item.longitude)
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

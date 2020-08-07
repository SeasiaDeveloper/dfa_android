package com.dfa.adapters

import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dfa.R
import com.dfa.databinding.RowPoliceStationBinding
import com.dfa.pojo.response.PoliceStationResponse
import com.dfa.ui.generalpublic.PoliceStationActivity
import kotlinx.android.synthetic.main.row_police_station.view.*
import kotlinx.android.synthetic.main.row_police_station.view.selectedImg
import java.util.*
import kotlin.collections.ArrayList

class PoliceStationAdapter(
    var context: PoliceStationActivity,
    var  officerList: ArrayList<PoliceStationResponse.Data>?
) : RecyclerView.Adapter<PoliceStationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoliceStationAdapter.ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_police_station,
            parent,
            false
        ) as RowPoliceStationBinding

        return PoliceStationAdapter.ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return officerList!!.size
    }

    fun setData( list: ArrayList<PoliceStationResponse.Data>){
        officerList=list
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: PoliceStationAdapter.ViewHolder, position: Int) {
        holder.bind(this, context, officerList!!, position)
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            adapter: PoliceStationAdapter,
            context: PoliceStationActivity,
            item: ArrayList<PoliceStationResponse.Data>,
            index: Int
        ) {

            try {
                var address=adapter.address(item.get(index).latitude!!.toDouble(),item.get(index).longitude!!.toDouble())

//           //     var d=item.get(index).distance_in_km!!.toDouble()
//                var  km=  String.format("%.2f", d)
                itemView.tvName.setText("Police Station: "+ item.get(index).name)
//                itemView.tvDistance.setText(km+"km")
                itemView.tvAddress.setText("Address: "+address)


                var selectedId=""

                if(item.get(index).isSelected.equals("false")){
                    itemView.selectedImg.setImageResource(R.drawable.ic_unselected)
                }else{
                    itemView.selectedImg.setImageResource(R.drawable.ic_selected)
                }
                itemView.cardView.setOnClickListener {
                    selectedId=item.get(index).id!!
                    if(item.get(index).isSelected.equals("true")){
                        item.get(index).isSelected="false"
                        selectedId=""
                    } else{
                        for (i in 0..item.size-1 ){
                            item.get(i).isSelected="false"
                        }

                        item.get(index).isSelected="true"
                    }
                    context.officerId(selectedId)
                    adapter.notifyDataSetChanged()


                }



            }catch (e:Exception){

            }


        }
    }


    fun address(latitude:Double,longitude:Double):String{
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(context, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        val address: String = addresses[0]
            .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

//        val city: String = addresses[0].getLocality()
//        val state: String = addresses[0].getAdminArea()
//        val country: String = addresses[0].getCountryName()
//        val postalCode: String = addresses[0].getPostalCode()
//        val knownName: String = addresses[0].getFeatureName()

        return address
    }
}
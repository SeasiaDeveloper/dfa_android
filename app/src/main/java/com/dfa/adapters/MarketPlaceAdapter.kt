package com.dfa.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.Layout
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.MenuCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dfa.R
import com.dfa.pojo.response.MarketPlaceResponse
import com.dfa.ui.home.fragments.marketplace.AddBusinessActivity
import com.dfa.ui.home.fragments.marketplace.MarketPlaceFragment
import com.dfa.utils.PreferenceHandler
import java.text.DecimalFormat

class MarketPlaceAdapter(
    var context: MarketPlaceFragment,
    var responseObject: ArrayList<MarketPlaceResponse.Data>
) :
    RecyclerView.Adapter<MarketPlaceAdapter.ViewHolder>() {

    var expandText1=""
    var lineEndIndex1:Int=0
    var maxLine1:Int=0
    var text1=""



    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_market_place, viewGroup, false)
        return ViewHolder(v)
    }

    fun setData(list: ArrayList<MarketPlaceResponse.Data>) {
        responseObject = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var userId = PreferenceHandler.readString(context.activity!!, PreferenceHandler.USER_ID, "")!!
        Glide.with(context).load(responseObject!!.get(position).banner_path)
            .placeholder(R.drawable.img_placeholder).into(holder.businessImage!!)
        holder.businessName!!.setText(responseObject!!.get(position).name)
        holder.categoriesName!!.setText(responseObject!!.get(position).category_name)
//        holder.tvProduct!!.setShowingChar(100);
      /*  holder.tvProduct!!.setShowingLine(2);
        holder.tvProduct!!.addShowMoreText("More");
        holder.tvProduct!!.addShowLessText("Less");
        holder.tvProduct!!.setShowMoreColor(Color.GREEN);
        holder.tvProduct!!.setShowLessTextColor(Color.GREEN);*/

        holder.tvProduct!!.setText(responseObject.get(position).product)
        holder.expandable_less!!.setText(responseObject!!.get(position).product)


//        holder.tvProduct!!.getLayout().getLineEnd(holder.tvProduct!!.getLineCount() - 1);
        //    makeTextViewResizable(holder,holder.tvProduct!!,2, "...Read more", true)
       // holder.tvProduct!!.text=(holder.tvProduct!!.getLineCount() - 1).toString()




        try {
//            if((holder.expandable_less!!.getLineCount() - 1)>2){
//                holder.tvViewMode!!.visibility=View.VISIBLE
//            } else{
//                holder.tvViewMode!!.visibility=View.GONE
//            }

            if(holder!!.tvProduct!!.text.length>100){
                holder.tvViewMode!!.visibility=View.VISIBLE
                holder.tvDot!!.visibility=View.VISIBLE
//                var text=  holder!!.tvProduct!!.text.trim().substring(0,60)+"...";
//                holder!!.tvProduct!!.text=text
                var line=2.5
                holder!!.tvProduct!!.setMaxLines(line.toInt());

            } else{
                holder.tvViewMode!!.visibility=View.GONE
                holder.tvDot!!.visibility=View.GONE
             //   holder.tvProduct!!.setText(responseObject.get(position).product)
            }

//            Toast.makeText(context.activity,""+lines,Toast.LENGTH_LONG).show()

        }
        catch (e:java.lang.Exception){

        }


        holder.tvViewMode!!.setOnClickListener {
            holder!!.fullView!!.visibility=View.VISIBLE
            holder!!.smallView!!.visibility=View.GONE
        }
        holder.tvViewLess!!.setOnClickListener {
            holder!!.smallView!!.visibility=View.VISIBLE
            holder!!.fullView!!.visibility=View.GONE
        }





        //  Toast.makeText(context.activity,""+holder!!.tvProduct!!.count, Toast.LENGTH_LONG).show()

      ///  makeTextViewResizable(holder.tvProduct!!, 3, "View More", true);


        holder.categoriesId!!.setText("/"+responseObject!!.get(position).id)
//        var dis=String.format("%.2f", responseObject!!.get(position).distance_in_km.toString());

        try {
            if(!responseObject!!.get(position).distance_in_km!!.isEmpty()){
                var  dis = DecimalFormat("##.##").format(responseObject!!.get(position).distance_in_km!!.toDouble()).toString();
                holder.distance!!.setText(dis+" km")

                 holder!!.distance!!.setOnClickListener {
                    val gmmIntentUri =
                        Uri.parse("google.navigation:q=" + responseObject!!.get(position).latitude + "," + responseObject!!.get(position).longitude + "")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                }

            } else{
                holder.distance!!.setText("NA")
            }
        }catch (e:Exception){

        }


        holder.btnCall!!.setOnClickListener {

            val elements= responseObject!!.get(position)!!.mobile!!.split(",")

            var numberList=ArrayList<String>()

            for (i in 0..elements.size-1){
                if(!elements.get(i).isEmpty()){
                    numberList.add(elements.get(i))
                }
            }

            context.openDialog(numberList)
        }

        holder!!.businessImage!!.setOnClickListener {
            context.fullImageDisplaying(responseObject!!.get(position).banner_path)
        }


        if(userId.equals(responseObject!!.get(position).added_by)){
            holder.icMenu!!.visibility=View.VISIBLE
        } else{
            holder.icMenu!!.visibility=View.GONE
        }

        var popup1 = PopupMenu(context.activity, holder.icMenu)

            popup1.inflate(R.menu.market_place_menu)

        MenuCompat.setGroupDividerEnabled(popup1.menu, true);

        holder.icMenu!!.setOnClickListener {
            popup1.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item1: MenuItem): Boolean {

                    when (item1.getItemId()) {
                        R.id.edit -> {
                            var product=responseObject!!.get(position).product
                            var intentt= Intent(context.activity, AddBusinessActivity::class.java)
                            intentt.putExtra("businessId",responseObject!!.get(position).id)
                            intentt.putExtra("businessName",responseObject!!.get(position).name)
                            intentt.putExtra("categoryId",responseObject!!.get(position).category_id)
                            intentt.putExtra("categoryName",responseObject!!.get(position).category_name)
                            intentt.putExtra("address",responseObject!!.get(position).address)
                            intentt.putExtra("pin",responseObject!!.get(position).pincode)
                            intentt.putExtra("contect",responseObject!!.get(position).contact_person)
                            intentt.putExtra("mobile",responseObject!!.get(position).mobile)
                            intentt.putExtra("latitude",responseObject!!.get(position).latitude)
                            intentt.putExtra("longitude",responseObject!!.get(position).longitude)
                            intentt.putExtra("product",responseObject!!.get(position).product)
                            intentt.putExtra("imageUrl",responseObject!!.get(position).banner_path)
                            intentt.putExtra("comingFrom","editBusiness")
                            context.startActivityForResult(intentt,12)
                        }
                        R.id.delete -> {
                            context.deleteBusiness(responseObject!!.get(position).id,position)
                        }
                    }
                    return false
                }
            })
            popup1.show()
        }
    }





    override fun getItemCount(): Int {
        return responseObject.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var index: Int? = null
        var businessImage: ImageView? = null
        var icMenu: ImageView? = null
        var businessName: TextView? = null
        var categoriesName: TextView? = null
        var distance: TextView? = null
        var tvProduct: TextView? = null
         var categoriesId: TextView? = null
         var tvViewMode: TextView? = null
         var tvViewLess: TextView? = null
         var tvDot: TextView? = null
         var expandable_less: TextView? = null
        var btnCall: Button? = null
        var smallView:RelativeLayout?=null
        var fullView:RelativeLayout?=null

        init {
            businessImage = itemView.findViewById(R.id.businessImage)
            tvViewLess = itemView.findViewById(R.id.tvViewLess)
            tvDot = itemView.findViewById(R.id.tvDot)
            expandable_less = itemView.findViewById(R.id.expandable_less)
            tvViewMode = itemView.findViewById(R.id.tvViewMode)
            smallView = itemView.findViewById(R.id.small_view)
            fullView = itemView.findViewById(R.id.full_view)
            categoriesId = itemView.findViewById(R.id.categoriesId)
            icMenu = itemView.findViewById(R.id.icMenu)
            businessName = itemView.findViewById(R.id.businessName)
            categoriesName = itemView.findViewById(R.id.categoriesName)
            distance = itemView.findViewById(R.id.location)
            tvProduct = itemView.findViewById(R.id.expandable_textsss)
            // tvProduct!!.setTag(tvProduct)
            btnCall = itemView.findViewById(R.id.btnCall)
        }
    }


}

package com.dfa.ui.contribute

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dfa.R

class CoupanAdapter(
    var context: ContributeActivity,
    var coupanList: ArrayList<TicketResponse.Data>
) :
    RecyclerView.Adapter<CoupanAdapter.ViewHolder>() {

    var expandText1 = ""
    var lineEndIndex1: Int = 0
    var maxLine1: Int = 0
    var text1 = ""


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_coupan_layout, viewGroup, false)
        return ViewHolder(v)
    }

    fun setData(list: ArrayList<TicketResponse.Data>) {
        coupanList = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CoupanAdapter.ViewHolder, position: Int) {
        val ticket1=coupanList.get(position).Ticket!!.substring(0,2)
        val ticket2=coupanList.get(position).Ticket!!.substring(2,4)
        val ticket3=coupanList.get(position).Ticket!!.substring(4,6)
        holder.ticket_1!!.setText(ticket1)
        holder.ticket_2!!.setText(ticket2)
        holder.ticket_3!!.setText(ticket3)

        holder.unChecked!!.setOnClickListener {
            var cost=0
            var ticketId=StringBuilder()

            holder.checked!!.visibility=View.VISIBLE
            holder.unChecked!!.visibility=View.GONE
            coupanList.get(position).isSelected=true
            for(i in 0..coupanList.size-1){
                if(coupanList.get(i).isSelected!!){
                    cost=cost+1000
                    if (i > 0) {
                        ticketId!!.append(',');
                    }
                    ticketId!!.append(coupanList.get(i).Ticket);
                }
            }
            context.totalCost(cost,ticketId)
        }

        holder.checked!!.setOnClickListener {
            var cost=0
            var ticketId=StringBuilder()
            holder.checked!!.visibility=View.GONE
            holder.unChecked!!.visibility=View.VISIBLE
            coupanList.get(position).isSelected=false
            for(i in 0..coupanList.size-1){
                if(coupanList.get(i).isSelected!!){
                    cost=cost+1000
                    if (i > 0) {
                        ticketId!!.append(',');
                    }
                    ticketId!!.append(coupanList.get(i).Ticket);

                }
            }
            context.totalCost(cost,ticketId)

        }
    }

    override fun getItemCount(): Int {
        return coupanList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ticket_1: TextView? = null
        var ticket_2: TextView? = null
        var ticket_3: TextView? = null
        var checked: ImageView? = null
        var unChecked: ImageView? = null

        init {
            ticket_1=itemView.findViewById(R.id.ticket_1)
            ticket_2=itemView.findViewById(R.id.ticket_2)
            ticket_3=itemView.findViewById(R.id.ticket_3)
            checked=itemView.findViewById(R.id.check)
            unChecked=itemView.findViewById(R.id.un_check)
        }
    }


}

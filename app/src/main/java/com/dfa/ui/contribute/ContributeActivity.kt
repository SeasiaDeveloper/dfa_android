package com.dfa.ui.contribute

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dfa.R
import com.dfa.application.MyApplication
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.databinding.ActivityContributeBinding
import com.dfa.ui.contribute.payment.PaymentActivity
import com.dfa.ui.generalpublic.pagination.EndlessRecyclerViewScrollListenerImplementation
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import kotlinx.android.synthetic.main.activity_contribute.*
import java.util.*
import kotlin.collections.ArrayList

class ContributeActivity : BaseActivity(),EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener,ContributeCallback,
    View.OnClickListener {
 var binding:ActivityContributeBinding?=null
    var endlessScrollListener: EndlessRecyclerViewScrollListenerImplementation? = null
    var adapter:CoupanAdapter?=null
    var presenter:ContributePresenter?=null
    var coupanList:ArrayList<TicketResponse.Data>?=null
    var page=1
    var contactNumber=""
    var userEmail=""
    var name=""
    var ticketsId=""

    override fun getLayout(): Int {
        return R.layout.activity_contribute
    }
    override fun setupUI() {
        binding=viewDataBinding as ActivityContributeBinding
        (binding!!.toolbarLayout as CenteredToolbar).title = getString(R.string.contibure_title)
        (binding!!.toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (binding!!.toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)
        (binding!!.toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        binding!!.viewMore.setOnClickListener(this)
        binding!!.donateButton.setOnClickListener(this)
        binding!!.payButton.setOnClickListener(this)
        coupanList= ArrayList()
        if(intent.getStringExtra("dueAmount")!=null){
            coupanList=intent.getSerializableExtra("dueTicketList") as ArrayList<TicketResponse.Data>
            viewMore.visibility=View.GONE

        } else{
            hitApi(page.toString())
            viewMore.visibility=View.VISIBLE
        }
        setAdapter()
        contactNumber=PreferenceHandler.readString(MyApplication.instance, PreferenceHandler.CONTACT_NUMBER, "")!!
        userEmail=PreferenceHandler.readString(MyApplication.instance, PreferenceHandler.USER_EMAIL, "")!!
        name=PreferenceHandler.readString(MyApplication.instance, PreferenceHandler.USER_FULLNAME, "")!!
    }

    override fun onClick(p0: View?) {
      when(p0!!.id){
          R.id.viewMore->{
              page=page+1
              hitApi(page.toString())
          }
          R.id.donateButton->{
              var intent= Intent(this,DonateActivity::class.java)
              startActivity(intent)
          }
          R.id.payButton->{

              if (!totalCost.equals("0")) {

                  if(contactNumber.isEmpty()){
                      guestUserPopup()
                  } else{
                      val intent= Intent(this,PaymentActivity::class.java)
                      intent.putExtra("totalCost",totalCost)
                      intent.putExtra("contactNumber",contactNumber)
                      intent.putExtra("name",name)
                      intent.putExtra("ticketsId",ticketsId)
                      intent.putExtra("userEmail",userEmail)
                      startActivity(intent)
                  }
              }else{
                  Toast.makeText(this,"Please Select coupon code",Toast.LENGTH_LONG).show()
              }
          }
      }
    }

    fun guestUserPopup(){
        val  dialog = Dialog(this) // Context, this, etc.
        dialog.setContentView(R.layout.email_guest_layout)
        dialog.setCanceledOnTouchOutside(false)
        //  dialog.setTitle(R.string.dialog_title)

        val tvPhoneNumber=dialog.findViewById<EditText>(R.id.tvPhone)
        val tvEmail=dialog.findViewById<EditText>(R.id.tvEmail)
        val tvUserName=dialog.findViewById<EditText>(R.id.tvUserName)
        val btnOk=dialog.findViewById<Button>(R.id.btnOk)
        btnOk.setOnClickListener {
            contactNumber=tvPhoneNumber.text.toString()
            userEmail=tvEmail.text.toString()
            name=tvUserName.text.toString()

            if(name.isEmpty()){
                tvUserName.error="Enter user name"
                tvUserName.requestFocus()
            }

           else if(contactNumber.isEmpty()){
                tvPhoneNumber.error="Enter contact number"
                tvPhoneNumber.requestFocus()
            }
            else if(contactNumber.length<10){
                tvPhoneNumber.error="Enter valid number"
                tvPhoneNumber.requestFocus()
            }

            else if(!isPhoneNumberValid(contactNumber)){
                tvPhoneNumber.error="Enter valid number"
                tvPhoneNumber.requestFocus()
            }

            else if(userEmail.isEmpty()){
                tvEmail.error="Enter email"
                tvEmail.requestFocus()
            }

            else if (!(Utilities.isValidMail(userEmail))){
                tvEmail.error="Enter valid email"
                tvEmail.requestFocus()
            }
            else{
                dialog.dismiss()
                val intent= Intent(this,PaymentActivity::class.java)
             intent.putExtra("totalCost",totalCost)
             intent.putExtra("contactNumber",contactNumber)
             intent.putExtra("name",name)
             intent.putExtra("ticketsId",ticketsId)
             intent.putExtra("userEmail",userEmail)
             startActivity(intent)
            }
        }
        dialog.show()
    }


    fun isPhoneNumberValid(
        phoneNumber: String?

    ): Boolean {
        //NOTE: This should probably be a member variable.
        val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
        try {

            val cCode: String = Locale.getDefault().getCountry()
            val lCode: String = Locale.getDefault().getLanguage()
            val code = lCode + "_" + cCode


            val numberProto: Phonenumber.PhoneNumber = phoneUtil.parse(phoneNumber, "IN")
            return phoneUtil.isValidNumber(numberProto)
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: " + e.toString())
        }
        return false
    }



    fun hitApi(page: String) {
        presenter= ContributePresenter(this)
        var input=TickerInput()
        input.page=page
        input.per_page="10"
        Utilities.showProgress(this)
        presenter!!.getMarketPlaceData(input)
    }



    fun setAdapter() {
        val layoutManager = LinearLayoutManager(this)
        binding!!.rvCouponList.setLayoutManager(layoutManager)
        adapter = CoupanAdapter(this@ContributeActivity, coupanList!!)
        binding!!.rvCouponList.adapter = adapter
    }

    override fun handleKeyboard(): View {
      return parentLayout
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {

    }

    override fun onSuccess(responseObject: TicketResponse) {
        Utilities.dismissProgress()
        if(responseObject.show_tickets.equals("1")){
            binding!!.parentPay.visibility=View.VISIBLE
            binding!!.parentLottery.visibility=View.VISIBLE
            binding!!.noTickets.visibility=View.GONE
        } else{
            binding!!.parentPay.visibility=View.GONE
            binding!!.parentLottery.visibility=View.GONE
            binding!!.noTickets.visibility=View.VISIBLE
        }
        if(responseObject.data!!.size>0){
            coupanList!!.addAll(responseObject.data!!)
            adapter!!.setData(coupanList!!)
            binding!!.viewMore.visibility=View.VISIBLE
        } else{
            Toast.makeText(this,"No more coupon",Toast.LENGTH_LONG).show()
            binding!!.viewMore.visibility=View.GONE
        }
    }

    override fun onFailed(s: String) {
        Toast.makeText(this,"s"+s,Toast.LENGTH_LONG).show()
        Utilities.dismissProgress()
    }
    var totalCost="0"

    fun totalCost(cost: Int, ticketId: StringBuilder?) {
        totalCost=cost.toString()
        binding!!.tvTotalPay!!.setText("Total Rs: "+cost.toString())
        ticketsId=ticketId.toString()!!
    }


}
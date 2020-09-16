package com.dfa.ui.generalpublic

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dfa.R
import com.dfa.adapters.PoliceStationAdapter
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.databinding.ActivityPiliceStationBindingImpl
import com.dfa.pojo.request.AddPoliceStationInput
import com.dfa.pojo.request.LoginRequest
import com.dfa.pojo.response.AddPoliceComplainResponse
import com.dfa.pojo.response.PoliceStationResponse
import com.dfa.ui.generalpublic.model.PoliceStationPresenter
import com.dfa.ui.generalpublic.presenter.PoliceStationCallback
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.activity_login_activity.*
import kotlinx.android.synthetic.main.activity_pilice_station.*

class PoliceStationActivity : BaseActivity(), PoliceStationCallback,View.OnClickListener {

    var binding: ActivityPiliceStationBindingImpl? = null
    var policeStationAdapter: PoliceStationAdapter? = null
    var token = ""
    var complaintId = ""
    var selectedId = ""
    private var presenter: PoliceStationPresenter? = null
    var statonList: ArrayList<PoliceStationResponse.Data>? = null
    override fun getLayout(): Int {
        return R.layout.activity_pilice_station
    }

    override fun setupUI() {
        binding = viewDataBinding as ActivityPiliceStationBindingImpl
        (binding!!.toolbarLayout as CenteredToolbar).title = getString(R.string.police_stations)
        (binding!!.toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (binding!!.toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (binding!!.toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        statonList = ArrayList()
        presenter = PoliceStationPresenter(this)
        setAdapter()
        token = intent.getStringExtra("token")!!
        complaintId = intent.getStringExtra("complaintId")!!
        if (isInternetAvailable()) {
            showProgress()
            presenter!!.getTermsConditions(token)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }

        binding!!.btnSubmitStation.setOnClickListener(this)
    }

    fun setAdapter() {
        policeStationAdapter = PoliceStationAdapter(this@PoliceStationActivity, statonList)
        val horizontalLayoutManager =
            LinearLayoutManager(this@PoliceStationActivity, RecyclerView.VERTICAL, false)
        binding!!.recyclerViewStation?.layoutManager = horizontalLayoutManager
        binding!!.recyclerViewStation?.adapter = policeStationAdapter
    }


    override fun handleKeyboard(): View {
        return policeParentLayout
    }

    override fun getPoliceStation(body: PoliceStationResponse) {
        dismissProgress()
        if (body != null) {
            statonList = body.data

            if(statonList!!.size>0){
                binding!!.noRecordFound.visibility=View.GONE
                binding!!.btnSubmitStation.visibility=View.VISIBLE
                //policeStationAdapter!!.setData(statonList!!)
                setAdapter()
            } else{
                binding!!.noRecordFound.visibility=View.VISIBLE
                binding!!.btnSubmitStation.visibility=View.GONE
            }

        }
    }
    override fun addPoliceComplaint(body: AddPoliceComplainResponse) {
        dismissProgress()
        if(body!=null){
            Toast.makeText(this,""+body.message,Toast.LENGTH_LONG).show()
            val data =  Intent();
            data.putExtra("data","15")
            setResult(RESULT_OK, data);
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    fun officerId(id: String) {
        selectedId = id
    }

    override fun error(serverError: String) {
        dismissProgress()
        Toast.makeText(this,""+serverError, Toast.LENGTH_LONG).show()
    }

    override fun onClick(p0: View?) {
       when(p0!!.id){
           R.id.btnSubmitStation->{
               if(selectedId.isEmpty()){
                   Toast.makeText(this,getString(R.string.please_select_station), Toast.LENGTH_LONG).show()
               } else{
                   if (isInternetAvailable()) {
                       showProgress()
                       var assignInput= AddPoliceStationInput()
                       assignInput.complaint_id=complaintId
                       assignInput.station_id=selectedId
                       presenter!!.addComplaint(assignInput,token)
                   } else {
                       Utilities.showMessage(this, getString(R.string.no_internet_connection))
                   }
               }

           }
       }
    }
}
package com.dfa.ui.generalpublic

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dfa.R
import com.dfa.adapters.PoliceOfficerAdapter
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.databinding.ActivityPoliceOfficerBinding
import com.dfa.pojo.request.AddPoliceStationInput
import com.dfa.pojo.request.AssignedOfficerInput
import com.dfa.pojo.response.AssignOfficedResponse
import com.dfa.pojo.response.PoliceOfficerResponse
import com.dfa.ui.generalpublic.model.PoliceOfficerPresenter
import com.dfa.ui.generalpublic.presenter.PoilceOfficercallback
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.activity_police_officer.*

class PoliceOfficerActivity : BaseActivity(),PoilceOfficercallback, View.OnClickListener {
    var binding:ActivityPoliceOfficerBinding?=null
    private var presenter: PoliceOfficerPresenter?=null
    var token=""
    var complaintId=""
    var officerId=""
    var statusAdapter:PoliceOfficerAdapter?=null
    var officerList:ArrayList<PoliceOfficerResponse.Data>?=null
    override fun getLayout(): Int {
       return R.layout.activity_police_officer
    }

    override fun setupUI() {
        binding=viewDataBinding as ActivityPoliceOfficerBinding
        (binding!!.toolbarLayout as CenteredToolbar).title = getString(R.string.police_officer)
        (binding!!.toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (binding!!.toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (binding!!.toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        presenter= PoliceOfficerPresenter(this)
        officerList= ArrayList()
        setAdapter()
        token=intent.getStringExtra("token")!!
        complaintId=intent.getStringExtra("complaintId")!!

        if (isInternetAvailable()) {
            showProgress()
            presenter!!.getPoliceOfficers(token)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }

        binding!!.btnSubmitOfficer.setOnClickListener(this)
    }
    fun setAdapter(){
        statusAdapter = PoliceOfficerAdapter(this@PoliceOfficerActivity, officerList)
        val horizontalLayoutManager = LinearLayoutManager(this@PoliceOfficerActivity, RecyclerView.VERTICAL, false)
        binding!!.recyclerViewPolice?.layoutManager = horizontalLayoutManager
        binding!!.recyclerViewPolice?.adapter = statusAdapter
    }

    override fun handleKeyboard(): View {
        return parentLayout
    }

    fun officerId(selectedId: String) {
        officerId=selectedId
    }

    override fun getPoliceOfficer(body: PoliceOfficerResponse) {
        dismissProgress()

        if(body.data!=null){
            officerList=body.data
            if(officerList!!.size>0){
                binding!!.noRecordFound.visibility=View.GONE
                binding!!.btnSubmitOfficer.visibility=View.VISIBLE
                statusAdapter!!.setData(officerList!!)
            } else{
                binding!!.noRecordFound.visibility=View.VISIBLE
                binding!!.btnSubmitOfficer.visibility=View.GONE
            }

        }
    }

    override fun assignPoliceOfficer(body: AssignOfficedResponse) {

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

    override fun error(serverError: String) {
        dismissProgress()
        Toast.makeText(this,""+serverError, Toast.LENGTH_LONG).show()

    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.btnSubmitOfficer->{
                if(officerId.isEmpty()){
                    Toast.makeText(this,getString(R.string.please_select_officer),Toast.LENGTH_LONG).show()
                } else{
                    if (isInternetAvailable()) {
                        showProgress()
                        var assignInput=AssignedOfficerInput()
                        assignInput.complaint_id=complaintId
                        assignInput.officer_id=officerId
                        presenter!!.addOfficerConditions(assignInput,token)
                    } else {
                        Utilities.showMessage(this, getString(R.string.no_internet_connection))
                    }
                }
            }
        }
    }
}
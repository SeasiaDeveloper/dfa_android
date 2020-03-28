package com.ngo.ui.crimedetails.view

import android.graphics.Color
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.NGOResponse
import com.ngo.ui.crimedetails.presenter.CrimeDetailsPresenter
import com.ngo.ui.crimedetails.presenter.CrimeDetailsPresenterImpl
import com.ngo.ui.ngoform.presenter.NGOFormPresenter
import com.ngo.ui.ngoform.presenter.NGOFormPresenterImpl
import com.ngo.ui.ngoform.view.NGOFormView
import com.ngo.utils.Constants
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_incident_detail.*
import kotlinx.android.synthetic.main.activity_incident_detail.etDescription
import kotlinx.android.synthetic.main.activity_incident_detail.imgView
import kotlinx.android.synthetic.main.activity_incident_detail.sb_steps_5
import kotlinx.android.synthetic.main.activity_incident_detail.spTypesOfCrime
import kotlinx.android.synthetic.main.activity_incident_detail.toolbarLayout

class IncidentDetailActivity : BaseActivity(), NGOFormView, CrimeDetailsView {
    // private lateinit var complaintsData: GetComplaintsResponse.Data
    private var ngoPresenter: NGOFormPresenter = NGOFormPresenterImpl(this)
    private var crimePresenter: CrimeDetailsPresenter = CrimeDetailsPresenterImpl(this)
    private lateinit var complaintId: String
    private var authorizationToken: String? = null

    override fun getLayout(): Int {
        return R.layout.activity_incident_detail
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.incident_detail)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        // complaintsData = intent.getSerializableExtra(Constants.PUBLIC_COMPLAINT_DATA) as GetComplaintsResponse.Data
        authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        complaintId = intent.getStringExtra(Constants.PUBLIC_COMPLAINT_DATA)
        var crimeDetailsRequest= CrimeDetailsRequest(complaintId)
        crimePresenter.hiCrimeDetailsApi(crimeDetailsRequest, authorizationToken)
        sb_steps_5.setOnRangeChangedListener(null)

    }

    private fun setComplaintData(getCrimeDetailsResponse: GetCrimeDetailsResponse) {
        var name = "Anonymous" //name , email,phone
        /* if (!(getCrimeDetailsResponse.name.isNullOrEmpty() || getCrimeDetailsResponse.name.equals(""))) name =
             getCrimeDetailsResponse.name.toString()
         etUserName.setText(name)
         if (getCrimeDetailsResponse.email!!.isNotEmpty()) {
             etEmail.setText((getCrimeDetailsResponse.email))
         } else {
             etEmail.visibility = View.GONE
             tvEmail.visibility = View.GONE
         }*/
        /*if (getCrimeDetailsResponse.phone!!.isNotEmpty()) {
            etContactNo.setText(getCrimeDetailsResponse.phone)
        } else {
            etContactNo.visibility = View.GONE
            tvContact.visibility = View.GONE
        }*/
        spTypesOfCrime.text = getCrimeDetailsResponse.crime_type

        sb_steps_5.setIndicatorTextDecimalFormat(getCrimeDetailsResponse.urgency)
        etDescription.setText(getCrimeDetailsResponse.info)
        val options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.noimage)
            .error(R.drawable.noimage)
        Glide.with(this).load(getCrimeDetailsResponse.media_list[0]).apply(options).into(imgView)
        var level: Double = 0.0
        if (!getCrimeDetailsResponse.urgency.equals("10"))
            level = getCrimeDetailsResponse.urgency!!.toFloat() * 11.0
        else
            level = 100.0
        sb_steps_5.setProgress(level.toFloat())

        sb_steps_5.isEnabled = false
    }

    override fun handleKeyboard(): View {
        return ngoDetailLayout
    }

    override fun showNGOResponse(response: NGOResponse) {
        dismissProgress()
        Utilities.showMessage(this, response.message)
    }

    override fun getCrimeDetailsSuccess(getCrimeDetailsResponse: GetCrimeDetailsResponse) {
        dismissProgress()
        setComplaintData(getCrimeDetailsResponse)
    }

    override fun getCrimeDetailsFailure() {
        Utilities.showMessage(this, "")
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

}
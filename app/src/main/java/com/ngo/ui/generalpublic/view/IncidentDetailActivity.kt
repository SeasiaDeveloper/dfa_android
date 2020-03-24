package com.ngo.ui.generalpublic.view

import android.graphics.Color
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.response.GetComplaintsResponse
import com.ngo.pojo.response.NGOResponse
import com.ngo.ui.ngoform.presenter.NGOFormPresenter
import com.ngo.ui.ngoform.presenter.NGOFormPresenterImpl
import com.ngo.ui.ngoform.view.NGOFormView
import com.ngo.utils.Constants
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_incident_detail.*
import kotlinx.android.synthetic.main.activity_incident_detail.etContactNo
import kotlinx.android.synthetic.main.activity_incident_detail.etDescription
import kotlinx.android.synthetic.main.activity_incident_detail.etEmail
import kotlinx.android.synthetic.main.activity_incident_detail.etUserName
import kotlinx.android.synthetic.main.activity_incident_detail.imgView
import kotlinx.android.synthetic.main.activity_incident_detail.sb_steps_5
import kotlinx.android.synthetic.main.activity_incident_detail.spTypesOfCrime
import kotlinx.android.synthetic.main.activity_incident_detail.toolbarLayout
import kotlinx.android.synthetic.main.activity_incident_detail.tvContact
import kotlinx.android.synthetic.main.activity_incident_detail.tvEmail
import kotlinx.android.synthetic.main.activity_public.*
import kotlinx.android.synthetic.main.general_complaints_listing_items.view.*

class IncidentDetailActivity : BaseActivity(), NGOFormView {

    private lateinit var complaintsData: GetComplaintsResponse.Data
    private var ngoPresenter: NGOFormPresenter = NGOFormPresenterImpl(this)

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
        complaintsData =
            intent.getSerializableExtra(Constants.PUBLIC_COMPLAINT_DATA) as GetComplaintsResponse.Data
        setComplaintData()
        sb_steps_5.setOnRangeChangedListener(null)

    }

    private fun setComplaintData() {

        var name="Anonymous"
        if(!(complaintsData.name.isNullOrEmpty() || complaintsData.name.equals("")))  name=complaintsData.name.toString()
        etUserName.setText(name)
        if (complaintsData.email!!.isNotEmpty()) {
            etEmail.setText((complaintsData.email))
        } else {
            etEmail.visibility = View.GONE
            tvEmail.visibility=View.GONE
        }
        if (complaintsData.phone!!.isNotEmpty()) {
            etContactNo.setText(complaintsData.phone)
        } else {
            etContactNo.visibility = View.GONE
            tvContact.visibility=View.GONE
        }
        spTypesOfCrime.text = complaintsData.crime

        sb_steps_5.setIndicatorTextDecimalFormat(complaintsData.level)
        etDescription.setText(complaintsData.description)
        val options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.noimage)
            .error(R.drawable.noimage)
        Glide.with(this).load(complaintsData.image).apply(options).into(imgView)
        var level: Double = 0.0
        if(!complaintsData.level.equals("10"))
            level = complaintsData.level !!.toFloat()*11.0
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

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

}
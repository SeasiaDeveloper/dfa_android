package com.ngo.ui.crimedetails.view

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.request.ChangePasswordRequest
import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.NGOResponse
import com.ngo.ui.crimedetails.presenter.CrimeDetailsPresenter
import com.ngo.ui.crimedetails.presenter.CrimeDetailsPresenterImpl
import com.ngo.ui.imagevideo.ImageVideoScreen
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
import kotlinx.android.synthetic.main.adapter_photos.view.*

class IncidentDetailActivity : BaseActivity(), NGOFormView, CrimeDetailsView {
    // private lateinit var complaintsData: GetComplaintsResponse.Data
    private var ngoPresenter: NGOFormPresenter = NGOFormPresenterImpl(this)
    private var crimePresenter: CrimeDetailsPresenter = CrimeDetailsPresenterImpl(this)
    private lateinit var complaintId: String
    private lateinit var postOrComplaint: String
    private var authorizationToken: String? = null
    private lateinit var getCrimeDetailsResponse: GetCrimeDetailsResponse

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
        postOrComplaint = intent.getStringExtra(Constants.POST_OR_COMPLAINT)

        if (postOrComplaint.equals("0")) {
            //complaint
            tvTitle.visibility = View.GONE
            editTitle.visibility = View.GONE
        } else if (postOrComplaint.equals("1")) {
            //post
            tvTitle.visibility = View.VISIBLE
            editTitle.visibility = View.VISIBLE
            tvUSerName.visibility = View.GONE
            etUserName.visibility = View.GONE
            tvEmail.visibility = View.GONE
            etEmail.visibility = View.GONE
            tvContact.visibility = View.GONE
            etContactNo.visibility = View.GONE
            tvTypesOfCrime.visibility = View.GONE
            spTypesOfCrime.visibility = View.GONE
            tvLevel.visibility = View.GONE
            sb_steps_5.visibility = View.GONE
            tvDescription.visibility = View.GONE
            etDescription.visibility = View.GONE
        }

        if (isInternetAvailable()) {
            showProgress()
            var crimeDetailsRequest = CrimeDetailsRequest(complaintId)
            crimePresenter.hiCrimeDetailsApi(crimeDetailsRequest, authorizationToken)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }

        sb_steps_5.setOnRangeChangedListener(null)

        imgView.setOnClickListener {
            var intent = Intent(this, ImageVideoScreen::class.java)
            if (getCrimeDetailsResponse.data?.get(0)?.media_type.equals("videos")) {
                intent.putExtra("fromWhere", "VIDEOS")
            } else {
                intent.putExtra("fromWhere", "IMAGES")
            }
            intent.putExtra(
                Constants.IMAGE_URL,
                getCrimeDetailsResponse.data?.get(0)?.media_list?.get(0)?.toString())
            startActivity(intent)
        }
    }

    private fun setComplaintData(getCrimeDetailsResponse: GetCrimeDetailsResponse) {
        var name = ""
        //getCrimeDetailsResponse.data.get(0).userDetail.username
        if (!(getCrimeDetailsResponse.data?.get(0)?.userDetail?.first_name.isNullOrEmpty() || getCrimeDetailsResponse.data?.get(
                0
            )?.userDetail?.first_name.isNullOrEmpty().equals(
                ""
            ))
        ) {
            name = getCrimeDetailsResponse.data?.get(0)?.userDetail?.first_name.toString()
            etUserName.setText(name)
        }
        if (!getCrimeDetailsResponse.data?.get(0)?.userDetail?.email.isNullOrEmpty()) {
            etEmail.setText(getCrimeDetailsResponse.data?.get(0)?.userDetail?.email)
        } else {
            etEmail.visibility = View.GONE
            tvEmail.visibility = View.GONE
        }
        if (!getCrimeDetailsResponse.data?.get(0)?.userDetail?.mobile.isNullOrEmpty()) {
            etContactNo.setText(getCrimeDetailsResponse.data?.get(0)?.userDetail?.mobile)
        } else {
            etContactNo.visibility = View.GONE
            tvContact.visibility = View.GONE
        }
        spTypesOfCrime.text = getCrimeDetailsResponse.data?.get(0)?.crime_type
        spStatusOfCrime.text = getCrimeDetailsResponse.data?.get(0)?.status

        if (!getCrimeDetailsResponse.data?.get(0)?.urgency.isNullOrEmpty()) {
            sb_steps_5.setIndicatorTextDecimalFormat(getCrimeDetailsResponse.data?.get(0)?.urgency)
        }
        if (!getCrimeDetailsResponse.data?.get(0)?.info.isNullOrEmpty()) {
            etDescription.setText(getCrimeDetailsResponse.data?.get(0)?.info)
        }
        var level: Double = 0.0
        if (!getCrimeDetailsResponse.data?.get(0)?.urgency.isNullOrEmpty() && !getCrimeDetailsResponse.data?.get(
                0
            )?.urgency.equals("10")
        )
            level = getCrimeDetailsResponse.data?.get(0)?.urgency!!.toFloat() * 11.0
        else
            level = 100.0
        sb_steps_5.setProgress(level.toFloat())

        sb_steps_5.isEnabled = false
    }

    fun setPostData(getCrimeDetailsResponse: GetCrimeDetailsResponse) {
        if (!getCrimeDetailsResponse.data?.get(0)?.info.isNullOrEmpty()) {
            editTitle.setText(getCrimeDetailsResponse.data?.get(0)?.info)
        }
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
        this.getCrimeDetailsResponse = getCrimeDetailsResponse
        if (postOrComplaint.equals("0")) {
            setComplaintData(getCrimeDetailsResponse)
        } else {
            setPostData(getCrimeDetailsResponse)
        }

        if (getCrimeDetailsResponse.data?.get(0)?.media_type.equals("videos")) {
            val requestOptions = RequestOptions()
            requestOptions.isMemoryCacheable
            Glide.with(this).setDefaultRequestOptions(requestOptions)
                .load(getCrimeDetailsResponse.data?.get(0)?.media_list?.get(0))
                .into(imgView)
            ivVideoIcon.visibility = View.VISIBLE
        } else {
            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
            Glide.with(this).load(getCrimeDetailsResponse.data?.get(0)?.media_list?.get(0))
                .apply(options)
                .into(imgView)
            ivVideoIcon.visibility = View.GONE
        }
    }

    override fun getCrimeDetailsFailure() {
        Utilities.showMessage(this, "")
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

}
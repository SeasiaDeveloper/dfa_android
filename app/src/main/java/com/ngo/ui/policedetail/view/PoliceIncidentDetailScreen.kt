package com.ngo.ui.policedetail.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.listeners.OnCaseItemClickListener
import com.ngo.listeners.StatusListener
import com.ngo.pojo.request.CasesRequest
import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.request.PoliceDetailrequest
import com.ngo.pojo.response.*
import com.ngo.ui.crimedetails.presenter.CrimeDetailsPresenter
import com.ngo.ui.crimedetails.presenter.CrimeDetailsPresenterImpl
import com.ngo.ui.imagevideo.ImageVideoScreen
import com.ngo.ui.ngoform.presenter.NGOFormPresenter
import com.ngo.ui.ngoform.presenter.NGOFormPresenterImpl
import com.ngo.ui.ngoform.view.NGOFormView
import com.ngo.ui.policedetail.presenter.PoliceDetailPresenter
import com.ngo.ui.policedetail.presenter.PoliceDetailPresenterImpl
import com.ngo.utils.Constants
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_incident_detail.imgView
import kotlinx.android.synthetic.main.activity_incident_detail.ivVideoIcon
import kotlinx.android.synthetic.main.activity_incident_detail.ngoDetailLayout
import kotlinx.android.synthetic.main.activity_incident_detail.spStatusOfCrime
import kotlinx.android.synthetic.main.activity_incident_detail.spTypesOfCrime
import kotlinx.android.synthetic.main.activity_incident_detail.toolbarLayout
import kotlinx.android.synthetic.main.police_detail_layout.*

class PoliceIncidentDetailScreen : BaseActivity(), PoliceDetailView, StatusListener,
    OnCaseItemClickListener {
    override fun statusUpdationSuccess(responseObject: DeleteComplaintResponse) {
        Utilities.dismissProgress()
        Utilities.showMessage(this, responseObject.message.toString())
        //refresh the back fragment
        finish()
    }

    override fun onItemClick(complaintsData: GetCasesResponse.Data, actionType: String) {
        when (actionType) {
            "location" -> {
                val gmmIntentUri =
                    Uri.parse("google.navigation:q=" + complaintsData.latitude + "," + complaintsData.longitude + "")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }
    }

    override fun onDeleteItem(complaintsData: GetCasesResponse.Data) {
        //do nothing
    }

    override fun changeLikeStatus(complaintsData: GetCasesResponse.Data) {
        //do nothing
    }

    override fun onStatusClick(statusId: String) {
        this.statusId = statusId
    }

    override fun onStatusSelected(comment: String) {
        if (statusId == "-1") {
            Utilities.showMessage(this@PoliceIncidentDetailScreen, getString(R.string.select_option_validation))
        } else {
            //hit status update api
            crimePresenter.updateStatus(
                authorizationToken!!,
                complaintId,
                statusId,
                comment
            )
        }
    }

    override fun onListFetchedSuccess(responseObject: GetStatusResponse) {
        Utilities.showStatusDialog("", responseObject, this, this, this)
    }

    // private var ngoPresenter: NGOFormPresenter = NGOFormPresenterImpl(this)
    private var crimePresenter: PoliceDetailPresenter = PoliceDetailPresenterImpl(this)
    private lateinit var complaintId: String
    private var authorizationToken: String? = null
    private lateinit var getCrimeDetailsResponse: GetCrimeDetailsResponse
    private var isKnowPostOrComplaint: String? = null
    private var statusId = "-1"

    override fun getLayout(): Int {
        return R.layout.police_detail_layout
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.complaint_details)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        // complaintsData = intent.getSerializableExtra(Constants.PUBLIC_COMPLAINT_DATA) as GetComplaintsResponse.Data
        authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        complaintId = intent.getStringExtra(Constants.PUBLIC_COMPLAINT_DATA)
        // setUiVisibilityOfdata()

        if (isInternetAvailable()) {
            showProgress()
            var crimeDetailsRequest = PoliceDetailrequest(complaintId, "1")
            crimePresenter.hitCrimeDetailsApi(crimeDetailsRequest, authorizationToken)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }

        /* imgView.setOnClickListener {
             var intent = Intent(this, ImageVideoScreen::class.java)
             if (getCrimeDetailsResponse.data?.get(0)?.media_type.equals("videos")) {
                 intent.putExtra("fromWhere", "VIDEOS")
             } else {
                 intent.putExtra("fromWhere", "IMAGES")
             }
             intent.putExtra(
                 Constants.IMAGE_URL,
                 getCrimeDetailsResponse.data?.get(0)?.media_list?.get(0)?.toString()
             )
             startActivity(intent)
         }
 */

        imgExpandable.setOnClickListener {
            if (image_video_layout.isVisible) {
                image_video_layout.visibility = View.GONE
            } else {
                image_video_layout.visibility = View.VISIBLE
            }

        }

    }

    private fun setComplaintData(getCrimeDetailsResponse: GetCrimeDetailsResponse) {
        var name = ""

        spTypesOfCrime.text = getCrimeDetailsResponse.data?.get(0)?.crime_type
        spStatusOfCrime.text = getCrimeDetailsResponse.data?.get(0)?.status
        level.text = "Level " + getCrimeDetailsResponse.data?.get(0)?.urgency

        if (!getCrimeDetailsResponse.data?.get(0)?.info.isNullOrEmpty()) {
            editDescription.setText(getCrimeDetailsResponse.data?.get(0)?.info)
        }

        tv_reported.setText(getCrimeDetailsResponse.data?.get(0)?.report_time)
    }

    override fun handleKeyboard(): View {
        return ngoDetailLayout
    }

    override fun getCrimeDetailsSuccess(getCrimeDetailsResponse: GetCrimeDetailsResponse) {
        dismissProgress()
        this.getCrimeDetailsResponse = getCrimeDetailsResponse
        /* if (isKnowPostOrComplaint.equals("tohit")) {
             postOrComplaint = getCrimeDetailsResponse.data?.get(0)?.type!!
             setUiVisibilityOfdata()
         }*/

        setComplaintData(getCrimeDetailsResponse)
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

        action_complaint.setOnClickListener {
          //open dialog to fetch status
            crimePresenter.fetchStatusList(authorizationToken!!, "2") // user role 2 is for police
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
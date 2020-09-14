package com.dfa.ui.policedetail.view

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dfa.R
import com.dfa.adapters.StatusAdapter
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.listeners.OnCaseItemClickListener
import com.dfa.listeners.StatusListener
import com.dfa.pojo.request.PoliceDetailrequest
import com.dfa.pojo.response.*
import com.dfa.ui.generalpublic.PoliceOfficerActivity
import com.dfa.ui.generalpublic.PoliceStationActivity
import com.dfa.ui.generalpublic.VideoPlayerActivity
import com.dfa.ui.generalpublic.view.GeneralPublicHomeFragment
import com.dfa.ui.policedetail.presenter.PoliceDetailPresenter
import com.dfa.ui.policedetail.presenter.PoliceDetailPresenterImpl
import com.dfa.utils.Constants
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.activity_incident_detail.imgView
import kotlinx.android.synthetic.main.activity_incident_detail.ivVideoIcon
import kotlinx.android.synthetic.main.activity_incident_detail.ngoDetailLayout
import kotlinx.android.synthetic.main.activity_incident_detail.spStatusOfCrime
import kotlinx.android.synthetic.main.activity_incident_detail.spTypesOfCrime
import kotlinx.android.synthetic.main.activity_incident_detail.toolbarLayout
import kotlinx.android.synthetic.main.police_detail_layout.*

class PoliceIncidentDetailScreen : BaseActivity(), PoliceDetailView, StatusListener,
    OnCaseItemClickListener {
    override fun statusUpdationSuccess(responseObject: UpdateStatusSuccess) {
        Utilities.dismissProgress()
        Utilities.showMessage(this, responseObject.message.toString())
        //refresh the back fragment
        GeneralPublicHomeFragment.changeThroughIncidentScreen = 1

        finish()
    }

    var token = ""
    var currentStatus = ""
    override fun onItemClick(
        complaintsData: GetCasesResponse.Data,
        actionType: String,
        position: Int
    ) {
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

    override fun changeLikeStatus(complaintsData: GetCasesResponse.Data, position: Int) {
        //do nothing
    }

    override fun onStatusClick(statusId: String) {
        this.statusId = statusId
    }

    override fun onStatusSelected(comment: String) {
        if (statusId == "-1") {
            Utilities.dismissProgress()
            Utilities.showMessage(
                this@PoliceIncidentDetailScreen,
                getString(R.string.no_option_selected)
            )
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
        try {
            Utilities.dismissProgress()
            //   Utilities.showStatusDialog("", responseObject, this, this, this)


            if (responseObject.data != null)

                currentStatus = status

            //   currentStatus = complaintsData.status!!
            //Utilities.showProgress(this@MyCasesActivity)
            //hit api based on role
            //presenter.fetchStatusList(token, type)
            var list: ArrayList<GetStatusDataBean> = ArrayList()
            var item1: GetStatusDataBean

            if (currentStatus.equals("Accept")) {
                item1 = GetStatusDataBean("4", "Accept", true)
            } else {
                item1 = GetStatusDataBean("4", "Accept", false)
            }
            list.add(item1)
            if (currentStatus.equals("Reject")) {
                item1 = GetStatusDataBean("6", "Reject", true)
            } else {
                item1 = GetStatusDataBean("6", "Reject", false)
            }
            list.add(item1)
            if (currentStatus.equals("Resolved")) {
                item1 = GetStatusDataBean("7", "Resolved", true)
            } else {
                item1 = GetStatusDataBean("7", "Resolved", false)
            }
            list.add(item1)
            if (currentStatus.equals("Unauthentic")) {
                item1 = GetStatusDataBean("8", "Unauthentic", true)
            } else {
                item1 = GetStatusDataBean("8", "Unauthentic", false)
            }
            list.add(item1)

            if (currentStatus.equals("Assign (to my suborodinate officer)")) {
                item1 = GetStatusDataBean("9", "Assign (to my suborodinate officer)", true)
            } else {
                item1 = GetStatusDataBean("9", "Assign (to my suborodinate officer)", false)
            }
            list.add(item1)
            if (currentStatus.equals("Transfer (to other Jurisdictional police station)")) {
                item1 = GetStatusDataBean(
                    "10",
                    "Transfer (to other Jurisdictional police station)",
                    true
                )
            } else {
                item1 = GetStatusDataBean(
                    "10",
                    "Transfer (to other Jurisdictional police station)",
                    false
                )
            }
            list.add(item1)



            for (element in list) {
                if (element.name.equals(currentStatus)) {
                    element.isChecked = true
                } else {
                    element.isChecked = false
                }
            }
            var data = GetStatusResponse("", 0, list)
            showStatusDialog("", data)


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun showStatusDialog(description: String, responseObject: GetStatusResponse) {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_change_status,
            null,
            false
        ) as com.dfa.databinding.DialogChangeStatusBinding
        // Inflate and set the layout for the dialog
        if (!description.equals("null") && !description.equals("")) binding.etDescription.setText(
            description
        )

        val mStatusList = responseObject.data.toMutableList()
        for (status in mStatusList) {
            if (status.isChecked) {
                statusId = status.id
            }
        }

        //display the list on the screen
        val statusAdapter = StatusAdapter(this, responseObject.data.toMutableList(), this)
        val horizontalLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvStatus?.layoutManager = horizontalLayoutManager
        binding.rvStatus?.adapter = statusAdapter
        binding.btnDone.setOnClickListener {
            if (statusId == "-1") {
                Utilities.showMessage(
                    this@PoliceIncidentDetailScreen,
                    getString(R.string.select_option_validation)
                )
            } else if (statusId.equals("9")) {
                var intent = Intent(this, PoliceOfficerActivity::class.java)
                intent.putExtra("token", token)
                intent.putExtra("complaintId", complaintId)
                startActivity(intent)
                dialog.dismiss()

            } else if (statusId.equals("10")) {
                var intent = Intent(this, PoliceStationActivity::class.java)
                intent.putExtra("token", token)
                intent.putExtra("complaintId", complaintId)
                startActivity(intent)
                dialog.dismiss()

            } else {
                Utilities.showProgress(this@PoliceIncidentDetailScreen)
                //hit status update api
                crimePresenter.updateStatus(
                    token,
                    complaintId,
                    statusId,
                    binding.etDescription.text.toString()
                )
                dialog.dismiss()
            }
        }

        builder.setView(binding.root)
        dialog = builder.create()
        dialog.show()
    }


    // private var ngoPresenter: NGOFormPresenter = NGOFormPresenterImpl(this)
    private var crimePresenter: PoliceDetailPresenter = PoliceDetailPresenterImpl(this)
    private lateinit var complaintId: String
    private var authorizationToken: String? = null
    private lateinit var getCrimeDetailsResponse: GetCrimeDetailsResponse
    private var isKnowPostOrComplaint: String? = null
    private var statusId = "-1"
    var status = ""
    var userId = ""
    var policeRank=""

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
        userId = PreferenceHandler.readString(this, PreferenceHandler.USER_ID, "")!!
         policeRank = PreferenceHandler.readString(this, PreferenceHandler.Rank, "")!!
        token = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")!!

        // complaintsData = intent.getSerializableExtra(Constants.PUBLIC_COMPLAINT_DATA) as GetComplaintsResponse.Data
        authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        complaintId = intent.getStringExtra(Constants.PUBLIC_COMPLAINT_DATA)
        // setUiVisibilityOfdata()

        if (isInternetAvailable()) {
            Utilities.showProgress(this@PoliceIncidentDetailScreen)
            val crimeDetailsRequest = PoliceDetailrequest(complaintId, "1")
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

        /*  imgExpandable.setOnClickListener {
              if (image_video_layout.isVisible) {
                  image_video_layout.visibility = View.GONE
              } else {
                  image_video_layout.visibility = View.VISIBLE
              }

          }*/


    }

    private fun setComplaintData(getCrimeDetailsResponse: GetCrimeDetailsResponse) {
        var name = ""

        spTypesOfCrime.text = getCrimeDetailsResponse.data?.get(0)?.crime_type
        spStatusOfCrime.text = getCrimeDetailsResponse.data?.get(0)?.status
        status = getCrimeDetailsResponse.data?.get(0)?.status!!
        level.text = getCrimeDetailsResponse.data?.get(0)?.urgency

        if (!getCrimeDetailsResponse.data?.get(0)?.info.isNullOrEmpty()) {
            editDescription.setText(getCrimeDetailsResponse.data?.get(0)?.info)
        } else {
            tvDescription.visibility = View.GONE
            editDescription.visibility = View.GONE
        }

        tv_reported.setText(getCrimeDetailsResponse.data?.get(0)?.report_time)
    }

    override fun handleKeyboard(): View {
        return ngoDetailLayout
    }

    override fun getCrimeDetailsSuccess(getCrimeDetailsResponse: GetCrimeDetailsResponse) {
        Utilities.dismissProgress()
        this.getCrimeDetailsResponse = getCrimeDetailsResponse
        /* if (isKnowPostOrComplaint.equals("tohit")) {
             postOrComplaint = getCrimeDetailsResponse.data?.get(0)?.type!!
             setUiVisibilityOfdata()
         }*/

        setComplaintData(getCrimeDetailsResponse)
        if (getCrimeDetailsResponse.data?.get(0)?.media_type.equals("videos")) {
            val requestOptions = RequestOptions()
            requestOptions.isMemoryCacheable

            try {
                Glide.with(this).setDefaultRequestOptions(requestOptions)
                    .load(getCrimeDetailsResponse.data?.get(0)?.media_list?.get(0)).placeholder(
                        R.drawable
                            .camera_placeholder
                    )
                    .into(ivVideoIcon)

                video_layout.visibility = View.VISIBLE
                ivVideoIcon.visibility = View.VISIBLE

            } catch (e: Exception) {
                e.printStackTrace()
            }

            video_layout.setOnClickListener {
                intent = Intent(this, VideoPlayerActivity::class.java)
                intent!!.putExtra(
                    "videoPath",
                    getCrimeDetailsResponse?.data?.get(0)?.media_list?.get(0)?.toString()
                )
                intent!!.putExtra("documentId", getCrimeDetailsResponse?.data?.get(0)?.id)
                startActivity(intent)
            }
        } else {
            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
            try {
                Glide.with(this).load(getCrimeDetailsResponse.data?.get(0)?.media_list?.get(0))
                    .apply(options)
                    .into(imgView)
                video_layout.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        action_complaint.setOnClickListener {
            try {
                Utilities.showProgress(this@PoliceIncidentDetailScreen)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //open dialog to fetch status
            crimePresenter.fetchStatusList(authorizationToken!!, "2") // user role 2 is for police
        }

        if (getCrimeDetailsResponse.data!!.get(0).transfered_to != null) {

            if (getCrimeDetailsResponse.data!!.get(0).transfered_to.equals(userId)) {
                action_complaint.visibility = View.VISIBLE
            } else {
                action_complaint.visibility = View.GONE
            }

        } else{
            if(policeRank.equals("1")){
                action_complaint.visibility = View.VISIBLE
            }
            else {
                action_complaint.visibility = View.GONE
            }
        }

    }

    override fun getCrimeDetailsFailure() {
        Utilities.showMessage(this, "")
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(this, error)
    }
}
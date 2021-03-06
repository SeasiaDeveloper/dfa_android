package com.dfa.ui.crimedetails.view

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
import com.dfa.adapters.CasesAdapter
import com.dfa.adapters.StatusAdapter
import com.dfa.application.MyApplication
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.databinding.DialogChangeStatusBinding
import com.dfa.listeners.OnCaseItemClickListener
import com.dfa.pojo.request.CrimeDetailsRequest
import com.dfa.pojo.response.*
import com.dfa.ui.crimedetails.presenter.CrimeDetailsPresenter
import com.dfa.ui.crimedetails.presenter.CrimeDetailsPresenterImpl
import com.dfa.ui.generalpublic.PoliceOfficerActivity
import com.dfa.ui.generalpublic.PoliceStationActivity
import com.dfa.ui.generalpublic.VideoPlayerActivity
import com.dfa.ui.generalpublic.view.AsyncResponse
import com.dfa.ui.generalpublic.view.GeneralPublicHomeFragment
import com.dfa.ui.imagevideo.ImageVideoScreen
import com.dfa.ui.mycases.MyCasesActivity
import com.dfa.ui.ngoform.view.NGOFormView
import com.dfa.ui.profile.ProfileActivity
import com.dfa.utils.Constants
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import com.dfa.utils.algo.DirectionApiAsyncTask
import kotlinx.android.synthetic.main.activity_incident_detail.*
import kotlinx.android.synthetic.main.item_case.view.*

class IncidentDetailActivity : BaseActivity(), NGOFormView, CrimeDetailsView, AsyncResponse,
    OnCaseItemClickListener {

    private var crimePresenter: CrimeDetailsPresenter = CrimeDetailsPresenterImpl(this)
    private lateinit var complaintId: String
    private lateinit var postOrComplaint: String
    private var authorizationToken: String? = null
    private var getCrimeDetailsResponse: GetCrimeDetailsResponse? = null
    private var isKnowPostOrComplaint: String? = null
    private var latitude: String = ""
    private var longitude: String = ""
    private var currentStatus = ""
    private var adapter: CasesAdapter? = null
    private var statusId = "-1"
    var type = ""

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
        GeneralPublicHomeFragment.fromIncidentDetailScreen = 1
        GeneralPublicHomeFragment.change = 0
        MyCasesActivity.change = 0
        MyCasesActivity.fromIncidentDetailScreen = 1
        type = PreferenceHandler.readString(this, PreferenceHandler.USER_ROLE, "")!!
        authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        complaintId = intent.getStringExtra(Constants.PUBLIC_COMPLAINT_DATA)
        isKnowPostOrComplaint = intent.getStringExtra(Constants.FROM_WHERE)
        if (isKnowPostOrComplaint.equals("nottohit")) {
            postOrComplaint = intent.getStringExtra(Constants.POST_OR_COMPLAINT)
            setUiVisibilityOfdata()
        }

        if (isInternetAvailable()) {
            Utilities.showProgress(this@IncidentDetailActivity)
            val crimeDetailsRequest = CrimeDetailsRequest(complaintId)
            crimePresenter.hiCrimeDetailsApi(crimeDetailsRequest, authorizationToken)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }

        sb_steps_5.setOnRangeChangedListener(null)
        var intent: Intent? = null
        imgView.setOnClickListener {
            if (getCrimeDetailsResponse?.data?.get(0)?.media_type.equals("videos")) {
                intent = Intent(this, VideoPlayerActivity::class.java)
                intent!!.putExtra(
                    "videoPath",
                    getCrimeDetailsResponse?.data?.get(0)?.media_list?.get(0)?.toString()
                )
                intent!!.putExtra("documentId", getCrimeDetailsResponse?.data?.get(0)?.id)
            } else {
                intent = Intent(this, ImageVideoScreen::class.java)
                intent!!.putExtra("fromWhere", "IMAGES")
            }
            intent!!.putExtra(
                Constants.IMAGE_URL,
                getCrimeDetailsResponse?.data?.get(0)?.media_list?.get(0)?.toString()
            )
            startActivity(intent)
        }

        show_location.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    fun setUiVisibilityOfdata() {
        if (postOrComplaint != null) {
            if (postOrComplaint.equals("0")) {
                //complaint
                sb_steps_5.visibility = View.VISIBLE
                title_layout.visibility = View.GONE
                userame_layout.visibility = View.VISIBLE
                email_layout.visibility = View.VISIBLE
                contact_layout.visibility = View.VISIBLE
                typecrime_layout.visibility = View.VISIBLE
                desc_layout.visibility = View.VISIBLE
                status_layout.visibility = View.VISIBLE
                urgency.visibility = View.GONE
                ngo_comment_layout.visibility = View.GONE

            } else if (postOrComplaint.equals("1")) {
                //post
                (toolbarLayout as CenteredToolbar).title = getString(R.string.post_description)
                title_layout.visibility = View.VISIBLE
                desc_layout.visibility = View.GONE
                userame_layout.visibility = View.GONE
                email_layout.visibility = View.GONE
                contact_layout.visibility = View.GONE
                typecrime_layout.visibility = View.GONE
                desc_layout.visibility = View.GONE
                status_layout.visibility = View.GONE
                urgency_level_layout.visibility = View.GONE
                urgency.visibility = View.GONE
                sb_steps_5.visibility = View.GONE
                tvTitle.visibility = View.GONE
                ngo_comment_layout.visibility = View.GONE

            }
        }

        val type = PreferenceHandler.readString(this, PreferenceHandler.USER_ROLE, "")!!
        if (type.equals("2") && postOrComplaint.equals("0")) {
            typecrime_layout.visibility = View.VISIBLE
            status_layout.visibility = View.VISIBLE
            desc_layout.visibility = View.VISIBLE
            contact_layout.visibility = View.GONE
            email_layout.visibility = View.GONE
            userame_layout.visibility = View.GONE
            urgency.visibility = View.VISIBLE
            sb_steps_5.visibility = View.GONE
            urgency_level_layout.visibility = View.VISIBLE
            ngo_comment_layout.visibility = View.VISIBLE
        }

        if (type.equals("2") && postOrComplaint.equals("0") || type.equals("1") && postOrComplaint.equals(
                "0"
            )
        ) {
            show_location.visibility = View.VISIBLE
        } else {
            show_location.visibility = View.GONE
        }

    }

    private fun setComplaintData(getCrimeDetailsResponse: GetCrimeDetailsResponse) {
        var name = ""
        latitude = getCrimeDetailsResponse.data?.get(0)?.latitude!!
        longitude = getCrimeDetailsResponse.data.get(0).longitude!!

        if (!(getCrimeDetailsResponse.data.get(0).userDetail?.first_name.isNullOrEmpty() || getCrimeDetailsResponse.data.get(
                0
            ).userDetail?.first_name.isNullOrEmpty().equals(
                ""
            ))
        ) {
            name = getCrimeDetailsResponse.data.get(0).userDetail?.first_name.toString()
            etUserName.setText(name)
        }
        if (!getCrimeDetailsResponse.data.get(0).userDetail?.email.isNullOrEmpty()) {
            etEmail.setText(getCrimeDetailsResponse.data.get(0).userDetail?.email)
        } else {
            email_layout.visibility = View.GONE
        }
        if (!getCrimeDetailsResponse.data.get(0).userDetail?.mobile.isNullOrEmpty()) {
            etContactNo.setText(getCrimeDetailsResponse.data.get(0).userDetail?.mobile)
        } else {
            contact_layout.visibility = View.GONE
        }
        spTypesOfCrime.text = getCrimeDetailsResponse.data.get(0).crime_type
        spStatusOfCrime.text = getCrimeDetailsResponse.data.get(0).status
        setColor(spStatusOfCrime, getCrimeDetailsResponse.data.get(0).status!!.toLowerCase())

        if (!getCrimeDetailsResponse.data.get(0).urgency.isNullOrEmpty()) {
            sb_steps_5.setIndicatorTextDecimalFormat(getCrimeDetailsResponse.data.get(0).urgency)
        }
        if (!getCrimeDetailsResponse.data.get(0).info.isNullOrEmpty()) {
            etDescription.setText(getCrimeDetailsResponse.data.get(0).info)
        }
        var level: Float = 0f
        if (!getCrimeDetailsResponse.data.get(0).urgency.isNullOrEmpty() && !getCrimeDetailsResponse.data.get(
                0
            ).urgency.equals("10")
        )
            level = (getCrimeDetailsResponse.data.get(0).urgency!!.toFloat() * 11) - 11
        else
            level = 0f

        if (getCrimeDetailsResponse != null && getCrimeDetailsResponse.data != null && getCrimeDetailsResponse.data?.get(
                0
            ) != null && getCrimeDetailsResponse.data?.get(0)?.urgency.equals("10")
        ) {
            level = 99f
        }
        sb_steps_5.setProgress(level)

        sb_steps_5.isEnabled = false
        if (!getCrimeDetailsResponse.data.get(0).urgency.isNullOrEmpty()) {
            urgency.setText(getCrimeDetailsResponse.data.get(0).urgency)
        }

        if (type.equals("2") && postOrComplaint.equals("0")) {
            if (!getCrimeDetailsResponse.data.get(0).ngo_comment.isNullOrEmpty()) {
                ngo_comment.setText(getCrimeDetailsResponse.data.get(0).ngo_comment)
            } else {
                ngo_comment_layout.visibility = View.GONE
            }
        }

        if (getCrimeDetailsResponse.data.get(0).latitude != null) {

            if (type.equals("2") && postOrComplaint.equals("0") || type.equals("1") && postOrComplaint.equals(
                    "0"
                )
            ) {
                val task = DirectionApiAsyncTask(
                    this,
                    getCrimeDetailsResponse.data.get(0).latitude!!,
                    getCrimeDetailsResponse.data.get(0).longitude!!,
                    this
                )
                task.execute()
            } else {
                show_location.visibility = View.GONE
            }

            //in case of NGO
            if (type.equals("1")) {
               // layout_action.visibility = View.VISIBLE
                action_complaint.setOnClickListener {
                    /*  if (getCrimeDetailsResponse.data.get(0).status != null) currentStatus = getCrimeDetailsResponse.data.get(0).status!!
                      //hit api based on role
                      Utilities.showProgress(this@IncidentDetailActivity)
                      crimePresenter.fetchStatusList(authorizationToken!!, type)*/
                    var list: ArrayList<GetStatusDataBean> = ArrayList()
                    var item1: GetStatusDataBean
                    if (type == "1") {
                        if (currentStatus.equals("Approved")) {
                            item1 = GetStatusDataBean("1", "Approved", true)
                        } else {
                            item1 = GetStatusDataBean("1", "Approved", false)
                        }
                        list.add(item1)
                        if (currentStatus.equals("Reject")) {
                            item1 = GetStatusDataBean("6", "Reject", true)
                        } else {
                            item1 = GetStatusDataBean("6", "Reject", false)
                        }
                        list.add(item1)
                    } else {
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
                        if (currentStatus.equals("Transfer (to other jurisdicational police station)")) {
                            item1 = GetStatusDataBean(
                                "10",
                                "Transfer (to other jurisdicational police station)",
                                true
                            )
                        } else {
                            item1 = GetStatusDataBean(
                                "10",
                                "Transfer (to other jurisdicational police station)",
                                false
                            )
                        }
                        list.add(item1)

                    }

                    for (element in list) {
                        if (element.name.equals(currentStatus)) {
                            element.isChecked = true
                        } else {
                            element.isChecked = false
                        }
                    }
                    var data = GetStatusResponse("", 0, list)
                    showStatusDialog("", data)

                }
                etUserName.setOnClickListener {
                    val intent = Intent(this@IncidentDetailActivity, ProfileActivity::class.java)
                    intent.putExtra("id", getCrimeDetailsResponse.data.get(0).userDetail?.id)
                    intent.putExtra("fromWhere", "userProfile")
                    startActivity(intent)
                }
            }

            if (getCrimeDetailsResponse.data.get(0).info.equals("")) {
                desc_layout.visibility = View.GONE
            }
        }

        //in case of police or General Public
        if (type.equals("0") || type.equals("2")) {
            etUserName.setText(resources.getString(R.string.drug_free_arunachal))
            contact_layout.visibility = View.GONE
        }
    }

    fun setPostData(getCrimeDetailsResponse: GetCrimeDetailsResponse) {
        if (!getCrimeDetailsResponse.data?.get(0)?.info.isNullOrEmpty()) {
            title_layout.visibility = View.VISIBLE
            editTitle.setText(getCrimeDetailsResponse.data?.get(0)?.info)
        }

        if (getCrimeDetailsResponse.data?.get(0)?.media_type != null) {
            layout_media.visibility = View.VISIBLE
        } else {
            layout_media.visibility = View.GONE
        }

    }

    override fun handleKeyboard(): View {
        return ngoDetailLayout
    }

    override fun showNGOResponse(response: NGOResponse) {
        Utilities.dismissProgress()
        Utilities.showMessage(this, response.message)
    }

    override fun getCrimeDetailsSuccess(getCrimeDetailsResponse: GetCrimeDetailsResponse) {
        Utilities.dismissProgress()
        this.getCrimeDetailsResponse = getCrimeDetailsResponse
        if (isKnowPostOrComplaint.equals("tohit")) {
            postOrComplaint = getCrimeDetailsResponse.data?.get(0)?.type!!
            setUiVisibilityOfdata()
        }

        if (postOrComplaint.equals("0")) {
            setComplaintData(getCrimeDetailsResponse)
        } else {
            setPostData(getCrimeDetailsResponse)
        }

        if (getCrimeDetailsResponse.data?.get(0)?.media_type.equals("videos")) {
            val requestOptions = RequestOptions()
            requestOptions.isMemoryCacheable
            try {
                Glide.with(this).setDefaultRequestOptions(requestOptions)
                    .load(getCrimeDetailsResponse.data?.get(0)?.media_list?.get(0))
                    .placeholder(R.drawable.grey_bg)
                    .into(imgView)
                ivVideoIcon.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
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
            } catch (e: Exception) {
                e.printStackTrace()
            }

            ivVideoIcon.visibility = View.GONE
        }
    }

    override fun getCrimeDetailsFailure() {
        Utilities.showMessage(this, "")
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun processFinish(output: String?) {
        if (output == null || output.equals("")) {
            show_location.setText(getString(R.string.zero_km_away)).toString()
        } else {
            if (output.contains("mi")) {
                val number = java.lang.Float.valueOf(
                    output.replace(
                        "[^\\d.]+|\\.(?!\\d)".toRegex(),
                        ""
                    )
                ) //val number: String = output.replace("\\D+", "")
                show_location.setText((number.toInt() * 1.60934).toInt().toString() + "KM away")
                    .toString()
            } else {
                show_location.setText(output + " away").toString()
            }
        }
    }

    override fun onListFetchedSuccess(responseObject: GetStatusResponse) {
        Utilities.dismissProgress()
        for (element in responseObject.data) {
            if (element.name.equals(currentStatus)) {
                element.isChecked = true
                break
            }
        }
        showStatusDialog("", responseObject)
    }

    override fun onStatusClick(statusId: String) {
        this.statusId = statusId
    }

    private fun showStatusDialog(description: String, responseObject: GetStatusResponse) {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this@IncidentDetailActivity)
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(this@IncidentDetailActivity),
            R.layout.dialog_change_status,
            null,
            false
        ) as DialogChangeStatusBinding
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
        val statusAdapter =
            StatusAdapter(this@IncidentDetailActivity, responseObject.data.toMutableList(), this)
        val horizontalLayoutManager =
            LinearLayoutManager(this@IncidentDetailActivity, RecyclerView.VERTICAL, false)
        binding.rvStatus?.layoutManager = horizontalLayoutManager
        binding.rvStatus?.adapter = statusAdapter
        binding.btnDone.setOnClickListener {

            if (statusId == "-1") {
                Utilities.showMessage(
                    this@IncidentDetailActivity,
                    getString(R.string.select_option_validation)
                )
            }

            else if (statusId.equals("9")) {
                var intent=Intent(this, PoliceOfficerActivity::class.java)
                intent.putExtra("token",authorizationToken)
                intent.putExtra("complaintId",complaintId)
                startActivity(intent)
                dialog.dismiss()

            } else if (statusId.equals("10")) {
                var intent=Intent(this, PoliceStationActivity::class.java)
                intent.putExtra("token",authorizationToken)
                intent.putExtra("complaintId",complaintId)
                startActivity(intent)
                dialog.dismiss()

            }
            else {
                //hit status update api
                Utilities.showProgress(this@IncidentDetailActivity)
                crimePresenter.updateStatus(
                    authorizationToken!!,
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

    override fun statusUpdationSuccess(
        responseObject: UpdateStatusSuccess,
        complaint: UpdateStatusSuccess.Data
    ) {
        Utilities.dismissProgress()
        Utilities.showMessage(this@IncidentDetailActivity, responseObject.message.toString())
        spStatusOfCrime.setText(complaint.status)
        if (responseObject.data?.size != 0) {
            adapter?.notifyActionData(responseObject.data!!)
        }
        GeneralPublicHomeFragment.changeThroughIncidentScreen = 1
    }

    override fun onItemClick(
        complaintsData: GetCasesResponse.Data,
        actionType: String,
        position: Int
    ) {
        when (actionType) {
            "action" -> {
                if (getCrimeDetailsResponse?.data?.get(0)?.status != null) currentStatus =
                    getCrimeDetailsResponse?.data?.get(0)?.status!!
                //hit api based on role
                Utilities.showProgress(this@IncidentDetailActivity)
                crimePresenter.fetchStatusList(authorizationToken!!, type)
            }
        }
    }

    override fun onDeleteItem(complaintsData: GetCasesResponse.Data) {
        //nothing to do
    }

    override fun changeLikeStatus(complaintsData: GetCasesResponse.Data,position: Int) {
        //nothing to do
    }


    fun setColor( textView:com.dfa.customviews.CustomtextView,data:String)
    {
        when(data)
        {
            "unassigned"->textView.setTextColor(MyApplication.instance.resources.getColor(R.color.red));
            "approved"->textView.setTextColor(MyApplication.instance.resources.getColor(R.color.yellow));
            "accepted"->textView.setTextColor(MyApplication.instance.resources.getColor(R.color.blue));
            "accept"->textView.setTextColor(MyApplication.instance.resources.getColor(R.color.blue));
            "resolved"->textView.setTextColor(MyApplication.instance.resources.getColor(R.color.green));
            "unauthentic"->textView.setTextColor(MyApplication.instance.resources.getColor(R.color.grey));
        }
    }


}
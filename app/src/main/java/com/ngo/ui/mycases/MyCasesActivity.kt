package com.ngo.ui.mycases

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngo.R
import com.ngo.adapters.CasesAdapter
import com.ngo.adapters.StatusAdapter
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.listeners.AlertDialogListener
import com.ngo.listeners.OnCaseItemClickListener
import com.ngo.pojo.request.CasesRequest
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.pojo.response.GetStatusResponse
import com.ngo.pojo.response.SignupResponse
import com.ngo.ui.crimedetails.view.IncidentDetailActivity
import com.ngo.ui.generalpublic.view.GeneralPublicHomeFragment
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenter
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.ngo.ui.home.fragments.cases.view.CasesView
import com.ngo.utils.Constants
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_my_cases.*
import kotlinx.android.synthetic.main.activity_my_cases.toolbarLayout

class MyCasesActivity : BaseActivity(), CasesView, OnCaseItemClickListener, AlertDialogListener {
    override fun onStatusClick(statusId: String) {
        //do nothing for now
    }
    override fun onClick(item: Any) {
        Utilities.showProgress(this@MyCasesActivity)
        val complaintsData = item as GetCasesResponse.Data
        //delete the item based on id
        presenter.deleteComplaint(token, complaintsData.id!!)
    }

    private var presenter: CasesPresenter = CasesPresenterImplClass(this)
    private var complaints: List<GetCasesResponse.Data> = mutableListOf()
    lateinit var casesRequest: CasesRequest
    var token: String = ""
    private var statusId = "-1"
    private var complaintId = "-1"
    private var currentStatus = ""
    var type = ""

    override fun setupUI() {
        token = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")!!

        (toolbarLayout as CenteredToolbar).title = getString(R.string.my_cases)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }

        val horizontalLayoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        rvPublic.layoutManager = horizontalLayoutManager

        casesRequest = CasesRequest(
            "0",
            etSearch.text.toString(),
            "0"
        ) //all = "0" for my cases and for fetching all the cases which are of type = 0
        Utilities.showProgress(this)
        presenter.getComplaints(casesRequest, token)
    }

    //displays my cases list on the view
    override fun showGetComplaintsResponse(response: GetCasesResponse) {
        Utilities.dismissProgress()
        complaints = response.data!!
        if (complaints.isNotEmpty()) {
            tvRecord.visibility = View.GONE
            rvPublic.visibility = View.VISIBLE
             type = PreferenceHandler.readString(this, PreferenceHandler.USER_ROLE, "0")!!
            rvPublic.adapter = CasesAdapter(this, complaints.toMutableList(), this, type.toInt(), this)
        } else {
            tvRecord.visibility = View.VISIBLE
            rvPublic.visibility = View.GONE
        }

        //add click listener after adding the list on the view
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                casesRequest = CasesRequest(
                    "0",
                    etSearch.text.toString(),
                    "0"
                ) //all = "1" for fetching all the cases whose type = 0

                Utilities.showProgress(this@MyCasesActivity)
                //hit api with search variable
                presenter.getComplaints(casesRequest, token)
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })
    }

    //changing the like status
    override fun changeLikeStatus(complaintsData: GetCasesResponse.Data) {
        Utilities.showProgress(this)
        val token = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        //delete the item based on id
        presenter.changeLikeStatus(token!!, complaintsData.id!!)
    }

    //refreshing the list after changing the like status
    override fun onLikeStatusChanged(responseObject: DeleteComplaintResponse) {
        Utilities.showMessage(this, responseObject.message!!)
        val casesRequest = CasesRequest(
            "0",
            "",
            "0"
        ) //all = 0 for only my cases;type = -1 for fetching all the data
        presenter.getComplaints(casesRequest, token)
        GeneralPublicHomeFragment.change =
            1 // so that list on Home gets refreshed after change in status
    }

    //to delete my case
    override fun onDeleteItem(complaintsData: GetCasesResponse.Data) {

    }

    //refreshing the list after deletion
    override fun onComplaintDeleted(responseObject: DeleteComplaintResponse) {
        Utilities.showMessage(this@MyCasesActivity, responseObject.message!!)
        val casesRequest = CasesRequest("0", "", "0") //type = -1 for fetching all the data
        presenter.getComplaints(casesRequest, token)
        GeneralPublicHomeFragment.change =
            1 // so that list on Home gets refreshed after change in status
    }

    //displays the detail of my case
    override fun onItemClick(complaintsData: GetCasesResponse.Data, actionType: String) {
        when (actionType) {
            "location" -> {
                val gmmIntentUri =
                    Uri.parse("google.navigation:q=" + complaintsData.latitude + "," + complaintsData.longitude + "")
                //  val gmmIntentUri = Uri.parse("google.navigation:q="+30.7106607+","+76.7091493+"")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }

            "action" -> {
                complaintId = complaintsData.id!!
                if(complaintsData.status!=null) currentStatus = complaintsData.status
                Utilities.showProgress(this@MyCasesActivity)
                //hit api based on role
                presenter.fetchStatusList(token, actionType)
            }

            else -> {
        val intent = Intent(this, IncidentDetailActivity::class.java)
        intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, complaintsData.id)
        intent.putExtra(Constants.POST_OR_COMPLAINT, "0")
        startActivity(intent)}}
    }

    override fun showDescError() {
        Utilities.dismissProgress()
        Utilities.showMessage(this, getString(R.string.please_select_image))
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun onPostAdded(responseObject: GetCasesResponse) {
        //nothing to do
    }

    override fun getLayout(): Int {
        return R.layout.activity_my_cases
    }

    override fun handleKeyboard(): View {
        return myCasesLayout
    }

    override fun adhaarSavedSuccess(responseObject: SignupResponse) {
        //do nothing
    }

    override fun statusUpdationSuccess(responseObject: DeleteComplaintResponse) {
        Utilities.dismissProgress()
        Utilities.showMessage(this,responseObject.message.toString())
        //refresh the list
        Utilities.showProgress(this)
        val casesRequest = CasesRequest("0", "", "0") //type = -1 for fetching all the data
        presenter.getComplaints(casesRequest, token)
    }

    override fun onListFetchedSuccess(responseObject: GetStatusResponse) {
        Utilities.dismissProgress()
        for (element in responseObject.data) {
            if (element.name.equals(currentStatus)) {
                element.isChecked = true
                break
            }
        }
        showStatusDialog("",responseObject)
    }

    private fun showStatusDialog(description: String, responseObject: GetStatusResponse) {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        val binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_change_status, null, false) as com.ngo.databinding.DialogChangeStatusBinding
        // Inflate and set the layout for the dialog
        if (!description.equals("null") && !description.equals("")) binding.etDescription.setText(description)

        //display the list on the screen
        val statusAdapter = StatusAdapter(this, responseObject.data.toMutableList(), this)
        val horizontalLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvStatus?.layoutManager = horizontalLayoutManager
        binding.rvStatus?.adapter = statusAdapter
        binding.btnDone.setOnClickListener{
            Utilities.showProgress(this@MyCasesActivity)
            //hit status update api
            presenter.updateStatus(token,complaintId,statusId,binding.etDescription.text.toString())
            dialog.dismiss()
        }

        builder.setView(binding.root)
        dialog = builder.create()
        dialog.show()
    }

}
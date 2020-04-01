package com.ngo.ui.home.fragments.cases

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngo.R
import com.ngo.adapters.CasesAdapter
import com.ngo.adapters.StatusAdapter
import com.ngo.listeners.AlertDialogListener
import com.ngo.listeners.OnCaseItemClickListener
import com.ngo.pojo.request.CasesRequest
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.pojo.response.GetStatusResponse
import com.ngo.pojo.response.SignupResponse
import com.ngo.ui.crimedetails.view.IncidentDetailActivity
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenter
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.ngo.ui.home.fragments.cases.view.CasesView
import com.ngo.utils.Constants
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.fragment_cases.*

class CasesFragment : Fragment(), CasesView, OnCaseItemClickListener, AlertDialogListener {

    override fun onClick(item: Any) {
        Utilities.showProgress(mContext)
        val complaintsData = item as GetCasesResponse.Data
        //delete the item based on id
        presenter.deleteComplaint(token, complaintsData.id!!)
    }

    private lateinit var mContext: Context
    private var presenter: CasesPresenter = CasesPresenterImplClass(this)
    private var complaints: List<GetCasesResponse.Data> = mutableListOf()
    lateinit var casesRequest: CasesRequest
    var token: String = ""
    private var statusId = "-1"
    private var complaintId = "-1"
    private var currentStatus = ""
    var type = ""

    override fun showGetComplaintsResponse(response: GetCasesResponse) {
        Utilities.dismissProgress()
        complaints = response.data!!
        if (complaints.isNotEmpty()) {
            tvRecord?.visibility = View.GONE
            rvPublic?.visibility = View.VISIBLE

            rvPublic?.adapter =
                CasesAdapter(mContext, complaints.toMutableList(), this, type.toInt(), this)
        } else {
            tvRecord?.visibility = View.VISIBLE
            rvPublic?.visibility = View.GONE
        }

        etSearch?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                casesRequest = CasesRequest(
                    "1",
                    etSearch.text.toString(),
                    "0"
                ) //all = "1" for fetching all the cases whose type = 0

                Utilities.showProgress(mContext)
                //hit api with search variable
                presenter.getComplaints(casesRequest, token,type)
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

    override fun showDescError() {
        Utilities.dismissProgress()
        Utilities.showMessage(mContext, getString(R.string.please_select_image))
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(mContext, error)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        token = PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")!!
        type = PreferenceHandler.readString(mContext, PreferenceHandler.USER_ROLE, "0")!!
    }

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
                Utilities.showProgress(mContext)
                //hit api based on role
                presenter.fetchStatusList(token, type)
            }

            else -> {
                val intent = Intent(mContext, IncidentDetailActivity::class.java)
                intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, complaintsData.id)
                intent.putExtra(Constants.POST_OR_COMPLAINT, "0")
                startActivity(intent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cases, container, false)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val horizontalLayoutManager = LinearLayoutManager(
            mContext,
            RecyclerView.VERTICAL, false
        )
        rvPublic.layoutManager = horizontalLayoutManager
    }

    override fun onPostAdded(responseObject: GetCasesResponse) {
        //nothing to do
    }

    override fun onDeleteItem(complaintsData: GetCasesResponse.Data) {
        //nothing to do
    }

    override fun onComplaintDeleted(responseObject: DeleteComplaintResponse) {
        Utilities.showMessage(mContext, responseObject.message!!)
        val casesRequest = CasesRequest("1", "", "0") //type = -1 for fetching all the data
        //  Utilities.showProgress(activity!!)
        presenter.getComplaints(casesRequest, token, type)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            casesRequest = CasesRequest(
                "1",
                /*   etSearch?.text.toString()*/"",
                "0"
            ) //all = "1" and for fetching all the cases which are of type = 0

            Utilities.showProgress(mContext)
            presenter.getComplaints(casesRequest, token, type)
        }
    }

    override fun changeLikeStatus(complaintsData: GetCasesResponse.Data) {
        Utilities.showProgress(mContext)
        val token = PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")
        //delete the item based on id
        presenter.changeLikeStatus(token!!, complaintsData.id!!)
    }

    override fun onLikeStatusChanged(responseObject: DeleteComplaintResponse) {
        Utilities.showMessage(mContext, responseObject.message!!)
        val casesRequest = CasesRequest("1", "", "0") //type = -1 for fetching all the data
        presenter.getComplaints(casesRequest, token, type)
    }

    override fun adhaarSavedSuccess(responseObject: SignupResponse) {
        //do nothing
    }

    override fun statusUpdationSuccess(responseObject: DeleteComplaintResponse) {
        Utilities.dismissProgress()
        Utilities.showMessage(mContext, responseObject.message.toString())
        //refresh the list
        Utilities.showProgress(mContext)
        val casesRequest = CasesRequest("1", "", "0")  //type = -1 for fetching both cases and posts
        presenter.getComplaints(casesRequest, token , type)
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

    override fun onStatusClick(statusId: String) {
        this.statusId = statusId
    }

    private fun showStatusDialog(description: String, responseObject: GetStatusResponse) {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(mContext)
        val binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dialog_change_status, null, false) as com.ngo.databinding.DialogChangeStatusBinding
        // Inflate and set the layout for the dialog
        if (!description.equals("null") && !description.equals("")) binding.etDescription.setText(description)

        //display the list on the screen
        val statusAdapter = StatusAdapter(mContext, responseObject.data.toMutableList(), this)
        val horizontalLayoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvStatus?.layoutManager = horizontalLayoutManager
        binding.rvStatus?.adapter = statusAdapter
        binding.btnDone.setOnClickListener{
            Utilities.showProgress(mContext)
            //hit status update api
            presenter.updateStatus(token,complaintId,statusId,binding.etDescription.text.toString())
            dialog.dismiss()
        }

        builder.setView(binding.root)
        dialog = builder.create()
        dialog.show()
    }

}

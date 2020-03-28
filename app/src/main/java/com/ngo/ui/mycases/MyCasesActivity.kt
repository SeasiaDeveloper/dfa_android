package com.ngo.ui.mycases

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngo.R
import com.ngo.adapters.CasesAdapter
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.listeners.OnCaseItemClickListener
import com.ngo.pojo.request.CasesRequest
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.ui.generalpublic.view.GeneralPublicHomeFragment
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenter
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.ngo.ui.home.fragments.cases.view.CasesView
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_my_cases.*
import kotlinx.android.synthetic.main.activity_my_cases.toolbarLayout
import kotlinx.android.synthetic.main.activity_profile.*

class MyCasesActivity : BaseActivity(), CasesView, OnCaseItemClickListener {
    private var presenter: CasesPresenter = CasesPresenterImplClass(this)
    private var complaints: List<GetCasesResponse.DataBean> = mutableListOf()
    lateinit var casesRequest: CasesRequest
    var token: String = ""

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
            rvPublic.adapter = CasesAdapter(this, complaints.toMutableList(), this, 1)
        } else {
            tvRecord.visibility = View.VISIBLE
            rvPublic.visibility = View.GONE
        }

        //add click listener after adding the list on the view
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                casesRequest = CasesRequest(
                    "1",
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
    override fun changeLikeStatus(complaintsData: GetCasesResponse.DataBean) {
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
        GeneralPublicHomeFragment.change = 1 // so that list on Home gets refreshed after change in status
    }

    //to delete my case
    override fun onDeleteItem(complaintsData: GetCasesResponse.DataBean) {
        Utilities.showProgress(this@MyCasesActivity)
        val token =
            PreferenceHandler.readString(this@MyCasesActivity, PreferenceHandler.AUTHORIZATION, "")
        //delete the item based on id
        presenter.deleteComplaint(token!!, complaintsData.id!!)
    }

    //refreshing the list after deletion
    override fun onComplaintDeleted(responseObject: DeleteComplaintResponse) {
        Utilities.showMessage(this@MyCasesActivity, responseObject.message!!)
        val casesRequest = CasesRequest("0", "", "0") //type = -1 for fetching all the data
        presenter.getComplaints(casesRequest, token)
        GeneralPublicHomeFragment.change = 1 // so that list on Home gets refreshed after change in status
    }

    //displays the detail of my case
    override fun onItemClick(complaintsData: GetCasesResponse.DataBean, type: String) {
        //pending
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
}
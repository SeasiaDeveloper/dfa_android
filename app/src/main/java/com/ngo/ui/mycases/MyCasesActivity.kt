package com.ngo.ui.mycases

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
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
import com.ngo.pojo.response.*
import com.ngo.ui.comments.CommentsActivity
import com.ngo.ui.crimedetails.view.IncidentDetailActivity
import com.ngo.ui.generalpublic.pagination.EndlessRecyclerViewScrollListenerImplementation
import com.ngo.ui.generalpublic.pagination.PaginationScrollListener
import com.ngo.ui.generalpublic.view.GeneralPublicHomeFragment
import com.ngo.ui.home.fragments.cases.CasesFragment
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenter
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.ngo.ui.home.fragments.cases.view.CasesView
import com.ngo.utils.Constants
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_my_cases.*
import kotlinx.android.synthetic.main.activity_my_cases.progressBar
import kotlinx.android.synthetic.main.activity_my_cases.rvPublic
import kotlinx.android.synthetic.main.activity_my_cases.toolbarLayout
import kotlinx.android.synthetic.main.activity_my_cases.tvRecord
import kotlinx.android.synthetic.main.fragment_public_home.*


class MyCasesActivity : BaseActivity(), CasesView, OnCaseItemClickListener, AlertDialogListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    //pagination
    var pageCount: Int = 1
    var endlessScrollListener: EndlessRecyclerViewScrollListenerImplementation? = null
    var isLike: Boolean = false
    var complaintIdTobeLiked: String? = null
    var whenDeleteCall: Boolean = false
    var deleteItemposition: Int? = null
    lateinit var horizontalLayoutManager: LinearLayoutManager
    var actionChanged: Boolean = false

    override fun onStatusClick(statusId: String) {
        this.statusId = statusId
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        if (!search) {
            pageCount = page
            val casesRequest =
                CasesRequest("0", "", "0", page.toString(), "10")//*totalItemsCount.toString()*//*)
            presenter.getComplaints(casesRequest, token, type)
            progressBar.visibility = View.VISIBLE
        }
    }

    override fun onClick(item: Any, position: Int) {
        Utilities.showProgress(this@MyCasesActivity)
        val complaintsData = item as GetCasesResponse.Data
        //delete the item based on id
        deleteItemposition = position
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
    private var adapter: CasesAdapter? = null
    private var search: Boolean = false

    companion object {
        var change = 0
        var commentChange = 0
        var fromIncidentDetailScreen = 1
        var commentsCount = 0
    }

    override fun onResume() {
        super.onResume()
        if (change == 1 || commentChange != 0) {
            pageCount = 1
            myCasesApiCall()
        }
    }

    private fun myCasesApiCall() {
        casesRequest = CasesRequest(
            "0",
            etSearch.text.toString(),
            "0", "1", "10"

        ) //all = "1" for fetching all the cases whose type = 0

        Utilities.showProgress(this@MyCasesActivity)
        //hit api with search variable
        presenter.getComplaints(casesRequest, token, type)
    }


    override fun onPause() {
        super.onPause()
        change = 1
    }


    override fun setupUI() {
        token = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")!!
        type = PreferenceHandler.readString(this, PreferenceHandler.USER_ROLE, "")!!

        (toolbarLayout as CenteredToolbar).title = getString(R.string.my_cases)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        adapter = CasesAdapter(this, complaints.toMutableList(), this, type.toInt(), this, this)
        horizontalLayoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        rvPublic?.layoutManager = horizontalLayoutManager
        rvPublic?.adapter = adapter

        if (endlessScrollListener == null)
            endlessScrollListener =
                EndlessRecyclerViewScrollListenerImplementation(horizontalLayoutManager, this)
        else
            endlessScrollListener?.setmLayoutManager(horizontalLayoutManager)
        rvPublic.addOnScrollListener(endlessScrollListener!!)
        endlessScrollListener?.resetState()

        doApiCall()

        swipeView.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                this,
                R.color.colorDarkGreen
            )
        )
        swipeView.setColorSchemeColors(Color.WHITE)

        swipeView.setOnRefreshListener {
            pageCount = 1
            adapter?.clear()
            endlessScrollListener?.resetState()
            myCasesApiCall()
            // itemsCells.clear()
            // setItemsData()
            // adapter = Items_RVAdapter(itemsCells)
            //  itemsrv.adapter = adapter
            swipeView.isRefreshing = false
        }

    }

    fun doApiCall() {
        casesRequest = CasesRequest(
            "0",
            etSearch.text.toString(),
            "0", "1", "10"
        ) //all = "0"  my cases and for fetching all the cases which are of type = 0
        Utilities.showProgress(this)
        presenter.getComplaints(casesRequest, token, type)
    }

    //displays my cases list on the view
    override fun showGetComplaintsResponse(response: GetCasesResponse) {
        Utilities.dismissProgress()
        complaints = response.data!!
        if (complaints.isNotEmpty()) {
            tvRecord.visibility = View.GONE
            rvPublic.visibility = View.VISIBLE
            swipeView.visibility = View.VISIBLE
            if (!isLike) {
                if (commentChange == 0 && !whenDeleteCall) {
                    if (pageCount == 1) {
                        adapter?.clear()
                        adapter?.setList(response.data.toMutableList()) //now
                    } else {
                        if (search /*&& pageCount==1*/) {
                            adapter?.clear()
                            adapter?.setList(response.data.toMutableList()) //now
                        } else {
                            adapter?.addDataInMyCases(
                                horizontalLayoutManager,
                                complaints.toMutableList()
                            )
                            progressBar.visibility = View.GONE
                        }
                        //adapter?.setList(response.data.toMutableList())
                    }
                } else {
                    if (whenDeleteCall) {
                        adapter?.removeAt(deleteItemposition!!)
                        whenDeleteCall = false
                    } else {
                        adapter?.notifyParticularItemWithComment(
                            commentChange.toString(),
                            response.data, commentsCount
                        )
                        commentsCount = 0
                        commentChange = 0
                    }
                }
            } else {
                //when to change like status
                adapter?.notifyParticularItem(complaintIdTobeLiked!!, response.data)
                isLike = false
            }
            //change = 1
        } else {
            if (pageCount == 1) {
                tvRecord.visibility = View.VISIBLE
                rvPublic.visibility = View.GONE
                swipeView.visibility = View.GONE
            } else {
                if (search && complaints.size == 0) {
                    tvRecord.visibility = View.VISIBLE
                    rvPublic.visibility = View.GONE
                }
                search = false
            }
            progressBar.visibility = View.GONE
        }

        //add click listener after adding the list on the view
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length >= 3 || s.length == 0) {
                    endlessScrollListener?.resetState()
                    if (s.length == 0) {
                        search = false
                        pageCount = 1
                        casesRequest = CasesRequest(
                            "0",
                            etSearch.text.toString(),
                            "0", "1", "10"
                        ) //all = "1" for fetching all the cases whose type = 0

                    } else {
                        search = true
                        casesRequest = CasesRequest(
                            "0",
                            etSearch.text.toString(),
                            "0", "1", "30"
                        ) //all = "1" for fetching all the cases whose type = 0
                    }


                    // endlessScrollListener?.resetState()
                    Utilities.showProgress(this@MyCasesActivity)
                    //hit api with search variable
                    presenter.getComplaints(casesRequest, token, type)
                }

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
        complaintIdTobeLiked = complaintsData.id
        presenter.changeLikeStatus(token!!, complaintsData.id!!)

    }

    //refreshing the list after changing the like status
    override fun onLikeStatusChanged(responseObject: DeleteComplaintResponse) {
       // Utilities.showMessage(this, responseObject.message!!)
        isLike = true
        val casesRequest = CasesRequest(
            "0",
            "",
            "0", "1", "10"
        ) //all = 0 for only my cases;type = -1 for fetching all the data
        presenter.getComplaints(casesRequest, token, type)
        GeneralPublicHomeFragment.changeThroughIncidentScreen = 1 // so that list on Home gets refreshed after change in status
    }

    //to delete my case
    override fun onDeleteItem(complaintsData: GetCasesResponse.Data) {

    }

    //refreshing the list after deletion
    override fun onComplaintDeleted(responseObject: DeleteComplaintResponse) {
        Utilities.showMessage(this@MyCasesActivity, responseObject.message!!)
        whenDeleteCall = true
        val casesRequest = CasesRequest(
            "0", "", "0", "1", "10"
        ) //type = -1 for fetching all the data
        presenter.getComplaints(casesRequest, token, type)
        GeneralPublicHomeFragment.changeThroughIncidentScreen = 1 // so that list on Home gets refreshed after change in status
    }

    //displays the detail of my case
    override fun onItemClick(
        complaintsData: GetCasesResponse.Data,
        actionType: String,
        position: Int
    ) {
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
                if (complaintsData.status != null) currentStatus = complaintsData.status!!
                Utilities.showProgress(this@MyCasesActivity)
                //hit api based on role
                presenter.fetchStatusList(token, type)
            }

            "webview" -> {
                val url = complaintsData.fir_url
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }

            "comment" -> {
                val intent = Intent(this, CommentsActivity::class.java)
                intent.putExtra("id", complaintsData.id!!)
                startActivity(intent)
            }

            else -> {
                /*val intent = Intent(this, IncidentDetailActivity::class.java)
                intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, complaintsData.id)
                intent.putExtra(Constants.POST_OR_COMPLAINT, "0")
                intent.putExtra(Constants.FROM_WHERE, "nottohit")
                startActivity(intent)*/
            }
        }
    }

    override fun showDescError() {
        Utilities.dismissProgress()
        Utilities.showMessage(this, getString(R.string.please_select_image))
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun onPostAdded(responseObject: CreatePostResponse) {
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

    override fun statusUpdationSuccess(responseObject: UpdateStatusSuccess) {
        Utilities.dismissProgress()
        Utilities.showMessage(this, responseObject.message.toString())
        //refresh the list
       /* if (responseObject.data?.size != 0) {
            adapter?.notifyActionData(responseObject.data!!)
        }*/

     /*   if(responseObject.data?.get(0)?.status!!.equals("Unauthentic") || responseObject.data.get(0).status!!.equals("Reject")){
            //refresh the list
            Utilities.showProgress(this@MyCasesActivity)
            doApiCall()
        }
        else {*/
            actionChanged = true
            if (responseObject.data!!.size != 0) {
                adapter?.notifyActionData(responseObject.data)
            }
       // }
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

    private fun showStatusDialog(description: String, responseObject: GetStatusResponse) {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_change_status,
            null,
            false
        ) as com.ngo.databinding.DialogChangeStatusBinding
        // Inflate and set the layout for the dialog
        if (!description.equals("null") && !description.equals("")) binding.etDescription.setText(
            description
        )

        //display the list on the screen
        val statusAdapter = StatusAdapter(this, responseObject.data.toMutableList(), this)
        val horizontalLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvStatus?.layoutManager = horizontalLayoutManager
        binding.rvStatus?.adapter = statusAdapter
        binding.btnDone.setOnClickListener {
            Utilities.showProgress(this@MyCasesActivity)
            //hit status update api
            presenter.updateStatus(
                token,
                complaintId,
                statusId,
                binding.etDescription.text.toString()
            )
            dialog.dismiss()
        }

        builder.setView(binding.root)
        dialog = builder.create()
        dialog.show()
    }
}
package com.ngo.ui.home.fragments.cases

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.ngo.pojo.response.*
import com.ngo.ui.comments.CommentsActivity
import com.ngo.ui.crimedetails.view.IncidentDetailActivity
import com.ngo.ui.generalpublic.pagination.EndlessRecyclerViewScrollListenerImplementation
import com.ngo.ui.generalpublic.view.GeneralPublicHomeFragment
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenter
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.ngo.ui.home.fragments.cases.view.CasesView
import com.ngo.utils.Constants
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_my_cases.*
import kotlinx.android.synthetic.main.activity_my_cases.progressBar
import kotlinx.android.synthetic.main.fragment_cases.etSearch
import kotlinx.android.synthetic.main.fragment_cases.rvPublic
import kotlinx.android.synthetic.main.fragment_cases.tvRecord
import kotlinx.android.synthetic.main.fragment_public_home.*

class CasesFragment : Fragment(), CasesView, OnCaseItemClickListener, AlertDialogListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {

    override fun onClick(item: Any, pos: Int) {
        Utilities.showProgress(mContext)
        val complaintsData = item as GetCasesResponse.Data
        //delete the item based on id
        deleteItemposition = pos
        presenter.deleteComplaint(token, complaintsData.id!!)
    }

    override fun onHide(item: Any, position: Int) {
        Utilities.showProgress(mContext)
        val complaintsData = item as GetCasesResponse.Data
        //delete the item based on id
        deleteItemposition = position
        presenter.hideComplaint(token, complaintsData.id!!)    }

    private lateinit var mContext: Context
    private var presenter: CasesPresenter = CasesPresenterImplClass(this)
    private var complaints: List<GetCasesResponse.Data> = mutableListOf()
    lateinit var casesRequest: CasesRequest
    var token: String = ""
    private var statusId = "-1"
    private var complaintId = "-1"
    private var currentStatus = ""
    var fragment:Fragment?=null
    var complaintIdTobeLiked: String? = null
    private var adapter: CasesAdapter? = null
    var deleteItemposition: Int? = null
    var type = ""
    var endlessScrollListener: EndlessRecyclerViewScrollListenerImplementation? = null
    var pageCount: Int = 1
    private var search: Boolean = false
    var isLike: Boolean = false
    var whenDeleteCall: Boolean = false
    var isFirst = true
    var horizontalLayoutManager: LinearLayoutManager? = null

    companion object {
        var change = 0
        var commentChange = 0
        var fromIncidentDetailScreen = 0
        var commentsCount = 0
    }

    override fun onResume() {
        super.onResume()
        if (isFirst) {
            doApiCall()
            isFirst = false
        } else if (!isFirst && change == 1) {
            adapter?.clear()
            endlessScrollListener?.resetState()
            doApiCall()
            change = 0
        } else {
            if (fromIncidentDetailScreen == 0) {
                if (commentChange != 0) {
                    doApiCall()
                }
            }
            fromIncidentDetailScreen == 0
        }
    }

    fun doApiCall() {
        casesRequest = CasesRequest(
            "1",
            "",
            "0", "1", "5"
        ) //all = "1" for fetching all the cases whose type = 0
        Utilities.showProgress(mContext)
        //hit api with search variable
        presenter.getComplaints(casesRequest, token, type)
    }

    override fun onPause() {
        super.onPause()
        GeneralPublicHomeFragment.change = 1
    }

    override fun showGetComplaintsResponse(response: GetCasesResponse) {
        Utilities.dismissProgress()
        complaints = response.data!!
        if (complaints.isNotEmpty()) {
            tvRecord.visibility = View.GONE
            rvPublic.visibility = View.VISIBLE
            itemsswipetorefresh.visibility = View.VISIBLE

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
                                horizontalLayoutManager!!,
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
            GeneralPublicHomeFragment.change = 1
        } else {
            if (pageCount == 1) {
                tvRecord.visibility = View.VISIBLE
                itemsswipetorefresh.visibility = View.GONE
                rvPublic.visibility = View.GONE
            } else {
                if (search && complaints.size == 0) {
                    tvRecord.visibility = View.VISIBLE
                    rvPublic.visibility = View.GONE
                    itemsswipetorefresh.visibility = View.GONE
                }
                search = false
            }
            progressBar.visibility = View.GONE
        }

        etSearch?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.length >= 3 || s.length == 0) {
                    endlessScrollListener?.resetState()
                    if (s.length == 0) {
                        search = false
                        pageCount = 1
                        casesRequest = CasesRequest(
                            "1",
                            etSearch.text.toString(),
                            "0", "1", "5"
                        ) //all = "1" for fetching all the cases whose type = 0

                    } else {
                        search = true
                        casesRequest = CasesRequest(
                            "1",
                            etSearch.text.toString(),
                            "0", "1", "30"
                        ) //all = "1" for fetching all the cases whose type = 0
                    }


                    // endlessScrollListener?.resetState()
                    Utilities.showProgress(activity!!)
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
        type = PreferenceHandler.readString(mContext, PreferenceHandler.USER_ROLE, "")!!
    }

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
                Utilities.showProgress(mContext)
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
                val intent = Intent(activity!!, CommentsActivity::class.java)
                intent.putExtra("id", complaintsData.id!!)
                startActivity(intent)
            }

            else -> {
                val intent = Intent(mContext, IncidentDetailActivity::class.java)
                intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, complaintsData.id)
                intent.putExtra(Constants.POST_OR_COMPLAINT, "0")
                intent.putExtra(Constants.FROM_WHERE, "nottohit")
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
        fragment=this
        adapter = CasesAdapter(mContext, complaints.toMutableList(), this, type.toInt(), this,activity as Activity,fragment as CasesFragment,false)
        horizontalLayoutManager = LinearLayoutManager(
            mContext,
            RecyclerView.VERTICAL, false
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

        itemsswipetorefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                activity!!,
                R.color.colorDarkGreen
            )
        )
        itemsswipetorefresh.setColorSchemeColors(Color.WHITE)

        itemsswipetorefresh.setOnRefreshListener {
            pageCount = 1
            adapter?.clear()
            endlessScrollListener?.resetState()
            doApiCall()
            // itemsCells.clear()
            // setItemsData()
            // adapter = Items_RVAdapter(itemsCells)
            //  itemsrv.adapter = adapter
            itemsswipetorefresh.isRefreshing = false
        }
        /*   rvPublic.layoutManager = horizontalLayoutManager
           adapter = CasesAdapter(mContext, complaints.toMutableList(), this, type.toInt(), this)*/
    }

    override fun onPostAdded(responseObject: CreatePostResponse) {
        //nothing to do
    }

    override fun onDeleteItem(complaintsData: GetCasesResponse.Data) {
        //nothing to do
    }

    override fun onComplaintDeleted(responseObject: DeleteComplaintResponse) {
        Utilities.showMessage(mContext, responseObject.message!!)
        whenDeleteCall = true
        val casesRequest =
            CasesRequest("1", "", "0", "1", "5") //type = -1 for fetching all the data
        //  Utilities.showProgress(activity!!)
        presenter.getComplaints(casesRequest, token, type)
        change = 1
    }

    /*override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            adapter?.clear()
            endlessScrollListener?.resetState()
            casesRequest = CasesRequest(
                "1",
                "",
                "0", "1", "10"
            ) //all = "1" and for fetching all the cases which are of type = 0

            Utilities.showProgress(mContext)
            presenter.getComplaints(casesRequest, token, type)
        }
    }*/

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        if (!search) {
            pageCount = page
            val casesRequest =
                CasesRequest("1", "", "0", page.toString(), "5")//*totalItemsCount.toString()*//*)
            presenter.getComplaints(casesRequest, token, type)
            progressBar.visibility = View.VISIBLE
        }
    }

    override fun changeLikeStatus(complaintsData: GetCasesResponse.Data) {
        Utilities.showProgress(mContext)
        val token = PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")
        //delete the item based on id
        complaintIdTobeLiked = complaintsData.id
        presenter.changeLikeStatus(token!!, complaintsData.id!!)
        change = 1
        GeneralPublicHomeFragment.change = 1
    }

    override fun onLikeStatusChanged(responseObject: DeleteComplaintResponse) {
        // Utilities.showMessage(mContext, responseObject.message!!)
        isLike = true
        val casesRequest =
            CasesRequest("1", "", "0", "1", "5") //type = -1 for fetching all the data
        presenter.getComplaints(casesRequest, token, type)
        change = 1
        GeneralPublicHomeFragment.change = 1
    }

    override fun adhaarSavedSuccess(responseObject: SignupResponse) {
        //do nothing
    }

    override fun statusUpdationSuccess(responseObject: UpdateStatusSuccess) {
        Utilities.dismissProgress()
        Utilities.showMessage(mContext, responseObject.message.toString())
        //refresh the list
        //actionChanged = true
        if (responseObject.data?.size != 0) {
            adapter?.notifyActionData(responseObject.data!!)
        }
    }

    override fun getFirImageData(response: FirImageResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        val builder = AlertDialog.Builder(mContext)
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.dialog_change_status,
            null,
            false
        ) as com.ngo.databinding.DialogChangeStatusBinding
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
        val statusAdapter = StatusAdapter(mContext, responseObject.data.toMutableList(), this)
        val horizontalLayoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvStatus?.layoutManager = horizontalLayoutManager
        binding.rvStatus?.adapter = statusAdapter
        binding.btnDone.setOnClickListener {
            Utilities.showProgress(mContext)
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

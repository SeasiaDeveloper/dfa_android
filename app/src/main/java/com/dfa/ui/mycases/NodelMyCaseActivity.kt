package com.dfa.ui.mycases

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dfa.R
import com.dfa.adapters.NodelCaseAdapter
import com.dfa.adapters.StatusAdapter
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.listeners.AlertDialogListener
import com.dfa.listeners.OnCaseItemClickListener
import com.dfa.pojo.request.CasesRequest
import com.dfa.pojo.request.CrimeDetailsRequest
import com.dfa.pojo.response.*
import com.dfa.ui.comments.CommentsActivity
import com.dfa.ui.generalpublic.PoliceOfficerActivity
import com.dfa.ui.generalpublic.PoliceStationActivity
import com.dfa.ui.generalpublic.pagination.EndlessRecyclerViewScrollListenerImplementation
import com.dfa.ui.generalpublic.view.GeneralPublicHomeFragment
import com.dfa.ui.home.fragments.cases.presenter.CasesPresenter
import com.dfa.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.dfa.ui.home.fragments.cases.view.CasesView
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.activity_my_cases.click_search_icon
import kotlinx.android.synthetic.main.activity_my_cases.etSearch
import kotlinx.android.synthetic.main.activity_my_cases.progressBar
import kotlinx.android.synthetic.main.activity_my_cases.rvPublic
import kotlinx.android.synthetic.main.activity_my_cases.swipeView
import kotlinx.android.synthetic.main.activity_my_cases.toolbarLayout
import kotlinx.android.synthetic.main.activity_my_cases.tvRecord
import kotlinx.android.synthetic.main.activity_nodel_my_case.*

class NodelMyCaseActivity : BaseActivity(), CasesView, OnCaseItemClickListener, AlertDialogListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    //pagination
    var pageCount: Int = 1
    var endlessScrollListener: EndlessRecyclerViewScrollListenerImplementation? = null
    var isLike: Boolean = false
    var complaintIdTobeLiked: String? = null
    var deleteItemposition: Int? = null
    lateinit var horizontalLayoutManager: LinearLayoutManager
    var actionChanged: Boolean = false
    lateinit var fragment: Fragment
    var positionOfFir: Int? = null
    private var complaintIdTobeLikedPosition=-1
    private var limit="20"

    private var presenter: CasesPresenter = CasesPresenterImplClass(this)
    private var complaints= ArrayList<GetCasesResponse.Data>()
    lateinit var casesRequest: CasesRequest
    var token: String = ""
    private var statusId = "-1"
    private var complaintId = "-1"
    private var currentStatus = ""
    private var filterValse = "complaint_id"
    var type = ""
    private var search: Boolean = false
    private var textChanged: Boolean = false
    private var isFirst1: Boolean = true
    var firComplaintId: String = ""
    var adapter: NodelCaseAdapter? = null
    var inflater:LayoutInflater?=null
    var pw:PopupWindow?=null
    var radioGroup:RadioGroup?=null



    override fun onStatusClick(statusId: String) {
        this.statusId = statusId
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        if (!search) {
            pageCount = page
            val casesRequest =
                CasesRequest("0", "", "0", page.toString(), limit,filterValse)//*totalItemsCount.toString()*//*)
            presenter.getComplaints(casesRequest, token, type)
            progressBar.visibility = View.VISIBLE
        }
    }

    override fun onClick(item: Any, position: Int) {
        Utilities.showProgress(this@NodelMyCaseActivity)
        val complaintsData = item as GetCasesResponse.Data
        //delete the item based on id
        deleteItemposition = position
        presenter.deleteComplaint(token, complaintsData.id!!)
    }

    override fun onHide(item: Any, position: Int) {
        Utilities.showProgress(this@NodelMyCaseActivity)
        val complaintsData = item as GetCasesResponse.Data
        //delete the item based on id
        deleteItemposition = position
        presenter.hideComplaint(token, complaintsData.id!!)
    }



    companion object {
        var change = 0
        var isFirst1 = true
        var commentChange = 0
        var fromIncidentDetailScreen = 1
        var commentsCount = 0
        var firstSavedList= ArrayList<GetCasesResponse.Data>()

        val PERMISSION_READ_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        val REQUEST_PERMISSIONS = 1
    }

    override fun onResume() {
        super.onResume()
        if (isFirst1) {
            if (change == 1 || commentChange != 0) {
                pageCount = 1
                myCasesApiCall()
                isFirst1=false
            }
        }
    }

    private fun myCasesApiCall() {

        if (isInternetAvailable()) {
            casesRequest = CasesRequest(
                "0",
                etSearch.text.toString(),
                "0", "1", "10"

                ,filterValse  ) //all = "1" for fetching all the cases whose type = 0

            Utilities.showProgress(this@NodelMyCaseActivity)
            //hit api with search variable
            presenter.getComplaints(casesRequest, token, type)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }
    }

    override fun onPause() {
        super.onPause()
        change = 1
    }


    override fun setupUI() {
        token = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")!!
        type = PreferenceHandler.readString(this, PreferenceHandler.USER_ROLE, "0")!!

        (toolbarLayout as CenteredToolbar).title = getString(R.string.my_cases)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        fragment = Fragment()
        setAdapter()
        if (endlessScrollListener == null)
            endlessScrollListener =
                EndlessRecyclerViewScrollListenerImplementation(
                    horizontalLayoutManager,
                    this
                )
        else
            endlessScrollListener?.setmLayoutManager(horizontalLayoutManager)
        rvPublic.addOnScrollListener(endlessScrollListener!!)
        endlessScrollListener?.resetState()

        if (isFirst1) {
            doApiCall()
            isFirst1 = false
        }


        swipeView.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                this,
                R.color.colorDarkGreen
            )
        )
        swipeView.setColorSchemeColors(Color.WHITE)
        swipeView.setDistanceToTriggerSync(500)
        swipeView.setOnRefreshListener {
            pageCount = 1
            // adapter?.clear()
            endlessScrollListener?.resetState()
            myCasesApiCall()
            // itemsCells.clear()
            // setItemsData()
            // adapter = Items_RVAdapter(itemsCells)
            //  itemsrv.adapter = adapter
            swipeView.isRefreshing = false
        }

         inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var views=inflater!!.inflate(R.layout.filter_dialog, null, false)
        pw =  PopupWindow(views, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        pw!!.setOutsideTouchable(true);


         radioGroup=views.findViewById<RadioGroup>(R.id.groupradio)
    }


    fun setAdapter() {
        adapter = NodelCaseAdapter(
            this,
            complaints,
            this,
            type.toInt(),
            this,
            this@NodelMyCaseActivity,
            false
        )
        horizontalLayoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        rvPublic?.layoutManager = horizontalLayoutManager
        rvPublic?.adapter = adapter

    }

    //call fir image api
    fun callFirImageApi(complaintId: String, position: Int) {
        Utilities.showProgress(this)
        firComplaintId = complaintId
        positionOfFir = position
        var firImageRequest = CrimeDetailsRequest(complaintId)
        presenter.callFirIamageApi(token, firImageRequest)
    }

    fun doApiCall() {
        if (isInternetAvailable()) {
            casesRequest = CasesRequest(
                "0",
                etSearch.text.toString(),
                "0", "1", limit
                ,filterValse ) //all = "0"  my cases and for fetching all the cases which are of type = 0
            Utilities.showProgress(this)
            presenter.getComplaints(casesRequest, token, type)
        }
        else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }
    }

    //displays my cases list on the view
    override fun showGetComplaintsResponse(response: GetCasesResponse) {
        Utilities.dismissProgress()
        if (isFirst1) {
            firstSavedList = response.data!!
            isFirst1 = false
        }

        if (response.data!!.isNotEmpty())

        {
            if (pageCount == 1) {
                adapter?.clear()
                complaints = response.data!!
                adapter?.setList(complaints)
                adapter?.notifyDataSetChanged()//now

            } else {
                complaints.addAll(response.data!!)
                adapter?.notifyDataSetChanged()

            }

            tvRecord.visibility = View.GONE
            rvPublic.visibility = View.VISIBLE
            swipeView.visibility = View.VISIBLE

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

        click_search_icon.setOnClickListener {
            // your action here
            endlessScrollListener?.resetState()
            //adapter!!.clearAdapter()
            if (etSearch.text.toString().length != 0) {
                search = true
                //adapter?.performSearch(etSearch.text.toString())
                casesRequest = CasesRequest(
                    "0",
                    etSearch.text.toString(),
                    "0", "1", limit
                    ,filterValse) //all = "1" for fetching all the cases whose type = 0

                Utilities.showProgress(this@NodelMyCaseActivity)
                //hit api with search variable
                presenter.getComplaints(casesRequest, token, type)
            }
        }

        etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                endlessScrollListener?.resetState()
                //adapter!!.clearAdapter()
                if (etSearch.text.toString().length != 0) {
                    search = true
                    textChanged = true
                    //adapter?.performSearch(etSearch.text.toString())
                    casesRequest = CasesRequest(
                        "0",
                        etSearch.text.toString(),
                        "0", "1", limit
                        ,filterValse) //all = "1" for fetching all the cases whose type = 0

                    Utilities.showProgress(this@NodelMyCaseActivity)
                    //hit api with search variable
                    presenter.getComplaints(casesRequest, token, type)
                }
                return true
            }
        })


        ivFilter.setOnClickListener {
            openFilterDialog(it)
        }



        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (/*s.length >= 3 ||*/ s.length == 0) {
                    search = true
                    if(textChanged) {
                        textChanged = false
                        //adapter?.performSearch(etSearch.text.toString())
                        casesRequest = CasesRequest(
                            "0",
                            "",
                            "0", "1", limit
                            , filterValse
                        ) //all = "1" for fetching all the cases whose type = 0

                        Utilities.showProgress(this@NodelMyCaseActivity)
                        //hit api with search variable
                        presenter.getComplaints(casesRequest, token, type)
                    }

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
                if (s.length == 0) {
                    if (s.length == 0) {
                        search = false
                        //isFirst=true

//                        if (firstSavedList.size > 0) {
//                            tvRecord.visibility = View.GONE
//                            rvPublic.visibility = View.VISIBLE
//                            swipeView.visibility = View.VISIBLE
//                        } else {
//                            tvRecord.visibility = View.VISIBLE
//                            rvPublic.visibility = View.GONE
//                            swipeView.visibility = View.GONE
//                        }
//                        adapter?.setList(firstSavedList)

                    }
                }
            }
        })
    }


    fun openFilterDialog(view:View?) {


        radioGroup!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btnComplaint -> {
                    filterValse="complaint_id"

                }
                R.id.btnPoliceStation -> {
                    filterValse="police_station"
                }

                R.id.btnDistrict -> {
                    filterValse="district"
                }
            }
        })

        pw!!.showAsDropDown(view)

      //  pw.showAtLocation(findViewById(R.id.main), Gravity.CENTER, 0, 0)
       /* val dialog = Dialog(this)
        dialog.setContentView(R.layout.filter_dialog)
        var radioGroup=dialog.findViewById<RadioGroup>(R.id.groupradio)
        radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
          override  fun onCheckedChanged(
                group: RadioGroup?,
                checkedId: Int
            ) {
              val selectedId = radioGroup.checkedRadioButtonId

              val radioButton = findViewById<View>(selectedId) as RadioButton

              dialog.hide()
            }
        })


        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btnNone -> {
                    Toast.makeText(this, "btnNone", Toast.LENGTH_SHORT).show()

                }
                R.id.btnComplaint -> {
                    Toast.makeText(baseContext,"btnComplaint", Toast.LENGTH_SHORT).show()
                }
                R.id.btnPoliceStation -> {
                    Toast.makeText(baseContext, "btnPoliceStation", Toast.LENGTH_SHORT).show()

                }

                R.id.btnDistrict -> {
                    Toast.makeText(baseContext, "btnDistrict", Toast.LENGTH_SHORT).show()

                }
            }
        })


        dialog.show()*/
    }


    //changing the like status
    override fun changeLikeStatus(complaintsData: GetCasesResponse.Data,position: Int) {
        // Utilities.showProgress(mContext)
        complaintIdTobeLiked = complaintsData.id
        complaintIdTobeLikedPosition=position

        //when to change like status
        /*adapter?.notifyParticularItem(complaintIdTobeLiked!!)
        isLike = false*/

        val token =
            PreferenceHandler.readString(
                this@NodelMyCaseActivity,
                PreferenceHandler.AUTHORIZATION,
                ""
            )

        //change the staus of the item based on id
        presenter.changeLikeStatus(token!!, complaintsData.id!!)

    }

    //refreshing the list after changing the like status
    override fun onLikeStatusChanged(responseObject: DeleteComplaintResponse) {
        // Utilities.showMessage(this, responseObject.message!!)
        isLike = true
        var like=
            Integer.parseInt(complaints.get(complaintIdTobeLikedPosition).like_count.toString())


        if(complaints.get(complaintIdTobeLikedPosition).is_liked==0) {
            complaints.get(complaintIdTobeLikedPosition).like_count = (like + 1).toString()
            complaints.get(complaintIdTobeLikedPosition).is_liked = 1

        }

        else
        {
            complaints.get(complaintIdTobeLikedPosition).like_count =  (like - 1).toString()
            complaints.get(complaintIdTobeLikedPosition).is_liked = 0
        }

        adapter?.notifyDataSetChanged()

//        val casesRequest = CasesRequest(
//            "0",
//            "",
//            "0", "1", "10"
//        ) //all = 0 for only my cases;type = -1 for fetching all the data
//        presenter.getComplaints(casesRequest, token, type)
//        GeneralPublicHomeFragment.changeThroughIncidentScreen =
//            1 // so that list on Home gets refreshed after change in status
    }

    //to delete my case
    override fun onDeleteItem(complaintsData: GetCasesResponse.Data) {

    }

    //refreshing the list after deletion
    override fun onComplaintDeleted(responseObject: DeleteComplaintResponse) {
        Utilities.dismissProgress()
        deleteItemposition?.let { complaints.removeAt(it) }
        adapter?.notifyDataSetChanged()
        //adapter?.removeAt(deleteItemposition!!)
        /*  whenDeleteCall = true
          val casesRequest = CasesRequest(
              "0", "", "0", "1", "5"
          ) //type = -1 for fetching all the data
          presenter.getComplaints(casesRequest, token, type)*/
        GeneralPublicHomeFragment.changeThroughIncidentScreen =
            1 // so that list on Home gets refreshed after change in status
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
                if (complaintsData.status != null)
                    currentStatus = complaintsData.status!!
                //Utilities.showProgress(this@NodelMyCaseActivity)
                //hit api based on role
                //presenter.fetchStatusList(token, type)
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
        statusId = "-1"
        Utilities.showMessage(this, error)
    }

    override fun onPostAdded(responseObject: CreatePostResponse) {
        //nothing to do
    }

    override fun getLayout(): Int {
        return R.layout.activity_nodel_my_case
    }

    override fun handleKeyboard(): View {
        return parentNodal
    }

    override fun adhaarSavedSuccess(responseObject: SignupResponse) {
        //do nothing
    }

    override fun statusUpdationSuccess(responseObject: UpdateStatusSuccess) {
        Utilities.dismissProgress()
        statusId = "-1"
        Utilities.showMessage(this, responseObject.message.toString())
        //refresh the list
        /* if (responseObject.data?.size != 0) {
             adapter?.notifyActionData(responseObject.data!!)
         }*/

        /*   if(responseObject.data?.get(0)?.status!!.equals("Unauthentic") || responseObject.data.get(0).status!!.equals("Reject")){
               //refresh the list
               Utilities.showProgress(this@NodelMyCaseActivity)
               doApiCall()
           }
           else {*/
        actionChanged = true
        if (responseObject.data!!.size != 0) {
            adapter?.notifyActionData(responseObject.data)
        }
        //  showProgress()
        //  myCasesApiCall()

        // }
    }

    override fun getFirImageData(response: FirImageResponse) {
        Utilities.dismissProgress()
        if (positionOfFir != null) {
            adapter?.notifyFirImageData(positionOfFir, response, firComplaintId)
        }
    }

    override fun advertisementSuccess(responseObject: AdvertisementResponse) {
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
                    this@NodelMyCaseActivity,
                    getString(R.string.select_option_validation)
                )
            }
            else if (statusId.equals("9")) {
                var intent=Intent(this, PoliceOfficerActivity::class.java)
                intent.putExtra("token",token)
                intent.putExtra("complaintId",complaintId)
                startActivity(intent)
                dialog.dismiss()

            } else if (statusId.equals("10")) {
                var intent=Intent(this, PoliceStationActivity::class.java)
                intent.putExtra("token",token)
                intent.putExtra("complaintId",complaintId)
                startActivity(intent)
                dialog.dismiss()

            }

            else {
                Utilities.showProgress(this@NodelMyCaseActivity)
                //hit status update api
                presenter.updateStatus(
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

}
package com.dfa.ui.generalpublic.view

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dfa.R
import com.dfa.adapters.CasesAdapter
import com.dfa.adapters.SlidingImage_Adapter
import com.dfa.adapters.StatusAdapter
import com.dfa.adapters.ZoomOutPageTransformer
import com.dfa.databinding.FragmentPublicHomeBinding
import com.dfa.listeners.AdharNoListener
import com.dfa.listeners.AlertDialogListener
import com.dfa.listeners.OnCaseItemClickListener
import com.dfa.pojo.request.CasesRequest
import com.dfa.pojo.request.CreatePostRequest
import com.dfa.pojo.request.CrimeDetailsRequest
import com.dfa.pojo.response.*
import com.dfa.ui.comments.CommentsActivity
import com.dfa.ui.generalpublic.GeneralPublicActivity
import com.dfa.ui.generalpublic.PoliceOfficerActivity
import com.dfa.ui.generalpublic.PoliceStationActivity
import com.dfa.ui.generalpublic.pagination.EndlessRecyclerViewScrollListenerImplementation
import com.dfa.ui.home.fragments.cases.presenter.CasesPresenter
import com.dfa.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.dfa.ui.home.fragments.cases.view.CasesView
import com.dfa.ui.home.fragments.home.view.HomeActivity
import com.dfa.ui.login.view.LoginActivity
import com.dfa.ui.mycases.MyCasesActivity
import com.dfa.utils.CompressImageUtilities
import com.dfa.utils.Constants
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_public_home.*
import java.lang.Integer.parseInt

class GeneralPublicHomeFragment : Fragment(), CasesView, View.OnClickListener,
    OnCaseItemClickListener, AlertDialogListener,
    AdharNoListener, EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    private var isResumeRun: Boolean = false
    lateinit var binding: FragmentPublicHomeBinding
    lateinit var mContext: Context
    private var IMAGE_REQ_CODE = 101
    private var path: String = ""
    private var authorizationToken: String? = ""
    private var media_type: String? = ""
    private var token: String = ""
    var isFirst = true
    var guestUser = ""
    var type = ""
    var hitType = "foreground"
    var firComplaintId: String = ""
    var horizontalLayoutManager: LinearLayoutManager? = null

    //pagination
    var endlessScrollListener: EndlessRecyclerViewScrollListenerImplementation? = null
    var pageCount: Int = 1
    var deleteItemposition: Int? = null
    var isLike: Boolean = false
    var whenDeleteCall: Boolean = false
    var setAdapterBoolean: Boolean = true
    var adapterActionPosition: Int? = null
    var fragment: Fragment? = null
    var positionOfFir: Int? = null
    private var limit = "10"
    private var complaintIdTobeLikedPosition = -1
//    var newCompaintsButton: ExtendedFloatingActionButton? = null

    var pages = 5
    var limit1 = 5
    var responseObjectList: ArrayList<AdvertisementResponse.Data>? = null

    private var complaints = ArrayList<GetCasesResponse.Data>()
    private var adapter: CasesAdapter? = null
    private var presenter: CasesPresenter = CasesPresenterImplClass(this)
    private var statusId = "-1"
    private var complaintId = "-1"
    private var currentStatus = ""
    var complaintIdTobeLiked: String? = null
    var actionChanged: Boolean = false
    override fun onClick(item: Any, position: Int) {
        Utilities.showProgress(mContext)
        deleteItemposition = position
        val complaintsData = item as GetCasesResponse.Data
        //delete the item based on id
        presenter.deleteComplaint(token, complaintsData.id!!)
    }

    override fun onHide(item: Any, position: Int) {
        Utilities.showProgress(mContext)
        deleteItemposition = position
        val complaintsData = item as GetCasesResponse.Data
        //delete the item based on id
        presenter.hideComplaint(token, complaintsData.id!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_public_home, container, false)
        responseObjectList = ArrayList()

       // val mPager: ViewPager = findViewById(R.id.pager)
        binding.addPager.setPageTransformer(true, ZoomOutPageTransformer())


     //   setAdvertisementAdapter()
        callPresenter("1000")
        pageCount=1
        setupUI()

            binding.itemsswipetorefresh.setOnRefreshListener(OnRefreshListener { // Your code to refresh the list here.
                if (Utilities.isInternetAvailableDialog(mContext)) {
                    hitType = "foreground"
                    val casesRequest =
                        CasesRequest(
                            "1",
                            "",
                            "-1",
                            "1",
                            limit
                            , ""
                        )  //type = -1 for fetching both cases and posts
                    presenter.getComplaints(casesRequest, token, type)
                }
        })




        return binding.root
    }


    fun callPresenter(perPage: String) {
        var input = AdvertisementInput()
        input.per_page = perPage
        input.page = "1"
        //   Utilities.showProgress(this.activity!!)
        presenter.advertisementInput(input)
    }

    //call fir image api
    fun callFirImageApi(complaintId: String, position: Int) {
        Utilities.showProgress(mContext)
        firComplaintId = complaintId
        positionOfFir = position
        var firImageRequest = CrimeDetailsRequest(complaintId)
        presenter.callFirIamageApi(token, firImageRequest)
    }

    override fun getFirImageData(response: FirImageResponse) {
        Utilities.dismissProgress()
        if (positionOfFir != null) {
            adapter?.notifyFirImageData(positionOfFir, response, firComplaintId)
        }
    }

    override fun advertisementSuccess(responseObject: AdvertisementResponse) {
        responseObjectList = responseObject.data

        if (responseObjectList!!.size > 0) {
            binding.addPager.visibility=View.VISIBLE

            if(activity!=null){
                binding.addPager!!.adapter =
                    SlidingImage_Adapter(activity!!, responseObjectList)
                binding.indicator.setViewPager( binding.addPager!!);

                if(responseObjectList!!.size!=1){
                    binding.indicator.visibility=View.VISIBLE
                }
            }


        } else {
            binding.addPager.visibility=View.GONE
            binding.indicator.visibility=View.GONE
        }
        if(responseObject.due.equals("0 INR")){
         //   dueIncomePopup()
        }
    }



    override fun showDescError() {
        Utilities.dismissProgress()
        Utilities.showMessage(mContext, getString(R.string.please_select_image))
    }

    companion object {
        var change = 0
        var changeThroughIncidentScreen = 0
        var commentChange = 0
        var fromIncidentDetailScreen = 0
        var commentsCount = 0
        var isApiHit: Boolean = false
    }

    fun refreshList() {
        pageCount = 1
        val casesRequest =
            CasesRequest("1", "", "-1", "1", limit, "") //type = -1 for fetching all the data
        Utilities.showProgress(mContext)
        presenter.getComplaints(casesRequest, token, type)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    fun setAdapter() {
        adapter = CasesAdapter(
            mContext,
            complaints,
            this,
            type.toInt(),
            this,
            activity as Activity,
            fragment as GeneralPublicHomeFragment,
            true
        )
        horizontalLayoutManager = LinearLayoutManager(
            mContext,
            RecyclerView.VERTICAL, false
        )
        binding.rvPublic.setNestedScrollingEnabled(false);
        binding.rvPublic?.layoutManager = horizontalLayoutManager
        binding.rvPublic?.adapter = adapter
        binding.rvPublic.setNestedScrollingEnabled(false);

        binding.mScroll.getViewTreeObserver()
            .addOnScrollChangedListener(object : ViewTreeObserver.OnScrollChangedListener {
                override fun onScrollChanged() {
                    try {
                        val view =
                            binding.mScroll.getChildAt(binding.mScroll.getChildCount() - 1) as View
                        val diff: Int = view.bottom - (binding.mScroll.getHeight() + binding.mScroll
                            .getScrollY())
                        if (diff == 0) {
                            pageCount = pageCount + 1
                            val casesRequest =
                                CasesRequest(
                                    "1",
                                    "",
                                    "-1",
                                    pageCount.toString(),
                                    limit /*totalItemsCount.toString()*/,
                                    ""
                                )
                          //  Utilities.showProgress(activity!!)
                            presenter.getComplaints(casesRequest, token, type)
                            progressBar.visibility = View.VISIBLE

                        }
                    } catch (e: java.lang.Exception) {
                    }
                }

            })

    }

    fun setupUI() {
//        (toolbarLayout as CenteredToolbar).title = getString(R.string.public_dashboard)
//        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        //itemsswipetorefresh.visibility = View.VISIBLE
       // norecordrefresh.visibility = View.GONE

        // newCompaintsButton = binding.extFab;
        //swipeRefresh.setOnRefreshListener(this)
        fragment = this
        setAdapter()
//        if (endlessScrollListener == null)
//            endlessScrollListener =
//                EndlessRecyclerViewScrollListenerImplementation(
//                    horizontalLayoutManager,
//                    this
//                )
//        else
//            endlessScrollListener?.setmLayoutManager(horizontalLayoutManager)
//        binding.rvPublic.addOnScrollListener(endlessScrollListener!!)
//        endlessScrollListener?.resetState()

        setProfilePic()
        binding.imgAdd.setOnClickListener(this)
        // Utilities.showProgress(mContext)

        /*  if (isFirst) {*/
        // doApiCall()F
        /*  isFirst = false
      }*/

        //doApiCall()

        binding.dashboardParentLayout.setOnClickListener {
            if (layoutPost.visibility == View.VISIBLE) {
                imgPost.visibility = View.GONE
                path = ""
                layoutAddPost.visibility = View.VISIBLE
                layoutPost.visibility = View.GONE

                edtPostInfo.setText("")
                imgPost.setImageResource(0)
                try {
                    Glide.with(this)
                        .load("")
                        .apply(
                            RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.camera_placeholder)
                        )
                        .into(imgPost)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        binding.txtAddPost.setOnClickListener {
            //show the addPost layout
            layoutAddPost.visibility = View.GONE
            layoutPost.visibility = View.VISIBLE
        }

        binding.btnPost.setOnClickListener {
            if (edtPostInfo.text.toString().trim().equals("")) {
                Utilities.showMessage(activity!!, "Please enter post title")
            } /*else if (path == null || path.equals("")) {
                Utilities.showMessage(activity!!, "Please select image or video")
            }*/ else {
                Utilities.showProgress(mContext)
                val pathArray = arrayOf(path)
                //hit api to add post and display post layout
                val request =
                    CreatePostRequest(edtPostInfo.text.toString().trim(), pathArray, media_type!!)
                authorizationToken =
                    PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")
                presenter.createPost(request, authorizationToken)
                layoutAddPost.visibility = View.VISIBLE
                layoutPost.visibility = View.GONE
            }
        }

        binding.btnCancel.setOnClickListener {
            //show the addPost layout
            imgPost.visibility = View.GONE
            path = ""
            layoutAddPost.visibility = View.VISIBLE
            layoutPost.visibility = View.GONE
            edtPostInfo.setText("")
            imgPost.setImageResource(0)
            try {
                Glide.with(this)
                    .load("")
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.camera_placeholder)
                    )
                    .into(imgPost)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.imgAttach.setOnClickListener {
            //open gallery
            val resultGallery = getMarshmallowPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Utilities.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
            if (resultGallery)
                galleryIntent()
        }


        try {
            pullToRefreshSettings(itemsswipetorefresh)
            pullToRefreshSettings(norecordrefresh)
        }catch (e:Exception){

        }



        /*  */
        binding.rvPublic.setOnClickListener {
            layoutAddPost.visibility = View.VISIBLE
            layoutPost.visibility = View.GONE
        }

    }


    private fun pullToRefreshSettings(itemsswipetorefresh: SwipeRefreshLayout) {
        itemsswipetorefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                activity!!,
                R.color.colorDarkGreen
            )
        )


        itemsswipetorefresh.setColorSchemeColors(Color.WHITE)
        itemsswipetorefresh.setDistanceToTriggerSync(300)

        itemsswipetorefresh.setOnRefreshListener {
            pageCount = 1
            adapter?.clear()
            endlessScrollListener?.resetState()
            doApiCall()
            //var k=1/0

            // itemsCells.clear()
            // setItemsData()
            // adapter = Items_RVAdapter(itemsCells)
            //  itemsrv.adapter = adapter
            itemsswipetorefresh.isRefreshing = false
        }
    }

    private fun galleryIntent() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*" //"image/* video/*"
        startActivityForResult(pickIntent, IMAGE_REQ_CODE)
    }

    private fun getMarshmallowPermission(permissionRequest: String, requestCode: Int): Boolean {
        return Utilities.checkPermission(
            mContext,
            permissionRequest,
            requestCode
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQ_CODE && resultCode == Activity.RESULT_OK && null != data) {
            imgPost.visibility = View.VISIBLE
            media_type = "photos"
            val selectedMedia: Uri = data.getData()!!
            val cr = mContext.contentResolver
            val mime = cr.getType(selectedMedia)
            if (mime?.toLowerCase()?.contains("image")!!) {
                val selectedImage = data.data
                val filePathColumn =
                    arrayOf(MediaStore.Images.Media.DATA)
                val cursor = activity!!.contentResolver
                    .query(selectedImage!!, filePathColumn, null, null, null)
                cursor?.moveToFirst()
                val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
                val picturePath = cursor?.getString(columnIndex!!)
                cursor?.close()
                try {
                    Glide.with(this).load(picturePath).into(imgPost)
                    //path = picturePath!!
                    //compression
                    val compClass = CompressImageUtilities()
                    val newPathString = compClass.compressImage(mContext, picturePath!!)
                    path = newPathString
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                change = 0
            } else if (mime.toLowerCase().contains("video")) {
                media_type = "videos"

                //below is commented for this milestone
                /* if (data.data != null) {
                     val realpath = RealPathUtil.getRealPath(activity!!, data.data!!)
                     val thumbnail = RealPathUtil.getThumbnailFromVideo(realpath!!)
                     try {
                         Glide.with(mContext)
                             .load(thumbnail)
                             //.apply(options)
                             .into(imgPost)
                     } catch (e: Exception) {
                         e.printStackTrace()
                     }
                     path = RealPathUtil.getRealPath(activity!!, data.data!!).toString()

                 }
                 change = 0*/
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Utilities.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent()
            }
        }
    }

    fun setProfilePic() {
        val value = PreferenceHandler.readString(mContext, PreferenceHandler.PROFILE_JSON, "")
        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
        if (jsondata != null) {
            if (jsondata.data?.profile_pic != null) {
                if (activity != null) {
                    val options1 = RequestOptions()
                        /* .centerCrop()*/
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)
                    try {
                        imgProfile.setImageResource(0)
                        Glide.with(activity!!).load(jsondata.data.profile_pic).apply(options1)
                            .into(imgProfile)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun showGetComplaintsResponse(response: GetCasesResponse) {
        Utilities.dismissProgress()
        binding.itemsswipetorefresh.isRefreshing=false

        try {



        if (hitType == "foreground") {
            // newCompaintsButton!!.visibility = View.GONE

               progressBar.visibility = View.GONE


            if (response.data!!.isNotEmpty()) {
                if (pageCount == 1) {
                    adapter?.clear()
                    complaints = response.data
                    adapter?.setList(complaints)
                    adapter?.notifyDataSetChanged()//now

                } else {
                    complaints.addAll(response.data)
                    adapter?.notifyDataSetChanged()

                }
                if (norecordrefresh != null) {
                    norecordrefresh.visibility = View.GONE

                }
                if (itemsswipetorefresh != null) {
                    itemsswipetorefresh.visibility = View.VISIBLE

                }

            } else {
                if (pageCount == 1) {
                    norecordrefresh.visibility = View.VISIBLE
                    itemsswipetorefresh.visibility = View.GONE
                    complaints = response.data
                    // }
//                } else {
////                    if (complaints.size == 0) {
////                        tvRecord.visibility = View.VISIBLE
////                        rvPublic.visibility = View.GONE
////                    }
//                }
                }
                progressBar.visibility = View.GONE
            }

            setProfilePic()
        } else {
            var new = 0;
            for (element in response.data!!) {

                val datat = complaints.filter { it.id.toString().contains(element.id.toString()) }
                if (datat.size == 0) new = 1

            }


            if (new == 1) {
                //newCompaintsButton!!.visibility = View.VISIBLE

            }

        }
        }
        catch (e:java.lang.Exception){

        }
    }


    override fun onItemClick(complaintsData: GetCasesResponse.Data, actionType: String, pos: Int) {
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
                adapterActionPosition = pos
                complaintId = complaintsData.id!!
                if (complaintsData.status != null) currentStatus = complaintsData.status!!
                //hit api based on role
                /*Utilities.showProgress(mContext) //remove
                presenter.fetchStatusList(token, type)*/
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
                change = 0
                fromIncidentDetailScreen = 0
                val intent = Intent(activity!!, CommentsActivity::class.java)
                intent.putExtra("id", complaintsData.id!!)
                startActivity(intent)
            }

            else -> {
                if (layoutPost.visibility == View.VISIBLE) {
                    imgPost.visibility = View.GONE
                    path = ""
                    layoutAddPost.visibility = View.VISIBLE
                    layoutPost.visibility = View.GONE
                    edtPostInfo.setText("")
                    imgPost.setImageResource(0)
                    try {
                        Glide.with(this)
                            .load("")
                            .apply(
                                RequestOptions()
                                    .placeholder(R.drawable.camera_placeholder)
                            )
                            .into(imgPost)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    change = 0
                    fromIncidentDetailScreen = 0
                    /* val intent = Intent(activity, IncidentDetailActivity::class.java) //change
                     intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, complaintsData.id)
                     intent.putExtra(Constants.POST_OR_COMPLAINT, complaintsData.type)
                     intent.putExtra(Constants.FROM_WHERE, "nottohit")
                     startActivity(intent)*/
                }

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
        val builder = AlertDialog.Builder(mContext)
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
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
        val statusAdapter = StatusAdapter(mContext, responseObject.data.toMutableList(), this)
        val horizontalLayoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvStatus?.layoutManager = horizontalLayoutManager
        binding.rvStatus?.adapter = statusAdapter
        binding.btnDone.setOnClickListener {

            if (statusId == "-1") {
                Utilities.showMessage(mContext, getString(R.string.select_option_validation))
            } else if (statusId.equals("9")) {
                var intent = Intent(this.activity, PoliceOfficerActivity::class.java)
                intent.putExtra("token", token)
                intent.putExtra("complaintId", complaintId)
                startActivity(intent)
                dialog.dismiss()
            } else if (statusId.equals("10")) {
                var intent = Intent(this.activity, PoliceStationActivity::class.java)
                intent.putExtra("token", token)
                intent.putExtra("complaintId", complaintId)
                startActivity(intent)
                dialog.dismiss()
            } else {
                //hit status update api
                Utilities.showProgress(mContext)
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

    override fun statusUpdationSuccess(responseObject: UpdateStatusSuccess) {
        Utilities.dismissProgress()
        statusId = "-1"
        Utilities.showMessage(mContext, responseObject.message.toString())

        /*if (responseObject.data?.get(0)?.status!!.equals("Unauthentic") || responseObject.data.get(0).status!!.equals(
                "Reject"
            )
        ) {
            //refresh the list
            Utilities.showProgress(mContext)
            doApiCall()
        } else {*/
        actionChanged = true
        if (responseObject.data!!.size != 0) {
            /*updated by Navjeet for remove rejected item from list*/

            if (statusId == "0")
                complaints.get(adapterActionPosition!!).status = "Approved"
            if (statusId == "1") complaints.get(adapterActionPosition!!).status = "Rejected"
            adapter?.notifyDataSetChanged()
            adapter?.notifyPublicHomeActionData(responseObject.data, statusId)

            //   adapter?.notifyActionData(responseObject.data)
        }
        // }
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        binding.itemsswipetorefresh.isRefreshing=false
        statusId = "-1"
        if (error.equals(Constants.TOKEN_ERROR)) {
            //logout user
            //(activity as GeneralPublicActivity).logoutUser
            //ForegroundService.stopService(activity as Context)
            (activity as HomeActivity).finish()
            PreferenceHandler.clearPreferences(activity as Context)
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            Utilities.showMessage(mContext, error)
            if (edtPostInfo != null) {
                edtPostInfo.setText("")
                imgPost.setImageResource(0)
                try {
                    Glide.with(this)
                        .load("")
                        .apply(
                            RequestOptions()
                                .placeholder(R.drawable.camera_placeholder)
                        )
                        .into(imgPost)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgAdd -> {

                if (!token.isEmpty()) {
                    val value =
                        PreferenceHandler.readString(mContext, PreferenceHandler.PROFILE_JSON, "")
                    val jsondata =
                        GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
                    if (jsondata != null) {
                        //check if user is partial/fully verified
                        if (jsondata.data?.adhar_number != null && !(jsondata.data.adhar_number.equals(
                                ""
                            ))
                        ) {
                            val intent = Intent(mContext, GeneralPublicActivity::class.java)
                            startActivity(intent)
                        } else {
                            //make the user partially verified:
                            Utilities.displayInputDialog(mContext, this)
                        }
                    }
                } else {
                    com.dfa.utils.alert.AlertDialog.guesDialog(mContext)
                }

            }
        }
    }

    fun checkVisibilty() {
        if (complaints.isNotEmpty()) {
            norecordrefresh?.visibility = View.GONE
            itemsswipetorefresh?.visibility = View.VISIBLE

        } else {
            itemsswipetorefresh?.visibility = View.GONE
            norecordrefresh?.visibility = View.VISIBLE
        }

    }

    override fun onResume() {  //isfirst // !isfirst //
        super.onResume()
        //EmergencyFragment.noChnage=false

        val profile_image =
            PreferenceHandler.readString(activity!!, PreferenceHandler.PROFILE_IMAGE, "")
        if (isFirst) {
            doApiCall()
            isFirst = false
            setAdapterBoolean = false
        } else if (changeThroughIncidentScreen == 1) {
            changeThroughIncidentScreen = 0
            adapter?.clear()
            endlessScrollListener?.resetState()
            doApiCall()
        } else if (!isFirst && change == 1) {
            /* adapter?.clear()
             endlessScrollListener?.resetState()
             doApiCall()*/

            checkVisibilty()
            change = 0
        } else {
            if (fromIncidentDetailScreen == 0) {
                if (commentChange != 0) {
                    doApiCall()
                }

            }
        }

        if (profile_image.equals("true")) {
            adapter?.clear()
            endlessScrollListener?.resetState()
            doApiCall()
            change = 0
            PreferenceHandler.writeString(
                activity!!,
                PreferenceHandler.PROFILE_IMAGE,
                "false"
            )
        }
        // setProfilePic()
        fromIncidentDetailScreen = 0
        MyCasesActivity.isfirst = true
    }

    fun doApiCall() {
        if (Utilities.isInternetAvailableDialog(mContext)) {
            hitType = "foreground"
            val casesRequest =
                CasesRequest(
                    "1",
                    "",
                    "-1",
                    "1",
                    limit
                    , ""
                )  //type = -1 for fetching both cases and posts
            Utilities.showProgress(mContext)
            presenter.getComplaints(casesRequest, token, type)
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        token = PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")!!
        type = PreferenceHandler.readString(mContext, PreferenceHandler.USER_ROLE, "0")!!
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onPostAdded(responseObject: CreatePostResponse) {
        Utilities.dismissProgress()
        layoutAddPost.visibility = View.VISIBLE
        layoutPost.visibility = View.GONE
        edtPostInfo.setText("")
        imgPost.setImageResource(0)
        try {
            Glide.with(this)
                .load("")
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.camera_placeholder)
                )
                .into(imgPost)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        path = ""
        Utilities.showMessage(mContext, responseObject.message!!)
        pageCount = 1
        val casesRequest =
            CasesRequest(
                "1",
                "",
                "-1",
                pageCount.toString(),
                limit
                , ""
            ) //type = -1 for fetching all the data
        presenter.getComplaints(casesRequest, token, type)
    }

    //delete your respective complaint/post
    override fun onDeleteItem(complaintsData: GetCasesResponse.Data) {
        //nothing to do
    }

    //refresh the list after deletion
    override fun onComplaintDeleted(responseObject: DeleteComplaintResponse) {
        Utilities.dismissProgress()
        deleteItemposition?.let { complaints.removeAt(it) }
        adapter?.notifyDataSetChanged()
        /* whenDeleteCall = true
         val casesRequest =
             CasesRequest("1", "", "-1", "1", "5") //type = -1 for fetching all the data
         presenter.getComplaints(casesRequest, token, type)*/
    }

/*    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isFirst) {
            adapter?.clear()
            endlessScrollListener?.resetState()
            val casesRequest =
                CasesRequest("1", "", "-1", "1", "10") //type = -1 for fetching all the data
            Utilities.showProgress(mContext)
            presenter.getComplaints(casesRequest, token, type)
        }
    }*/

    //changes the like status
    override fun changeLikeStatus(complaintsData: GetCasesResponse.Data, position: Int) {
        // Utilities.showProgress(mContext)
        complaintIdTobeLiked = complaintsData.id
        complaintIdTobeLikedPosition = position
        //when to change like status
        /*   adapter?.notifyParticularItem(complaintIdTobeLiked!!,)
           isLike = true*/

        val token = PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")
        if (token!!.isEmpty()) {
            guestUser = "true"
        }

        //change the staus of the item based on id
        presenter.changeLikeStatus(token!!, complaintsData.id!!)
    }

    //refresh the list after like status is changed
    override fun onLikeStatusChanged(responseObject: DeleteComplaintResponse) {
        // Utilities.showMessage(mContext, responseObject.message!!)
        isLike = true
        var like = parseInt(complaints.get(complaintIdTobeLikedPosition).like_count.toString())


        if (complaints.get(complaintIdTobeLikedPosition).is_liked == 0) {
            complaints.get(complaintIdTobeLikedPosition).like_count = (like + 1).toString()
            complaints.get(complaintIdTobeLikedPosition).is_liked = 1

        } else {
            complaints.get(complaintIdTobeLikedPosition).like_count = (like - 1).toString()
            complaints.get(complaintIdTobeLikedPosition).is_liked = 0
        }

        adapter?.notifyDataSetChanged()
        // val casesRequest =
        //  CasesRequest("1", "", "-1", pageCount.toString(), limit) //type = -1 for fetching all the data
        // presenter.getComplaints(casesRequest, token, type)
    }

    override fun adharNoListener(adhaarNo: String) {
        // check is Adhar no valid
        if (!(Utilities.validateAadharNumber(adhaarNo))) {
            Toast.makeText(mContext, getString(R.string.adhar_not_valid), Toast.LENGTH_SHORT).show()
        } else {
            Utilities.showProgress(mContext)
            //hit Api to save the adhar no in backend
            presenter.saveAdhaarNo(token, adhaarNo)
        }
    }

    override fun adhaarSavedSuccess(responseObject: SignupResponse) {
        // Utilities.showMessage(mContext, responseObject.message)
        Utilities.showMessage(mContext, "Aadhaar card added successfully")
        val value = PreferenceHandler.readString(mContext, PreferenceHandler.PROFILE_JSON, "")
        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
        jsondata.data?.adhar_number = responseObject.data.adhar_number //add adhar no
        Utilities.dismissProgress()
        val intent = Intent(activity, GeneralPublicActivity::class.java)
        startActivity(intent)
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        // pageCount = page
//        val casesRequest =
//            CasesRequest(
//                "1",
//                "",
//                "-1",
//                pageCount.toString(),
//                limit /*totalItemsCount.toString()*/,
//                ""
//            )
//        presenter.getComplaints(casesRequest, token, type)
        //   progressBar.visibility = View.VISIBLE
    }


    //BAckground Task for referehing


//     class someTask() : AsyncTask<Void, Void, String>() {
//
//
//
//
//
//        override fun doInBackground(vararg params: Void?): String? {
//           doBackgroundRefresh()
//            return ""
//        }
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//
//        }
//
//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//
//        }
//    }
//


}
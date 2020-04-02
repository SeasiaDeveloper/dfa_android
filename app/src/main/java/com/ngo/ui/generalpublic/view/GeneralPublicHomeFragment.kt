package com.ngo.ui.generalpublic.view

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
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder

import com.ngo.R
import com.ngo.adapters.CasesAdapter
import com.ngo.adapters.StatusAdapter
import com.ngo.customviews.CenteredToolbar
import com.ngo.databinding.FragmentPublicHomeBinding
import com.ngo.listeners.AdharNoListener
import com.ngo.listeners.AlertDialogListener
import com.ngo.listeners.OnCaseItemClickListener
import com.ngo.pojo.request.CasesRequest
import com.ngo.pojo.request.CreatePostRequest
import com.ngo.pojo.response.*
import com.ngo.ui.crimedetails.view.IncidentDetailActivity
import com.ngo.ui.generalpublic.GeneralPublicActivity
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenter
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.ngo.ui.home.fragments.cases.view.CasesView
import com.ngo.ui.home.fragments.home.view.HomeActivity
import com.ngo.utils.Constants
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.RealPathUtil
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.dialog_change_status.*
import kotlinx.android.synthetic.main.fragment_public_home.*

class GeneralPublicHomeFragment : Fragment(), CasesView, View.OnClickListener,
    OnCaseItemClickListener, AlertDialogListener, AdharNoListener {

    override fun onClick(item: Any) {
        Utilities.showProgress(mContext)
        val complaintsData = item as GetCasesResponse.Data
        //delete the item based on id
        presenter.deleteComplaint(token, complaintsData.id!!)
    }

    private var pathOfImages = ArrayList<String>()
    lateinit var binding: FragmentPublicHomeBinding
    lateinit var mContext: Context
    private var IMAGE_REQ_CODE = 101
    private var path: String = ""
    private var imageUri: Uri? = null
    private var imageUrl: String? = null
    private var authorizationToken: String? = ""
    private var media_type: String? = ""
    private var token: String = ""
    var isFirst = true
    var type = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_public_home, container, false)
        return binding.root
    }

    override fun showDescError() {
        Utilities.dismissProgress()
        Utilities.showMessage(mContext, getString(R.string.please_select_image))
    }

    companion object {
        var change = 0
    }

    private var complaints: List<GetCasesResponse.Data> = mutableListOf()
    private var adapter: CasesAdapter? = null
    private var presenter: CasesPresenter = CasesPresenterImplClass(this)
    private var statusId = "-1"
    private var complaintId = "-1"
    private var currentStatus = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.public_dashboard)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        type = PreferenceHandler.readString(mContext, PreferenceHandler.USER_ROLE, "0")!!
        adapter = CasesAdapter(mContext, complaints.toMutableList(), this, type.toInt(), this)
        val horizontalLayoutManager = LinearLayoutManager(
            mContext,
            RecyclerView.VERTICAL, false
        )
        rvPublic?.layoutManager = horizontalLayoutManager
        rvPublic?.adapter = adapter

        val value = PreferenceHandler.readString(mContext, PreferenceHandler.PROFILE_JSON, "")
        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
        if (jsondata != null) {
            if (jsondata.data?.profile_pic != null) {
                Glide.with(this).load(jsondata.data.profile_pic).into(imgProfile)
            }
        }


        imgAdd.setOnClickListener(this)
        Utilities.showProgress(mContext)

        val casesRequest =
            CasesRequest("1", "", "-1")  //type = -1 for fetching both cases and posts

        presenter.getComplaints(casesRequest, token)

        txtAddPost.setOnClickListener {
            //show the addPost layout
            layoutAddPost.visibility = View.GONE
            layoutPost.visibility = View.VISIBLE
        }

        btnPost.setOnClickListener {
            Utilities.showProgress(mContext)
            val pathArray = arrayOf(path)
            //hit api to add post and display post layout
            val request = CreatePostRequest(edtPostInfo.text.toString(), pathArray, media_type!!)
            authorizationToken =
                PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")
            presenter.createPost(request, authorizationToken)
            layoutAddPost.visibility = View.VISIBLE
            layoutPost.visibility = View.GONE
        }

        btnCancel.setOnClickListener {
            //show the addPost layout
            layoutAddPost.visibility = View.VISIBLE
            layoutPost.visibility = View.GONE
            edtPostInfo.setText("")
            imgPost.setImageResource(0)
        }

        img_attach.setOnClickListener {
            //open gallery
            val resultGallery = getMarshmallowPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Utilities.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
            if (resultGallery)
                galleryIntent()
        }
    }

    private fun galleryIntent() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/* video/*"
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
            media_type = "photos"
            val selectedMedia: Uri = data.getData()!!
            val cr = mContext.contentResolver
            val mime = cr.getType(selectedMedia)
            if (mime?.toLowerCase()?.contains("image")!!) {
                val selectedImage = data.data
                val filePathColumn =
                    arrayOf(MediaStore.Images.Media.DATA)
                val cursor = activity!!.getContentResolver()
                    .query(selectedImage!!, filePathColumn, null, null, null)
                cursor?.moveToFirst()
                val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
                val picturePath = cursor?.getString(columnIndex!!)
                cursor?.close()
                Glide.with(this).load(picturePath).into(imgPost)
                path = picturePath!!
                change=0
            } else if (mime.toLowerCase().contains("video")) {
                media_type = "videos"
                if (data?.data != null) {
                    val realpath = RealPathUtil.getRealPath(activity!!, data.data!!)
                    val thumbnail = RealPathUtil.getThumbnailFromVideo(realpath!!)
                    Glide.with(mContext)
                        .load(thumbnail)
                        //.apply(options)
                        .into(imgPost)
                    path = RealPathUtil.getRealPath(activity!!, data.data!!).toString()
                }
                change=0
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

    override fun showGetComplaintsResponse(response: GetCasesResponse) {
        Utilities.dismissProgress()
        complaints = response.data!!
        if (complaints.isNotEmpty()) {
            tvRecord?.visibility = View.GONE
            adapter?.setList(response.data.toMutableList())
            // rvPublic?.adapter = adapter
            change = 1
        } else {
            tvRecord.visibility = View.VISIBLE
        }

        val value = PreferenceHandler.readString(mContext, PreferenceHandler.PROFILE_JSON, "")
        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
        if (jsondata != null) {
            if (jsondata.data?.profile_pic != null) {
                if (activity != null) {
                    Glide.with(this).load(jsondata.data.profile_pic).into(imgProfile)
                }
            }
        }
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
                if (complaintsData.status != null) currentStatus = complaintsData.status
                //hit api based on role
                Utilities.showProgress(mContext)
                presenter.fetchStatusList(token, type)
            }

            else -> {
                val intent = Intent(activity, IncidentDetailActivity::class.java)
                intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, complaintsData.id)
                intent.putExtra(Constants.POST_OR_COMPLAINT, complaintsData.type)
                startActivity(intent)
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
        ) as com.ngo.databinding.DialogChangeStatusBinding
        // Inflate and set the layout for the dialog
        if (!description.equals("null") && !description.equals("")) binding.etDescription.setText(
            description
        )

        //display the list on the screen
        val statusAdapter = StatusAdapter(mContext, responseObject.data.toMutableList(), this)
        val horizontalLayoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvStatus?.layoutManager = horizontalLayoutManager
        binding.rvStatus?.adapter = statusAdapter
        binding.btnDone.setOnClickListener {
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

        builder.setView(binding.root)
        dialog = builder.create()
        dialog.show()
    }

    override fun statusUpdationSuccess(responseObject: DeleteComplaintResponse) {
        Utilities.dismissProgress()
        Utilities.showMessage(mContext, responseObject.message.toString())
        //refresh the list
        Utilities.showProgress(mContext)
        val casesRequest =
            CasesRequest("1", "", "-1")  //type = -1 for fetching both cases and posts
        presenter.getComplaints(casesRequest, token)
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(mContext, error)
        if (edtPostInfo != null) {
            edtPostInfo.setText("")
            imgPost.setImageResource(0)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgAdd -> {
                val value =
                    PreferenceHandler.readString(mContext, PreferenceHandler.PROFILE_JSON, "")
                val jsondata =
                    GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
                if (jsondata != null) {
                    //check if user is partial/fully verified
                    if (jsondata.data?.adhar_number != null && !(jsondata.data.adhar_number.equals(""))) {
                        val intent = Intent(mContext, GeneralPublicActivity::class.java)
                        startActivity(intent)
                    } else {
                        //make the user partially verified:
                        Utilities.displayInputDialog(mContext, this)
                    }
                }
            }
        }
    }

    fun changeProfileImage() {
        if (activity != null) {
            val value = PreferenceHandler.readString(mContext, PreferenceHandler.PROFILE_JSON, "")
            val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
            if (jsondata != null) {
                if (jsondata.data?.profile_pic != null) {
                    Glide.with(this).load(jsondata.data.profile_pic).into(imgProfile)
                }
            }

        }

    }

    override fun onResume() {
        super.onResume()
        if (isFirst) {
            isFirst = false
            val casesRequest = CasesRequest("1", "", "-1") //type = -1 for fetching all the data
            Utilities.showProgress(mContext)
            presenter.getComplaints(casesRequest, token)
        } else if (change == 1) {
            val casesRequest = CasesRequest("1", "", "-1") //type = -1 for fetching all the data
            Utilities.showProgress(activity!!)
            presenter.getComplaints(casesRequest, token)
            change = 0

        }

        // var imageUrl: String = ""
        // changeProfileImage(imageUrl)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        token = PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")!!
    }

    override fun onPostAdded(responseObject: GetCasesResponse) {
        Utilities.dismissProgress()
        layoutAddPost.visibility = View.VISIBLE
        layoutPost.visibility = View.GONE
        edtPostInfo.setText("")
        imgPost.setImageResource(0)
        path = ""
        Utilities.showMessage(mContext, responseObject.message!!)
        adapter?.setList(responseObject.data?.toMutableList()!!)
        //rvPublic?.adapter?.notifyDataSetChanged()
    }

    //delete your respective complaint/post
    override fun onDeleteItem(complaintsData: GetCasesResponse.Data) {
        //nothing to do
    }

    //refresh the list after deletion
    override fun onComplaintDeleted(responseObject: DeleteComplaintResponse) {
        Utilities.showMessage(mContext, responseObject.message!!)
        val casesRequest = CasesRequest("1", "", "-1") //type = -1 for fetching all the data
        presenter.getComplaints(casesRequest, token)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isFirst) {
            val casesRequest = CasesRequest("1", "", "-1") //type = -1 for fetching all the data
            Utilities.showProgress(mContext)
            presenter.getComplaints(casesRequest, token)
        }
    }

    //changes the like status
    override fun changeLikeStatus(complaintsData: GetCasesResponse.Data) {
        Utilities.showProgress(mContext)
        val token = PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")
        //change the staus of the item based on id
        presenter.changeLikeStatus(token!!, complaintsData.id!!)
    }

    //refresh the list after like status is changed
    override fun onLikeStatusChanged(responseObject: DeleteComplaintResponse) {
        Utilities.showMessage(mContext, responseObject.message!!)
        val casesRequest = CasesRequest("1", "", "-1") //type = -1 for fetching all the data
        presenter.getComplaints(casesRequest, token)
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
        Utilities.showMessage(mContext, responseObject.message)
        val value = PreferenceHandler.readString(mContext, PreferenceHandler.PROFILE_JSON, "")
        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
        jsondata.data?.adhar_number = responseObject.data.adhar_number //add adhar no
        Utilities.dismissProgress()
        val intent = Intent(activity, GeneralPublicActivity::class.java)
        startActivity(intent)
    }

}
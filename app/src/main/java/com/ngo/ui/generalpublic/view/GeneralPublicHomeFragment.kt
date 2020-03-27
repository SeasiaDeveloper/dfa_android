package com.ngo.ui.generalpublic.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngo.R
import com.ngo.adapters.CasesAdapter
import com.ngo.customviews.CenteredToolbar
import com.ngo.databinding.FragmentPublicHomeBinding
import com.ngo.listeners.OnCaseItemClickListener
import com.ngo.pojo.request.CasesRequest
import com.ngo.pojo.request.CreatePostRequest
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.ui.generalpublic.GeneralPublicActivity
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenter
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.ngo.ui.home.fragments.cases.view.CasesView
import com.ngo.utils.Constants
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.fragment_public_home.*
import kotlinx.android.synthetic.main.fragment_public_home.btnCancel
import kotlinx.android.synthetic.main.fragment_public_home.toolbarLayout

class GeneralPublicHomeFragment : Fragment(), CasesView, View.OnClickListener,
    OnCaseItemClickListener {

    lateinit var binding: FragmentPublicHomeBinding
    lateinit var mContext: Context
    private var IMAGE_REQ_CODE = 101
    private var path: String = ""

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
        Utilities.showMessage(activity!!, getString(R.string.please_select_image))
    }

    companion object {
        var change = 0
    }

    private var complaints: List<GetCasesResponse.Data> = mutableListOf()
    private lateinit var adapter: CasesAdapter
    private var presenter: CasesPresenter = CasesPresenterImplClass(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.public_dashboard)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)

        /*  if (((toolbarLayout as CenteredToolbar).title).equals("My Cases")) {
              val casesRequest = CasesRequest("0", "") //all = "0" for my case
          }*/
        imgAdd.setOnClickListener(this)
        Utilities.showProgress(activity!!)

        val casesRequest = CasesRequest("1", "") //to verify
        presenter.getComplaints(casesRequest)

        txtAddPost.setOnClickListener {
            //show the addPost layout
            layoutAddPost.visibility = View.GONE
            layoutPost.visibility = View.VISIBLE
        }

        btnPost.setOnClickListener {
            Utilities.showProgress(mContext)
            val pathArray = arrayOf(path)
            //hit api to add post and display post layout
            val request = CreatePostRequest(edtPostInfo.text.toString(), pathArray, "testt")
            presenter.createPost(request)
            layoutAddPost.visibility = View.VISIBLE
            layoutPost.visibility = View.GONE
        }

        btnCancel.setOnClickListener {
            //show the addPost layout
            layoutAddPost.visibility = View.VISIBLE
            layoutPost.visibility = View.GONE
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
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select File"), IMAGE_REQ_CODE)
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
            if (data.data != null) {
                val imageUri = data.data
                val wholeID = DocumentsContract.getDocumentId(imageUri)
                val id =
                    wholeID.split((":").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]
                val column = arrayOf(MediaStore.Images.Media.DATA)
                val sel = MediaStore.Images.Media._ID + "=?"
                val cursor = mContext.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, arrayOf(id), null
                )
                val columnIndex = cursor?.getColumnIndex(column[0])
                if (cursor!!.moveToFirst()) {
                    path = cursor.getString(columnIndex!!)
                }
                cursor.close()
                imgPost.setImageURI(imageUri)
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
            tvRecord.visibility = View.GONE
            adapter = CasesAdapter(activity!!, complaints.toMutableList(), this, 1)
            val horizontalLayoutManager = LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL, false
            )
            rvPublic.layoutManager = horizontalLayoutManager
            rvPublic.adapter = adapter
        } else {
            tvRecord.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(complaintsData: GetCasesResponse.Data, type: String) {
        /* val intent = Intent(activity, IncidentDetailActivity::class.java)
         intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, complaintsData)
         startActivity(intent)*/
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(activity!!, error)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgAdd -> {
                val intent = Intent(activity, GeneralPublicActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (change == 1) {
            val casesRequest = CasesRequest("1", "") //to verify
            Utilities.showProgress(activity!!)
            presenter.getComplaints(casesRequest)
            change = 0

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onPostAdded(responseObject: GetCasesResponse) {
        Utilities.dismissProgress()
        layoutAddPost.visibility = View.GONE
        layoutPost.visibility = View.VISIBLE
        Utilities.showMessage(mContext, responseObject.message!!)
    }
}
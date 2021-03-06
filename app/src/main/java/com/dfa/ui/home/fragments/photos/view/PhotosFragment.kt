package com.dfa.ui.home.fragments.photos.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dfa.R
import com.dfa.adapters.PhotosAdapter
import com.dfa.application.MyApplication
import com.dfa.customviews.GridSpacingItemDecoration
import com.dfa.pojo.request.GetPhotosRequest
import com.dfa.pojo.response.GetCrimeDetailsResponse
import com.dfa.pojo.response.GetPhotosResponse
import com.dfa.ui.crimedetails.view.IncidentDetailActivity
import com.dfa.ui.emergency.view.EmergencyFragment
import com.dfa.ui.generalpublic.view.GeneralPublicHomeFragment
import com.dfa.ui.home.fragments.photos.presenter.PhotosPresenter
import com.dfa.ui.home.fragments.photos.presenter.PhotosPresenterImpl
import com.dfa.utils.Constants
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import com.dfa.utils.Utilities.showProgress
import kotlinx.android.synthetic.main.fragment_photos.*
import kotlinx.android.synthetic.main.fragment_photos.itemsswipetorefresh
import kotlinx.android.synthetic.main.fragment_photos.tvRecord

class PhotosFragment : Fragment(), PhotosView, OnClickOfVideoAndPhoto {
    private lateinit var adapter: PhotosAdapter
    private var photos: List<GetPhotosResponse.Data> = mutableListOf()
    lateinit var request: GetPhotosRequest
    private var presenter: PhotosPresenter = PhotosPresenterImpl(this)
    private var authorizationToken: String? = ""
    private var isFirst: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photos, container, false)!!
    }

    override fun onResume() {
        super.onResume()
       // EmergencyFragment.noChnage=false
        if (isFirst) {
            request = GetPhotosRequest("photos")
            if (Utilities.isInternetAvailableDialog(MyApplication.instance)) {
                showProgress(activity!!)
            authorizationToken =
                PreferenceHandler.readString(activity!!, PreferenceHandler.AUTHORIZATION, "")
                presenter.getPhotos(authorizationToken, request)
            }
            isFirst = false
        }
        else
        {
            checkVisiibilty()
        }
    }


    fun pullToRefreshSettings(itemsswipetorefresh: SwipeRefreshLayout)
    {
        itemsswipetorefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                activity!!,
                R.color.colorDarkGreen
            )
        )
        itemsswipetorefresh.setColorSchemeColors(Color.WHITE)

        itemsswipetorefresh.setOnRefreshListener {
            //pageCount = 0
            if (Utilities.isInternetAvailableDialog(MyApplication.instance)) {
                photos.toMutableList().clear()
                presenter.getPhotos(authorizationToken, request)
            }
            itemsswipetorefresh.isRefreshing = false

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        pullToRefreshSettings(itemsswipetorefresh)
        pullToRefreshSettings(norecordrefresh)

        /* request = GetPhotosRequest("photos")
         showProgress(activity!!)
         val authorizationToken =
             PreferenceHandler.readString(activity!!, PreferenceHandler.AUTHORIZATION, "")
         presenter.getPhotos(authorizationToken, request)*/


    }

    private fun setAdapter() {
        val layoutManager = GridLayoutManager(activity!!, 4)
        rvPhotos.setLayoutManager(layoutManager)
        val spanCount = 4
        val spacing = 10
        val includeEdge = true
        rvPhotos.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )

        adapter = PhotosAdapter(activity!!, photos.toMutableList(), this)
        rvPhotos.adapter = adapter

    }

    override fun onPause() {
        super.onPause()
        GeneralPublicHomeFragment.change = 1
    }

    fun checkVisiibilty()
    {

        if (photos.isNotEmpty()) {
            norecordrefresh?.visibility = View.GONE
            itemsswipetorefresh?.visibility = View.VISIBLE

        } else {
            itemsswipetorefresh?.visibility = View.GONE
            norecordrefresh?.visibility = View.VISIBLE
        }
    }

    override fun showGetPhotosResponse(response: GetPhotosResponse) {
        Utilities.dismissProgress()
        photos = response.data!!
        adapter.changeList(photos.toMutableList())
        checkVisiibilty()
    }

    override fun getPhotosFailure(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(activity!!, error)
    }

    override fun getCrimeDetailsSuccess(crimeDetailsResponse: GetCrimeDetailsResponse) {
        Utilities.dismissProgress()
        val intent = Intent(activity, IncidentDetailActivity::class.java)
        intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, crimeDetailsResponse.data?.get(0)?.id)
        intent.putExtra(Constants.POST_OR_COMPLAINT, crimeDetailsResponse.data?.get(0)?.type)
        startActivity(intent)

    }

    override fun getCrimeDetailsFailure(error: String) {
        Utilities.dismissProgress()
        if (activity != null) {
            Utilities.showMessage(activity!!, error)
        }
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        if (activity != null) {
            Utilities.showMessage(activity!!, error)
        }
    }

    override fun getComplaintId(id: String?) {
        val intent = Intent(activity, IncidentDetailActivity::class.java)
        intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, id)
        // intent.putExtra(Constants.POST_OR_COMPLAINT, crimeDetailsResponse?.data?.get(0)?.type)
        intent.putExtra(Constants.FROM_WHERE, "tohit")
        startActivity(intent)


        /* if (isInternetAvailable()) {
             Utilities.showProgress(activity!!)
             var crimeDetailsRequest = CrimeDetailsRequest(id!!)
             authorizationToken =
                 PreferenceHandler.readString(activity!!, PreferenceHandler.AUTHORIZATION, "")
             presenter.getComplaintDetails(crimeDetailsRequest, authorizationToken)
         } else {
             Utilities.showMessage(activity!!, getString(R.string.no_internet_connection))
         }*/
    }

    /*
  * method to check internet connection
  * */




}
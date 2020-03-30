package com.ngo.ui.home.fragments.photos.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ngo.R
import com.ngo.adapters.PhotosAdapter
import com.ngo.customviews.GridSpacingItemDecoration
import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.request.GetPhotosRequest
import com.ngo.pojo.response.GetCrimeDetailsResponse
import com.ngo.pojo.response.GetPhotosResponse
import com.ngo.ui.crimedetails.view.IncidentDetailActivity
import com.ngo.ui.home.fragments.photos.presenter.PhotosPresenter
import com.ngo.ui.home.fragments.photos.presenter.PhotosPresenterImpl
import com.ngo.utils.Constants
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import com.ngo.utils.Utilities.showProgress
import kotlinx.android.synthetic.main.fragment_photos.*

class PhotosFragment : Fragment(), PhotosView, OnClickOfVideoAndPhoto {

    private lateinit var adapter: PhotosAdapter
    private var photos: List<GetPhotosResponse.Data> = mutableListOf()
    lateinit var request: GetPhotosRequest
    private var presenter: PhotosPresenter = PhotosPresenterImpl(this)
    private var authorizationToken: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photos, container, false)!!
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        request = GetPhotosRequest("photos")
        Utilities.showProgress(activity!!)
        presenter.getPhotos(request)
    }

    private fun setAdapter() {
        val layoutManager = GridLayoutManager(activity!!, 2)
        rvPhotos.setLayoutManager(layoutManager)
        val spanCount = 2
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
        rvPhotos.setAdapter(adapter)

    }

    override fun showGetPhotosResponse(response: GetPhotosResponse) {
        Utilities.dismissProgress()
        photos = response.data!!
        adapter.changeList(photos.toMutableList())
        if (photos.isNotEmpty()) {
            tvRecord?.visibility = View.GONE
            rvPhotos?.visibility = View.VISIBLE

        } else {
            tvRecord?.visibility = View.VISIBLE
            rvPhotos?.visibility = View.GONE
        }
    }

    override fun getPhotosFailure(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(activity!!, error)
    }

    override fun getCrimeDetailsSuccess(crimeDetailsResponse: GetCrimeDetailsResponse) {
        Utilities.dismissProgress()
        val intent = Intent(activity, IncidentDetailActivity::class.java)
        intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, crimeDetailsResponse?.data?.get(0)?.id)
        intent.putExtra(Constants.POST_OR_COMPLAINT, crimeDetailsResponse?.data?.get(0)?.type)
        startActivity(intent)

    }

    override fun getCrimeDetailsFailure(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(activity!!, error)
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(activity!!, error)
    }

    override fun getComplaintId(id: String?) {
        if (isInternetAvailable()) {
            Utilities.showProgress(activity!!)
            var crimeDetailsRequest = CrimeDetailsRequest(id!!)
            authorizationToken =
                PreferenceHandler.readString(activity!!, PreferenceHandler.AUTHORIZATION, "")
            presenter.getComplaintDetails(crimeDetailsRequest, authorizationToken)
        } else {
            Utilities.showMessage(activity!!, getString(R.string.no_internet_connection))
        }
    }

    /*
  * method to check internet connection
  * */
    fun isInternetAvailable(): Boolean {
        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }
}
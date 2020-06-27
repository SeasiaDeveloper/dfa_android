package com.dfa.ui.home.fragments.videos.view

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
import com.dfa.adapters.VideosAdapter
import com.dfa.customviews.GridSpacingItemDecoration
import com.dfa.pojo.request.GetPhotosRequest
import com.dfa.pojo.response.GetCrimeDetailsResponse
import com.dfa.pojo.response.GetPhotosResponse
import com.dfa.ui.crimedetails.view.IncidentDetailActivity
import com.dfa.ui.emergency.view.EmergencyFragment
import com.dfa.ui.generalpublic.view.GeneralPublicHomeFragment
import com.dfa.ui.home.fragments.photos.view.OnClickOfVideoAndPhoto
import com.dfa.ui.home.fragments.videos.presenter.VideoPresenter
import com.dfa.ui.home.fragments.videos.presenter.VideosPresenterImpl
import com.dfa.utils.Constants
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.fragment_videos.*
import kotlinx.android.synthetic.main.fragment_videos.itemsswipetorefresh

class VideosFragment : Fragment(), VideosView, OnClickOfVideoAndPhoto {
    private lateinit var adapter: VideosAdapter
    private var videos: List<GetPhotosResponse.Data> = mutableListOf()
    lateinit var request: GetPhotosRequest
    private var presenter: VideoPresenter = VideosPresenterImpl(this)
    private var authorizationToken: String? = ""
    private var isFirst: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_videos, container, false)!!
    }

    override fun onResume() {
        super.onResume()
        EmergencyFragment.noChnage=false

        if (isFirst) {
            request = GetPhotosRequest("videos")
            Utilities.showProgress(activity!!)
            authorizationToken =
                PreferenceHandler.readString(activity!!, PreferenceHandler.AUTHORIZATION, "")
            presenter.getVideos(authorizationToken, request)
            isFirst=false
        }
        else
        {
            checkVisiibilty()
        }

    }


    fun pullToRefreshSettings(itemsswipetorefresh:SwipeRefreshLayout)
    {
        itemsswipetorefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                activity!!,
                R.color.colorDarkGreen
            )
        )
        itemsswipetorefresh.setColorSchemeColors(Color.WHITE)

        itemsswipetorefresh.setOnRefreshListener {
            presenter.getVideos(authorizationToken, request)
            itemsswipetorefresh.isRefreshing = false
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        pullToRefreshSettings(itemsswipetorefresh)
        pullToRefreshSettings(norecordrefresh)

    }

    override fun onPause() {
        super.onPause()
        GeneralPublicHomeFragment.change = 1
    }

    private fun setAdapter() {
        val layoutManager = GridLayoutManager(activity!!, 4)
        rvVideos.layoutManager = layoutManager
        val spanCount = 4
        val spacing = 10
        val includeEdge = true
        rvVideos.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )
        adapter = VideosAdapter(activity!!, videos.toMutableList(), this)
        rvVideos.adapter = adapter

    }


    fun checkVisiibilty()
    {
        if (videos.isNotEmpty()) {
            itemsswipetorefresh?.visibility = View.VISIBLE
            norecordrefresh?.visibility = View.GONE

        } else {
            norecordrefresh?.visibility = View.VISIBLE
            itemsswipetorefresh?.visibility = View.GONE
        }
    }

    override fun showGetVideosResponse(response: GetPhotosResponse) {
        Utilities.dismissProgress()
        videos = response.data!!
        adapter.changeList(videos.toMutableList())
         checkVisiibilty()
    }

    override fun getVideosFailure(error: String) {
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
        Utilities.showMessage(activity!!, error)
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(activity!!, error)
    }

    override fun getComplaintId(id: String?) {
        val intent = Intent(activity, IncidentDetailActivity::class.java)
        intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, id)
        intent.putExtra(Constants.FROM_WHERE, "tohit")
        //intent.putExtra(Constants.POST_OR_COMPLAINT, crimeDetailsResponse.data?.get(0)?.type)
        startActivity(intent)
        /*if (isInternetAvailable()) {
            Utilities.showProgress(activity!!)
            val crimeDetailsRequest = CrimeDetailsRequest(id!!)
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

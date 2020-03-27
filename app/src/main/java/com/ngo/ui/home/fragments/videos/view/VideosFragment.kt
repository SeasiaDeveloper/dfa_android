package com.ngo.ui.home.fragments.videos.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ngo.R
import com.ngo.adapters.VideosAdapter
import com.ngo.customviews.GridSpacingItemDecoration
import com.ngo.pojo.request.GetPhotosRequest
import com.ngo.pojo.response.GetPhotosResponse
import com.ngo.ui.home.fragments.photos.presenter.PhotosPresenter
import com.ngo.ui.home.fragments.photos.presenter.PhotosPresenterImpl
import com.ngo.ui.home.fragments.photos.view.PhotosView
import com.ngo.ui.home.fragments.videos.presenter.VideoPresenter
import com.ngo.ui.home.fragments.videos.presenter.VideosPresenterImpl
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.fragment_photos.*
import kotlinx.android.synthetic.main.fragment_videos.*

class VideosFragment: Fragment(), VideosView {

    private lateinit var adapter: VideosAdapter
    private var videos: List<GetPhotosResponse.Data> = mutableListOf()
    lateinit var request: GetPhotosRequest
    private var presenter: VideoPresenter = VideosPresenterImpl(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_videos, container, false)!!
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        request = GetPhotosRequest("videos")
        Utilities.showProgress(activity!!)
        presenter.getVideos(request)
    }

    private fun setAdapter() {
        val layoutManager = GridLayoutManager(activity!!, 2)
        rvVideos.setLayoutManager(layoutManager)
        val spanCount = 2
        val spacing = 10
        val includeEdge = true
        rvVideos.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )
        adapter = VideosAdapter(activity!!, videos.toMutableList())
        rvVideos.setAdapter(adapter)

    }

    override fun showGetVideosResponse(response: GetPhotosResponse) {
            Utilities.dismissProgress()
            videos = response.data!!
            adapter.changeList(videos.toMutableList())
            if (videos.isNotEmpty()) {
                tvRecordVideos.visibility = View.GONE
                rvVideos.visibility = View.VISIBLE

            } else {
                tvRecordVideos.visibility = View.VISIBLE
                rvVideos.visibility = View.GONE
            }
    }

    override fun getVideosFailure(error: String) {
        Utilities. dismissProgress()
        Utilities.showMessage(activity!!, error)
    }

    override fun showServerError(error: String) {
        Utilities. dismissProgress()
        Utilities.showMessage(activity!!, error)
    }

}

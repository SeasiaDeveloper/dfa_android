package com.ngo.ui.home.fragments.photos.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ngo.R
import com.ngo.adapters.PhotosAdapter
import com.ngo.customviews.GridSpacingItemDecoration
import com.ngo.pojo.request.GetPhotosRequest
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.pojo.response.GetPhotosResponse
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenter
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.ngo.ui.home.fragments.cases.view.CasesView
import com.ngo.ui.home.fragments.photos.presenter.PhotosPresenter
import com.ngo.ui.home.fragments.photos.presenter.PhotosPresenterImpl
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.fragment_photos.*

class PhotosFragment :Fragment(), PhotosView {

    private lateinit var adapter: PhotosAdapter
    private var photos: List<GetPhotosResponse.Data> = mutableListOf()
    lateinit var request: GetPhotosRequest
    private var presenter: PhotosPresenter = PhotosPresenterImpl(this)

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
        adapter = PhotosAdapter(activity!!, photos.toMutableList())
        rvPhotos.setAdapter(adapter)

    }

    override fun showGetPhotosResponse(response: GetPhotosResponse) {
        Utilities.dismissProgress()
        photos = response.data!!
        adapter.changeList(photos.toMutableList())
        if (photos.isNotEmpty()) {
            tvRecord.visibility = View.GONE
            rvPhotos.visibility = View.VISIBLE

        } else {
            tvRecord.visibility = View.VISIBLE
            rvPhotos.visibility = View.GONE
        }

    }

    override fun getPhotosFailure(error: String) {
        Utilities. dismissProgress()
        Utilities.showMessage(activity!!, error)
    }

    override fun showServerError(error: String) {
        Utilities. dismissProgress()
        Utilities.showMessage(activity!!, error)
    }


}
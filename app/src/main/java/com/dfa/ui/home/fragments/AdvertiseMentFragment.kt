package com.dfa.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dfa.R
import com.dfa.adapters.AdvertisementAdapter
import com.dfa.databinding.FragmentAdvertiseMentBinding
import com.dfa.pojo.response.AdvertisementInput
import com.dfa.pojo.response.AdvertisementResponse
import com.dfa.ui.generalpublic.pagination.EndlessRecyclerViewScrollListenerImplementation
import com.dfa.ui.home.fragments.home.presenter.AdvertisementCallback
import com.dfa.ui.home.fragments.home.presenter.AdvertisementPresenter
import com.dfa.utils.Utilities

class AdvertiseMentFragment : Fragment(), AdvertisementCallback, EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
var binding:FragmentAdvertiseMentBinding?=null
    var presenter: AdvertisementPresenter?=null
    var advertisAdapter:AdvertisementAdapter?=null
    var pages=5
    var limit=5
    var responseObjectList: ArrayList<AdvertisementResponse.Data>?=null

    var endlessScrollListener: EndlessRecyclerViewScrollListenerImplementation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_advertise_ment, container, false)

        responseObjectList= ArrayList()
        setAdapter()
        callPresenter(pages.toString())
        return binding!!.root
    }

    fun callPresenter(perPage: String) {
        var input= AdvertisementInput()
        input.per_page=perPage
        input.page="1"
        Utilities.showProgress(this.activity!!)
        presenter=AdvertisementPresenter(this)
        presenter!!.getAdvertisement(input)
    }

    override fun advertisementSuccess(responseObject: AdvertisementResponse) {
        responseObjectList= ArrayList()
        responseObjectList!!.addAll(responseObject.data!!)
        advertisAdapter!!.setData(responseObjectList!!)
        Utilities.dismissProgress()
    }

    override fun failer(serverError: String) {
        Utilities.dismissProgress()
        Toast.makeText(activity,""+serverError,Toast.LENGTH_LONG).show()
    }



    fun setAdapter() {
//        val layoutManager = LinearLayoutManager(activity)
//        binding!!.rvPhotos.setLayoutManager(layoutManager)
//        advertisAdapter = AdvertisementAdapter(this@AdvertiseMentFragment,responseObjectList!!)
//        binding!!.rvPhotos.adapter = advertisAdapter
//
//        if (endlessScrollListener == null)
//            endlessScrollListener =
//                EndlessRecyclerViewScrollListenerImplementation(
//                    layoutManager,
//                    this
//                )
//        else
//            endlessScrollListener?.setmLayoutManager(layoutManager)
//        binding!!.rvPhotos.addOnScrollListener(endlessScrollListener!!)
//        endlessScrollListener?.resetState()

    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        if(limit<=totalItemsCount){
            pages=pages+5
            callPresenter(pages.toString())
        }
    }

}
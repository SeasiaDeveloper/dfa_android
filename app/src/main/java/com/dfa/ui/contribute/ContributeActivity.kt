package com.dfa.ui.contribute

import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.databinding.ActivityContributeBinding
import com.dfa.ui.generalpublic.pagination.EndlessRecyclerViewScrollListenerImplementation
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.activity_contribute.*

class ContributeActivity : BaseActivity(),EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener,ContributeCallback {
 var binding:ActivityContributeBinding?=null
    var endlessScrollListener: EndlessRecyclerViewScrollListenerImplementation? = null
    var adapter:CoupanAdapter?=null
    var presenter:ContributePresenter?=null
    var coupanList:ArrayList<TicketResponse.Data>?=null
    var page="1"

    override fun getLayout(): Int {
        return R.layout.activity_contribute
    }
    override fun setupUI() {
        binding=viewDataBinding as ActivityContributeBinding
        (binding!!.toolbarLayout as CenteredToolbar).title = getString(R.string.contibure_title)
        (binding!!.toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (binding!!.toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)
        (binding!!.toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        coupanList= ArrayList()

        setAdapter()
        hitApi(page)
        binding!!.viewMore.setOnClickListener {
            page=page+1
            hitApi(page)
        }

    }

    fun hitApi(page: String) {
        presenter= ContributePresenter(this)
        var input=TickerInput()
        input.page=page
        input.per_page="10"
        Utilities.showProgress(this)
        presenter!!.getMarketPlaceData(input)
    }



    fun setAdapter() {
        val layoutManager = LinearLayoutManager(this)
        binding!!.rvCouponList.setLayoutManager(layoutManager)
        adapter = CoupanAdapter(this@ContributeActivity, coupanList!!)
        binding!!.rvCouponList.adapter = adapter

        if (endlessScrollListener == null)
            endlessScrollListener =
                EndlessRecyclerViewScrollListenerImplementation(layoutManager, this)
        else
            endlessScrollListener?.setmLayoutManager(layoutManager)
        binding!!.rvCouponList.addOnScrollListener(endlessScrollListener!!)
        endlessScrollListener?.resetState()
    }

    override fun handleKeyboard(): View {
      return parentLayout
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {

    }

    override fun onSuccess(responseObject: TicketResponse) {
        if(responseObject.data!!.size>0){
            coupanList!!.addAll(responseObject.data!!)
            adapter!!.setData(coupanList!!)
            Utilities.dismissProgress()
        }

    }

    override fun onFailed(s: String) {
        Toast.makeText(this,"s"+s,Toast.LENGTH_LONG).show()
        Utilities.dismissProgress()
    }
}
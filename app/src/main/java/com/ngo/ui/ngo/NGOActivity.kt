package com.ngo.ui.ngo

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.listeners.OnListItemClickListener
import com.ngo.pojo.response.GetComplaintsResponse
import com.ngo.ui.ngo.adapter.GeneralComplaintsListAdapter
import com.ngo.ui.ngo.presenter.NgoPresenter
import com.ngo.ui.ngo.presenter.NgoPresenterImpl
import com.ngo.ui.ngo.view.NgoView
import com.ngo.ui.ngoform.NGOFormDetailActivity
import com.ngo.utils.Constants
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_drugs.*

class NGOActivity : BaseActivity(), NgoView, OnListItemClickListener {

    private var complaints: List<GetComplaintsResponse.Data> = mutableListOf()
    private lateinit var adapter: GeneralComplaintsListAdapter
    private var presenter: NgoPresenter = NgoPresenterImpl(this)


    companion object {
        var change = 0
    }
    override fun getLayout(): Int {
        return R.layout.activity_drugs
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.ngo)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }

showProgress()
        presenter.getComplaints()
    }

    override fun handleKeyboard(): View {
        return drugsParentLayout
    }


    override fun showGetComplaintsResponse(response: GetComplaintsResponse) {
        dismissProgress()
        complaints = response.data!!
        if (complaints.isNotEmpty()) {
            tvRecord.visibility = View.GONE
            adapter = GeneralComplaintsListAdapter(this, complaints.toMutableList(), this, 2)
            val horizontalLayoutManager = LinearLayoutManager(
                this,
                RecyclerView.VERTICAL, false
            )
            rvDrugs.layoutManager = horizontalLayoutManager
            rvDrugs.adapter = adapter
        } else {
            tvRecord.visibility = View.VISIBLE
        }
    }

    override fun showDescError() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.please_select_image))
    }

    override fun onItemClick(complaintsData: GetComplaintsResponse.Data, type: String) {
        when (type) {

            "location" -> {
                val gmmIntentUri = Uri.parse("google.navigation:q="+complaintsData.lat+","+complaintsData.lng+"")
              //  val gmmIntentUri = Uri.parse("google.navigation:q="+30.7106607+","+76.7091493+"")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
            else-> {
                val intent = Intent(this, NGOFormDetailActivity::class.java)
                intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, complaintsData)
                startActivity(intent)
            }

        }

    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun onResume() {
        super.onResume()
        if(change==1)
        {showProgress()
            presenter.getComplaints()
            change=0

        }
    }

}
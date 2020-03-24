package com.ngo.ui.generalpublic.view

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.listeners.OnListItemClickListener
import com.ngo.pojo.response.GetComplaintsResponse
import com.ngo.ui.generalpublic.GeneralPublicActivity
import com.ngo.ui.ngo.NGOActivity
import com.ngo.ui.ngo.adapter.GeneralComplaintsListAdapter
import com.ngo.ui.ngo.presenter.NgoPresenter
import com.ngo.ui.ngo.presenter.NgoPresenterImpl
import com.ngo.ui.ngo.view.NgoView
import com.ngo.utils.Constants
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_public_home.*


class GeneralPublicHomeActivity : BaseActivity(), NgoView, View.OnClickListener , OnListItemClickListener {
    override fun showDescError() {
        dismissProgress()
        Utilities.showMessage(this, getString(R.string.please_select_image))
    }

    companion object {
        var change = 0
    }
    private var complaints: List<GetComplaintsResponse.Data> = mutableListOf()
    private lateinit var adapter: GeneralComplaintsListAdapter
    private var presenter: NgoPresenter = NgoPresenterImpl(this)

    override fun getLayout(): Int {
        return R.layout.activity_public_home
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.public_dashboard)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        imgAdd.setOnClickListener(this)
        showProgress()
        presenter.getComplaints()
    }

    override fun handleKeyboard(): View {
        return dashboardParentLayout
    }


    override fun showGetComplaintsResponse(response: GetComplaintsResponse) {
        dismissProgress()
        complaints = response.data!!
        if (complaints.isNotEmpty()) {
            tvRecord.visibility = View.GONE
            adapter = GeneralComplaintsListAdapter(this, complaints.toMutableList(),this,1)
            val horizontalLayoutManager = LinearLayoutManager(
                this,
                RecyclerView.VERTICAL, false
            )
            rvPublic.layoutManager = horizontalLayoutManager
            rvPublic.adapter = adapter
        } else {
            tvRecord.visibility = View.VISIBLE
        }
    }



    override fun onItemClick(complaintsData:GetComplaintsResponse.Data,type:String) {
        val intent = Intent(this, IncidentDetailActivity::class.java)
        intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, complaintsData)
        startActivity(intent)    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }



    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.imgAdd -> {

                var intent = Intent(this@GeneralPublicHomeActivity, GeneralPublicActivity::class.java)
                startActivity(intent)


            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(change ==1)
        {showProgress()
            presenter.getComplaints()
           change =0

        }
    }
}
package com.ngo.ui.generalpublic.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngo.R
import com.ngo.customviews.CenteredToolbar
import com.ngo.databinding.FragmentPublicHomeBinding
import com.ngo.listeners.OnListItemClickListener
import com.ngo.pojo.response.GetComplaintsResponse
import com.ngo.ui.generalpublic.GeneralPublicActivity
import com.ngo.ui.ngo.adapter.GeneralComplaintsListAdapter
import com.ngo.ui.ngo.presenter.NgoPresenter
import com.ngo.ui.ngo.presenter.NgoPresenterImpl
import com.ngo.ui.ngo.view.NgoView
import com.ngo.utils.Constants
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.fragment_public_home.*


class GeneralPublicHomeFragment : Fragment(), NgoView, View.OnClickListener , OnListItemClickListener {

    lateinit var binding: FragmentPublicHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
    private var complaints: List<GetComplaintsResponse.Data> = mutableListOf()
    private lateinit var adapter: GeneralComplaintsListAdapter
    private var presenter: NgoPresenter = NgoPresenterImpl(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }


     fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.public_dashboard)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        imgAdd.setOnClickListener(this)
        Utilities.showProgress(activity!!)
        presenter.getComplaints()
    }


    override fun showGetComplaintsResponse(response: GetComplaintsResponse) {
        Utilities.dismissProgress()
        complaints = response.data!!
        if (complaints.isNotEmpty()) {
            tvRecord.visibility = View.GONE
            adapter = GeneralComplaintsListAdapter(activity!!, complaints.toMutableList(),this,1)
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


    override fun onItemClick(complaintsData:GetComplaintsResponse.Data,type:String) {
        val intent = Intent(activity, IncidentDetailActivity::class.java)
        intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, complaintsData)
        startActivity(intent)    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(activity!!, error)
    }



    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgAdd -> {
                var intent = Intent(activity, GeneralPublicActivity::class.java)
                startActivity(intent)

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(change ==1)
        {
            Utilities.showProgress(activity!!)
            presenter.getComplaints()
           change =0

        }
    }
}
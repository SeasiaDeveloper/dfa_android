package com.ngo.ui.home.fragments.cases

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngo.R
import com.ngo.adapters.CasesAdapter
import com.ngo.listeners.OnCaseItemClickListener
import com.ngo.pojo.request.CasesRequest
import com.ngo.pojo.response.GetCasesResponse
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenter
import com.ngo.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.ngo.ui.home.fragments.cases.view.CasesView
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.fragment_cases.*

class CasesFragment : Fragment(), CasesView, OnCaseItemClickListener {
    private lateinit var mContext: Context
    private var presenter: CasesPresenter = CasesPresenterImplClass(this)
    private var complaints: List<GetCasesResponse.Data> = mutableListOf()
    lateinit var casesRequest: CasesRequest

    override fun showGetComplaintsResponse(response: GetCasesResponse) {
        Utilities.dismissProgress()
        complaints = response.data!!
        if (complaints.isNotEmpty()) {
            tvRecord.visibility = View.GONE
            rvPublic.visibility = View.VISIBLE
            rvPublic.adapter = CasesAdapter(mContext, complaints.toMutableList(), this, 1)
        } else {
            tvRecord.visibility = View.VISIBLE
            rvPublic.visibility = View.GONE
        }

        etSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                casesRequest = CasesRequest(
                    "1",
                    etSearch.text.toString()
                ) //all = "1" for fetchng all the cases

                Utilities.showProgress(mContext)
                //hit api with search variable
                presenter.getComplaints(casesRequest)
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })

    }

    override fun showDescError() {
        Utilities. dismissProgress()
        Utilities.showMessage(mContext, getString(R.string.please_select_image))
    }

    override fun showServerError(error: String) {
        Utilities. dismissProgress()
        Utilities.showMessage(mContext, error)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onItemClick(complaintsData: GetCasesResponse.Data, type: String) {
        /*  val intent = Intent(mContext, IncidentDetailActivity::class.java)
          intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, complaintsData)
          startActivity(intent)*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cases, container, false)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val horizontalLayoutManager = LinearLayoutManager(
            mContext,
            RecyclerView.VERTICAL, false
        )
        rvPublic.layoutManager = horizontalLayoutManager

        casesRequest = CasesRequest("1", etSearch.text.toString()) //all = "1" for fetchng all the cases

        Utilities.showProgress(mContext)
        presenter.getComplaints(casesRequest)
    }

    override fun onPostAdded(responseObject: GetCasesResponse) {
        //nothing to do
    }
}

package com.ngo.ui.ngoform

import android.graphics.Color
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.request.NGORequest
import com.ngo.pojo.response.GetComplaintsResponse
import com.ngo.pojo.response.NGOResponse
import com.ngo.ui.ngo.NGOActivity
import com.ngo.ui.ngoform.presenter.NGOFormPresenter
import com.ngo.ui.ngoform.presenter.NGOFormPresenterImpl
import com.ngo.ui.ngoform.view.NGOFormView
import com.ngo.utils.Constants
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_ngo_detail.*

class NGOFormDetailActivity : BaseActivity(), NGOFormView {

    private lateinit var complaintsData: GetComplaintsResponse.Data
    private var ngoPresenter: NGOFormPresenter = NGOFormPresenterImpl(this)

    override fun getLayout(): Int {
        return R.layout.activity_ngo_detail
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.ngo_form)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        complaintsData =
            intent.getSerializableExtra(Constants.PUBLIC_COMPLAINT_DATA) as GetComplaintsResponse.Data
        setComplaintData()
        btnSubmit.setOnClickListener {
            if (isInternetAvailable()) {

                if( etNGOComment.text.toString().trim().isEmpty())
                {
                    Utilities.showAlert(this, getString(R.string.enter_comment))
                }
                else {
                    showProgress()
                    val request = NGORequest(
                        complaintsData.id,
                        etNGOComment.text.toString().trim()
                    )
                    ngoPresenter.saveDetailsRequest(request)
                }
            } else {
                Utilities.showMessage(this, getString(R.string.no_internet_connection))
            }
        }
    }

    private fun setComplaintData() {
        var name="Anonymous"
        if(!(complaintsData.name.isNullOrEmpty() || complaintsData.name.equals("")))  name=complaintsData.name.toString()
        etUserName.setText(name)


        if (complaintsData.email!!.isNotEmpty()) {
            etEmail.setText((complaintsData.email))
        } else {
            etEmail.visibility = View.GONE
            tvEmail.visibility=View.GONE
        }
        if (complaintsData.phone!!.isNotEmpty()) {
            etContactNo.setText(complaintsData.phone)
        } else {
            etContactNo.visibility = View.GONE
            tvContact.visibility=View.GONE
        }

        if (complaintsData.forwarded==1) {
            forwarded.visibility=View.GONE
        }
        var level: Double = 0.0
        if(!complaintsData.level.equals("10"))
            level = complaintsData.level !!.toFloat()*11.0
        else
            level = 100.0
        sb_steps_5.setProgress(level.toFloat())

        sb_steps_5.isEnabled = false
        spTypesOfCrime.text = complaintsData.crime
        etDescription.setText(complaintsData.description)
        val options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.noimage)
            .error(R.drawable.noimage)
        Glide.with(this).load(complaintsData.image).apply(options).into(imgView)
    }

    override fun handleKeyboard(): View {
        return ngoDetailLayout
    }

    override fun showNGOResponse(response: NGOResponse) {
        dismissProgress()
        Utilities.showMessage(this, response.message)

        if(response.status==200) {
            NGOActivity.change=1
            finish()

        }
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

}
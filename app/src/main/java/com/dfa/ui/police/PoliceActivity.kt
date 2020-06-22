package com.dfa.ui.police

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.fragments.PhotoDialogFragment
import com.dfa.listeners.OnMarkStatusClickListener
import com.dfa.pojo.request.PoliceStatusRequest
import com.dfa.pojo.response.GetPoliceFormData
import com.dfa.pojo.response.PoliceFormData
import com.dfa.pojo.response.PoliceStatusResponse
import com.dfa.ui.police.adapter.NgoDetailsForPoliceAdapter
import com.dfa.ui.police.presenter.PolicePresenter
import com.dfa.ui.police.presenter.PolicePresenterImpl
import com.dfa.ui.police.view.PoliceView
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.activity_police.*
import java.lang.Exception


class PoliceActivity : BaseActivity(), PoliceView, OnMarkStatusClickListener {
    private lateinit var selectedStatus: String
    override fun showPoliceStatusResponse(response: PoliceStatusResponse) {
        dismissProgress()
        if (isInternetAvailable()) {
            showProgress()
            policePresenter.getPoliceDetailsRequest()
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }
    }

    private var mNgoDetailsList: List<PoliceFormData> = listOf()
    private lateinit var recyclerAdapter: NgoDetailsForPoliceAdapter
    private var policePresenter: PolicePresenter = PolicePresenterImpl(this)

    override fun getLayout(): Int {
        return R.layout.activity_police

    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.police)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        if (isInternetAvailable()) {
            showProgress()
            policePresenter.getPoliceDetailsRequest()
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }
        val layoutManager = LinearLayoutManager(this)
        rvPolice.layoutManager = layoutManager
        recyclerAdapter = NgoDetailsForPoliceAdapter(this, mNgoDetailsList, this)
        rvPolice.adapter = recyclerAdapter

    }

    /*This method used to set the recyclerView*/
    private fun setRecyclerView(detailsList: List<PoliceFormData>) {
        if (detailsList.isNotEmpty()) {
            recyclerAdapter.setData(detailsList)
        }
    }

    override fun handleKeyboard(): View {
        return policeParentLayout
    }

    override fun showPoliceDetailsResponse(response: GetPoliceFormData) {
        dismissProgress()
        if (response.data.isNotEmpty()) {
            rvPolice.visibility = View.VISIBLE
            mNgoDetailsList = response.data
            tvPoliceRecord.visibility = View.GONE
        } else {
            tvPoliceRecord.visibility = View.VISIBLE
        }
        setRecyclerView(mNgoDetailsList)
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun onMarkClick(forwardId: String, status: String, description: String) {
        showDialog(forwardId, status, description)
    }

    override fun onLocationClick(lat: String, lng: String) {

        val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lng")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    override fun onPhotoClick(complaintId: Int) {
        val manager = supportFragmentManager
        val alert = PhotoDialogFragment(complaintId)
        alert.show(manager, "jh")
    }

    override fun onDescPhotoClick(url: String?) {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)

        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.fragment_image_dialog_view,
            null,
            false
        ) as com.dfa.databinding.FragmentImageDialogViewBinding
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        val options = RequestOptions()
            .centerCrop()
            .placeholder(circularProgressDrawable)
            .error(R.drawable.noimage)
        try {
            Glide.with(this).load(url).apply(options).into(binding.imgForm)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        builder.setView(binding.root)



        dialog = builder.create()

        binding.imgCross.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        dialog.show()
    }

//    .setPositiveButton("OK", object : View.OnClickListener {
//        fun onClick(dialog: DialogInterface, which: Int) {
//            dialog.dismiss()
//        }
//    }).setView(image)

    private fun showDialog(forwardId: String, status: String, description: String) {

        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)

        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_change_status,
            null,
            false
        ) as com.dfa.databinding.DialogChangeStatusBinding
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog
        // layout
        if (!description.equals("null") && !description.equals("")) binding.etDescription.setText(
            description
        )
        when (status.toLowerCase()) {
            "authentic" -> binding.radioAuth.isChecked = true
            "unauthentic" -> binding.radioUnAuth.isChecked = true
            "resolved" -> binding.radioRes.isChecked = true
            "unresolved" -> binding.radioUnRes.isChecked = true


        }


        builder.setView(binding.root)
        dialog = builder.create()



        binding.btnDone.setOnClickListener(View.OnClickListener {

            var selectedId = binding.radioStatus.checkedRadioButtonId
            var selectedRadioButton = dialog.findViewById<RadioButton>(selectedId)
            selectedStatus = selectedRadioButton!!.text.toString().toLowerCase()
            if (isInternetAvailable()) {
                showProgress()
                val request = PoliceStatusRequest(
                    selectedStatus,
                    forwardId,
                    binding.etDescription.text.toString()

                )
                policePresenter.savePoliceStatusRequest(request)
                dialog.dismiss()
            } else {
                Utilities.showMessage(
                    this@PoliceActivity,
                    getString(R.string.no_internet_connection)
                )
            }
        })


        dialog.show()
    }


}
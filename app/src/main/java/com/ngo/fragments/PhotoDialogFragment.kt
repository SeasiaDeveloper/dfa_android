package com.ngo.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ngo.R
import com.ngo.fragments.presenter.FragmentPresenter
import com.ngo.fragments.presenter.FragmentPresenterImpl
import com.ngo.fragments.view.FragmentView
import com.ngo.pojo.response.GetPoliceFormResponse
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.fragment_image_dialog_view.*
import java.lang.Exception

class PhotoDialogFragment(private var complaint_id: Int) : DialogFragment(), FragmentView {

    private var policePresenter: FragmentPresenter = FragmentPresenterImpl(this)
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.fragment_image_dialog_view, container)
        /*if (dialog != null && dialog!!.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }*/
        return layoutView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgCross.setOnClickListener {
            dialog?.dismiss()
        }

      //  policePresenter.savePoliceFormRequest(complaint_id)

    }

    override fun showPoliceFormResponse(response: GetPoliceFormResponse) {
        if(response.status==404){
            Utilities.showMessage(mContext,"Image not found")
            dialog?.dismiss()
        }else {
           // imgForm.setImageDrawable(mContext.resources.getDrawable(R.drawable.common_full_open_on_phone))
         try{   Glide.with(mContext).load("http://stgsp.appsndevs.com:9041/Complaint/api/v1/showcomplaint/"+complaint_id).into(imgForm)}
         catch (e:Exception){e.printStackTrace()}
            //Picasso.with(context).load(response.url).into(imgForm)
        }
    }

    override fun showServerError(error: String) {
        dismiss()
        Utilities.showMessage(mContext, error)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}
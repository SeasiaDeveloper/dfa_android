package com.ngo.ui.emergency

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngo.R
import com.ngo.adapters.EmergencyDetailsAdapter
import com.ngo.pojo.response.GetEmergencyDetailsResponse.Details
import com.ngo.ui.generalpublic.view.GeneralPublicHomeFragment
import com.ngo.utils.PreferenceHandler
import kotlinx.android.synthetic.main.fragment_emergency.*

class EmergencyFragment : Fragment() {

    private lateinit var adapter: EmergencyDetailsAdapter
    private var emergencyDetailList = ArrayList<Details>()
    lateinit var mContext: Context
    private val RECORD_REQUEST_CODE = 101

    companion object {
        var change = 0
        var commentChange = 0
        var fromIncidentDetailScreen = 0
        var commentsCount = 0
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_emergency, container, false)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emergencyDetailList.clear()
        var details = Details()
        details.contact_number = "9485230836"
        details.name = "Fire Station"
        emergencyDetailList.add(details)
        details = Details()
        details.contact_number = "03602257220"
        details.name = "Police Station"
        emergencyDetailList.add(details)
        details = Details()
        details.contact_number = "03602257220"
        details.name = "Women Police Station"
        emergencyDetailList.add(details)
        details = Details()
        details.contact_number = "9436682337"
        details.name = "Child Helpline"
        emergencyDetailList.add(details)
        details = Details()
        details.contact_number = "7005698992"
        details.name = "Women Helpline"
        emergencyDetailList.add(details)
       /* details = Details()
        details.contact_number = "9530606006"
        details.name = "Akash"
        emergencyDetailList.add(details)*/

        val permission = ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.CALL_PHONE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            //Log.i(TAG, "Permission to record denied")
            // makeRequest(context)
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.CALL_PHONE),
                RECORD_REQUEST_CODE
            )
        }
        setEmergencyAdapter()
        getDistrictDropDown()
    }

    fun getDistrictDropDown() {

        val distValueList = ArrayList<String>()
        //  distValueList.add("Itanagar")
        var list_of_items = arrayOf("Itanagar")
        val distArray = distValueList.toArray(arrayOfNulls<String>(distValueList.size))
        val adapter = ArrayAdapter(
            mContext,
            android.R.layout.simple_spinner_item, list_of_items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDistrict.setAdapter(adapter);
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        //  token = PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")!!
        // type = PreferenceHandler.readString(mContext, PreferenceHandler.USER_ROLE, "")!!
    }

    private fun setEmergencyAdapter() {
        //val layoutManager = GridLayoutManager(activity!!, 4)
        adapter = EmergencyDetailsAdapter(activity!!, emergencyDetailList)
        val horizontalLayoutManager = LinearLayoutManager(
            mContext,
            RecyclerView.VERTICAL, false
        )
        rvEmergencies?.layoutManager = horizontalLayoutManager
        rvEmergencies?.adapter = adapter

        // rvEmergencies.adapter = adapter

    }

    override fun onPause() {
        super.onPause()
        GeneralPublicHomeFragment.change = 1
    }

    /* override fun onRequestPermissionsResult(
         requestCode: Int,
         permissions: Array<String>, grantResults: IntArray
     ) {
         when (requestCode) {
             RECORD_REQUEST_CODE -> {

                 if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                     Log.i("", "Permission has been denied by user")
                 } else {
                     Log.i("", "Permission has been granted by user")
                 }

             }
         }

     }﻿*/

    // Receive the permissions request result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                //val isPermissionsGranted = managePermissions.processPermissionsResult(requestCode, permissions, grantResults)

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // Do the task now
                    //toast("Permissions granted.")
                } else {
                    //toast("Permissions denied.")
                }
                return
            }
        }
    }

}
package com.dfa.ui.emergency.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dfa.R
import com.dfa.adapters.EmergencyDetailsAdapter
import com.dfa.adapters.PhoneNumberAdapter
import com.dfa.pojo.request.EmergencyDataRequest
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.EmergencyDataResponse
import com.dfa.ui.emergency.presenter.EmegencyFragmentPresenterImpl
import com.dfa.ui.emergency.presenter.EmergencyFragmentPresenter
import com.dfa.ui.generalpublic.view.GeneralPublicHomeFragment
import com.dfa.utils.InternetUtils
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import com.dfa.utils.Utilities.dismissProgress
import com.dfa.utils.Utilities.showProgress
import kotlinx.android.synthetic.main.fragment_emergency.*

class EmergencyFragment : Fragment(), EmergencyFragmentView {
    private var presenter: EmergencyFragmentPresenter = EmegencyFragmentPresenterImpl(this)
    private lateinit var emergencyDetailsAdapter: EmergencyDetailsAdapter
    private lateinit var phoneNumberAdapter: PhoneNumberAdapter
    private var emergencyDetailList = ArrayList<EmergencyDataResponse.Data>()
    lateinit var mContext: Context
    private val RECORD_REQUEST_CODE = 101
    private var isFirst: Boolean = true

    companion object {
        var change = 0
        var commentChange = 0
        var fromIncidentDetailScreen = 0
        var commentsCount = 0
        var staticDistValueList: DistResponse? = null
        var noChnage = false
    }

    override fun onResume() {
        super.onResume()
        if (isFirst) {
            var internetUtils = InternetUtils()
            if (internetUtils.isOnline(activity!!)) {
                showProgress(mContext)
                presenter.hitDistricApi()
                isFirst = false
            } else {
                Utilities.showMessage(mContext, getString(R.string.no_internet_connection))
            }
        } else {
            if (staticDistValueList?.data!!.size > 0 && noChnage==false) {
                getDistrictDropDown(staticDistValueList!!)
                emergencyDetailsAdapter.changeList(emergencyDetailList)

            }

        }
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
        /*   emergencyDetailList.clear()
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
        callApi()
        setEmergencyAdapter()

    }

    fun callApi()
    {


        var internetUtils = InternetUtils()
        if (internetUtils.isOnline(activity!!)) {
            showProgress(mContext)
            var authorizationToken =
                PreferenceHandler.readString(
                    mContext,
                    PreferenceHandler.AUTHORIZATION,
                    ""
                )
            val latitude = PreferenceHandler.readString(mContext, PreferenceHandler.LATITUDE, "").toString()
            val longitude = PreferenceHandler.readString(mContext, PreferenceHandler.LONGITUDE, "").toString()
            var request = EmergencyDataRequest("",latitude,longitude)

           if(latitude!="" && latitude!="null")
            presenter.hitEmergencyApi(request, authorizationToken)
        } else {
            Utilities.showMessage(
                mContext,
                getString(R.string.no_internet_connection)
            )
        }
    }
    fun getDistrictDropDown(response: DistResponse) {
        staticDistValueList = response
        val distValueList = ArrayList<String>()
        distValueList.add("Please select district")
        for (i in 0..response.data.size - 1) {
            distValueList.add(response.data.get(i).name)
        }

        // var list_of_items = arrayOf(distValueList)
        // val distArray = distValueList.toArray(arrayOfNulls<String>(distValueList.size))
        val adapter = ArrayAdapter(
            mContext,
            R.layout.view_spinner_item, distValueList
        )

        adapter.setDropDownViewResource(R.layout.view_spinner_item)
        spDistrict.setAdapter(adapter)

        try {


            spDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Display the selected item text on text view
                    "Spinner selected : ${parent.getItemAtPosition(position)}"
                    if (position != 0) {
                        var internetUtils = InternetUtils()
                        if (internetUtils.isOnline(activity!!)) {
                            showProgress(mContext)
                            var authorizationToken =
                                PreferenceHandler.readString(
                                    mContext,
                                    PreferenceHandler.AUTHORIZATION,
                                    ""
                                )
                            val latitude = PreferenceHandler.readString(mContext, PreferenceHandler.LATITUDE, "").toString()
                            val longitude = PreferenceHandler.readString(mContext, PreferenceHandler.LONGITUDE, "").toString()

                            var request = EmergencyDataRequest(response.data.get(position - 1).id,latitude,longitude)
                            presenter.hitEmergencyApi(request, authorizationToken)
                        } else {
                            Utilities.showMessage(
                                mContext,
                                getString(R.string.no_internet_connection)
                            )
                        }
                   }
//                    else {
////                        var mList: ArrayList<EmergencyDataResponse.Data> = ArrayList()
////                        emergencyDetailsAdapter.changeList(mList)
////                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Another interface callback
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        //  token = PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")!!
        // type = PreferenceHandler.readString(mContext, PreferenceHandler.USER_ROLE, "")!!
    }

    private fun setEmergencyAdapter() {
        emergencyDetailsAdapter = EmergencyDetailsAdapter(activity!!, emergencyDetailList)
        val horizontalLayoutManager = LinearLayoutManager(
            mContext,
            RecyclerView.VERTICAL, false
        )
        rvEmergencies?.layoutManager = horizontalLayoutManager
        rvEmergencies?.adapter = emergencyDetailsAdapter
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
     }*/

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

    override fun getEmergencyDataSuccess(myEarningsResponse: EmergencyDataResponse) {
        dismissProgress()
        if (myEarningsResponse.data!!.size > 0 ) {
            if(isFirst) emergencyDetailList=myEarningsResponse.data
            emergencyDetailsAdapter.changeList(myEarningsResponse.data!!)
        } else {
            emergencyDetailsAdapter.changeList(myEarningsResponse.data!!)
           if(isFirst==false) Utilities.showMessage(mContext, "No data found corresponding to the selected District.")
        }

    }


    override fun getEmergencyDataFailure(error: String) {
        dismissProgress()
        Utilities.showMessage(mContext, error)
    }

    override fun getDistrictsSuccess(response: DistResponse) {
        dismissProgress()
        getDistrictDropDown(response)
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(mContext, error)
    }

    /*
 * method to check internet connection
 * */
    /* fun isInternetAvailable(): Boolean {
         val connectivityManager =
             activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             val nw = connectivityManager.activeNetwork ?: return false
             val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
             return when {
                 actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                 actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                 else -> false
             }
         } else {
             val nwInfo = connectivityManager.activeNetworkInfo ?: return false
             return nwInfo.isConnected
         }
     }*/

}
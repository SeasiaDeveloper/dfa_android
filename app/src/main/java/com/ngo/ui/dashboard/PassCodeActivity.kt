package com.ngo.ui.dashboard

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.ui.generalpublic.view.GeneralPublicHomeFragment
import com.ngo.ui.ngo.NGOActivity
import com.ngo.ui.police.PoliceActivity
import com.ngo.utils.Constants
import com.ngo.utils.GpsUtils
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_passcode.*


class PassCodeActivity : BaseActivity(), View.OnClickListener {
    private lateinit var locationManager: LocationManager
    private  var longitude: String=""
    private  var lattitude: String=""
    private var isGPS: Boolean = false

    override fun getLayout(): Int {
        return R.layout.activity_passcode
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.passcode)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        btnSubmit.setOnClickListener(this)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        Utilities.requestPermissions(this)
    }

    override fun handleKeyboard(): View {
        return dashboardParentLayout
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.btnSubmit -> {
                if (pinview.value.isEmpty() || pinview.value.equals("")) {

                    Utilities.showAlert(
                        this,
                        getString(R.string.enter_Code)
                    )
                } else {
                    if (pinview.value.equals("1111")) {
                        var intent =
                            Intent(this@PassCodeActivity, GeneralPublicHomeFragment::class.java)
                        startActivity(intent)
                        pinview.clearValue()

                        // finish()
                    } else if (pinview.value.equals("2222")) {
                        intent = Intent(this@PassCodeActivity, NGOActivity::class.java)

                        startActivity(intent)
                        pinview.clearValue()

                        // finish()
                    } else if (pinview.value.equals("3333")) {
                        intent = Intent(this@PassCodeActivity, PoliceActivity::class.java)

                        startActivity(intent)
                        pinview.clearValue()

                        // finish()
                    } else {
                        Utilities.showAlert(
                            this,
                            getString(R.string.invalid_code)
                        )
                    }
                    /*if (!pinview.value.equals("1111")) Utilities.showAlert(
                                this,
                                getString(R.string.invalid_code)
                            )
                            else {
                                var intent = Intent(this@PassCodeActivity, GeneralPublicHomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                      //  }

                      //  "2" -> {
                            if (!pinview.value.equals("2222")) Utilities.showAlert(
                                this,
                                getString(R.string.invalid_code)
                            )
                            else {
                                intent = Intent(this@PassCodeActivity, NGOActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                    //    }
                     //   "3" -> {
                            if (!pinview.value.equals("3333"))

                                Utilities.showAlert(
                                    this,
                                    getString(R.string.invalid_code)
                                )
                            else {
                                intent = Intent(this@PassCodeActivity, PoliceActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        //}


                    }


                }
            }*/
                }
            }
        }
    }



    private fun getLocation() {
        if(!Utilities.checkPermissions(this))
            Utilities.requestPermissions(this)
        else
            try {
                // Request location updates
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, mLocationListener)
            } catch(ex: SecurityException) {
                Log.d("myTag", "Security Exception, no location available")
            }
    }

    private val mLocationListener = object: LocationListener {
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }

        override fun onLocationChanged(location: Location) {
            //your code here
            if (location != null)
            {
                val latti = location.latitude
                val longi = location.longitude
                lattitude = (latti).toString()
                longitude = (longi).toString()
                PreferenceHandler.writeString(this@PassCodeActivity, PreferenceHandler.LAT,lattitude)
                PreferenceHandler.writeString(this@PassCodeActivity, PreferenceHandler.LNG,longitude)
            }
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (requestCode == Utilities.PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Utilities.requestPermissions(this)
            else
                askForGPS()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.GPS_REQUEST) {
            isGPS = true
            getLocation()
        }

    }

    private fun askForGPS() {
        GpsUtils(this).turnGPSOn(object: GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable:Boolean) {
                // turn on GPS
                isGPS = isGPSEnable
                /* if(!isGPS)
            askForGPS()*/
                if (isGPS)
                    getLocation()
            }
        })
    }






}
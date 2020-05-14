package com.ngo.ui.contactus

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.utils.PreferenceHandler

import kotlinx.android.synthetic.main.image_video_layout.toolbarLayout
import kotlinx.android.synthetic.main.activity_contactus.*

class ContactUsActivity : BaseActivity(){

    override fun getLayout(): Int {
        return R.layout.activity_contactus
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.contact_us)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }

        etMobile1.setText(PreferenceHandler.readString(this, PreferenceHandler.NGO_CONTACT_NO, "6009121346"))
        etName.setText(PreferenceHandler.readString(this, PreferenceHandler.NGO_NAME, "Drug Free Arunachal"))
        etAddressLine1.setText(PreferenceHandler.readString(this, PreferenceHandler.NGO_ADDRESS, "Yupia Market, Yupia"))
        etDist.setText(PreferenceHandler.readString(this, PreferenceHandler.NGO_DIST, "Papum Pare"))
        etState.setText( PreferenceHandler.readString(this, PreferenceHandler.NGO_STATE, "Arunachal Pradesh"))
        etPinCode.setText(PreferenceHandler.readString(this, PreferenceHandler.NGO_PIN, "791112"))
        etEmail.setText(PreferenceHandler.readString(this, PreferenceHandler.NGO_EMAIL, "dfa.contact@gmail.com"))

        val lat =(PreferenceHandler.readString(this, PreferenceHandler.NGO_LATITUDE, "27.144675"))!!.toDouble()
        val lng = PreferenceHandler.readString(this, PreferenceHandler.NGO_LONGITUDE, "93.727255")!!.toDouble()

        btnLocate.setOnClickListener {
            val mTitle  = "DFA"
            val geoUri = "http://maps.google.com/maps?q=loc:$lat,$lng ($mTitle)"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
            startActivity(intent)
        }
    }

    override fun handleKeyboard(): View {
        return contactusLayout
    }

}
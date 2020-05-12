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

            var contactNumber = PreferenceHandler.readString(this, PreferenceHandler.CONTACT_NUMBER, "")
            var authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")

        btnLocate.setOnClickListener {
            val lat = 27.144675 //30.7106607
            val lng = 93.727255 //76.7091493
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
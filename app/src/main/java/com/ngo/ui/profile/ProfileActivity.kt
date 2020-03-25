package com.ngo.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ngo.R
import com.ngo.ui.profile.presenter.ProfilePresenter
import com.ngo.ui.profile.presenter.ProfilePresenterImplClass
import com.ngo.ui.profile.view.ProfileView

class ProfileActivity : AppCompatActivity(),ProfileView {
    override fun showServerError(error: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var profilePresenter:ProfilePresenter = ProfilePresenterImplClass(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }
}

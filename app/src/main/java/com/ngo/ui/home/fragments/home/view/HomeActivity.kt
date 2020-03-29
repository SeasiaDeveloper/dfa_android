package com.ngo.ui.home.fragments.home.view

import android.app.AlertDialog
import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import com.ngo.R
import com.ngo.adapters.TabLayoutAdapter
import com.ngo.base.BaseActivity
import com.ngo.pojo.response.GetProfileResponse
import com.ngo.ui.generalpublic.view.GeneralPublicHomeFragment
import com.ngo.ui.home.fragments.cases.CasesFragment
import com.ngo.ui.home.fragments.photos.view.PhotosFragment
import com.ngo.ui.home.fragments.videos.view.VideosFragment
import com.ngo.ui.home.fragments.home.presenter.HomePresenter
import com.ngo.ui.home.fragments.home.presenter.HomePresenterImpl
import com.ngo.ui.mycases.MyCasesActivity
import com.ngo.ui.profile.ProfileActivity
import com.ngo.ui.termsConditions.view.TermsConditionsFragment
import com.ngo.ui.updatepassword.view.UpdatePasswordActivity
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import com.ngo.utils.alert.AlertDialogPolice
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.nav_header.*


class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, HomeView {
    private var mDrawerLayout: DrawerLayout? = null
    private var mToggle: ActionBarDrawerToggle? = null
    private var mToolbar: Toolbar? = null
    private var homePresenter: HomePresenter = HomePresenterImpl(this)
    private var authorizationToken: String? = null
    private var preferencesHelper: PreferenceHandler? = null

    override fun getLayout(): Int {
        return R.layout.home_activity
    }

    override fun onResume() {
        super.onResume()
        authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        homePresenter.hitProfileApi(authorizationToken)
    }

    override fun setupUI() {
        mToolbar = findViewById<View>(R.id.nav_action) as Toolbar
        setSupportActionBar(mToolbar)

        mDrawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout
        mToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
        mDrawerLayout!!.addDrawerListener(mToggle!!)
        mToggle!!.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayShowTitleEnabled(false)
        }

        val adapter = TabLayoutAdapter(supportFragmentManager)
        adapter.addFragment(GeneralPublicHomeFragment(), "Home")
        adapter.addFragment(CasesFragment(), "Cases")
        adapter.addFragment(PhotosFragment(), "Photo(s)")
        adapter.addFragment(VideosFragment(), "Video(s)")
        viewPager?.adapter = adapter
        tabs.setupWithViewPager(viewPager)
        nav_view?.setNavigationItemSelectedListener(this)
    }

    private fun loadNavHeader(getProfileResponse: GetProfileResponse) { // name, wegbsite
        textName.setText(getProfileResponse.data?.first_name + " " + getProfileResponse.data?.middle_name + " " + getProfileResponse.data?.last_name)
        textAddress.setText(getProfileResponse.data?.address_1)
        if (getProfileResponse.data?.profile_pic != null) {
            Glide.with(this).load(getProfileResponse.data?.profile_pic)
                /*  .crossFade()
                  .thumbnail(0.5f)
                  .bitmapTransform(CircleTransform(this))
                  .diskCacheStrategy(DiskCacheStrategy.ALL)*/
                .into(imageNavigator)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_edit_profile -> {
                var intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_password -> {
                var intent = Intent(this, UpdatePasswordActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {

            }
            R.id.nav_cases -> startActivity(Intent(this@HomeActivity, MyCasesActivity::class.java))
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun handleKeyboard(): View {
        return drawerLayout
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (mToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onGetProfileSucess(getProfileResponse: GetProfileResponse) {
        dismissProgress()
        loadNavHeader(getProfileResponse)
        var gson = getProfileResponse.data
        PreferenceHandler.writeString(this, PreferenceHandler.PROFILE_JSON, gson.toString())

        val jsonString = GsonBuilder().create().toJson(getProfileResponse)
        //Save that String in SharedPreferences
        PreferenceHandler.writeString(this, PreferenceHandler.PROFILE_JSON, jsonString)
        PreferenceHandler.writeString(
            this,
            PreferenceHandler.USER_ID,
            getProfileResponse.data?.id.toString()
        )
    }


    override fun ongetProfileFailure(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun onShowError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }
}
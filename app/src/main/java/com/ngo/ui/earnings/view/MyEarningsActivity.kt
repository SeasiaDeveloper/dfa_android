package com.ngo.ui.earnings.view


import android.graphics.Color
import android.view.View
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.response.MyEarningsResponse
import com.ngo.ui.earnings.presenter.MyEarningsPresenter
import com.ngo.ui.earnings.presenter.MyEarningsPresenterImpl
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.image_video_layout.toolbarLayout
import kotlinx.android.synthetic.main.myearnings_layout.*

class MyEarningsActivity : BaseActivity(), MyEarningsView {
    private var presenter: MyEarningsPresenter = MyEarningsPresenterImpl(this)

    override fun getLayout(): Int {
        return R.layout.myearnings_layout
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.my_earnings)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }

        if (isInternetAvailable()) {
            showProgress()
            var contactNumber =
                PreferenceHandler.readString(this, PreferenceHandler.CONTACT_NUMBER, "")
            var authorizationToken =
                PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
            presenter.hitMyEarningsApi(contactNumber, authorizationToken)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }

    }

    override fun handleKeyboard(): View {
        return myearningslayout
    }

    override fun myEarningssuccess(myEarningsResponse: MyEarningsResponse) {
        dismissProgress()
        my_earnings.setText(myEarningsResponse.data?.earning)
    }

    override fun myEarningFailure(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

}
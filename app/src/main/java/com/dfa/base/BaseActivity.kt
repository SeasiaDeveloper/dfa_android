package com.dfa.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.kaopiz.kprogresshud.KProgressHUD
import com.dfa.utils.Utilities


abstract class BaseActivity:AppCompatActivity() {
    private lateinit var customDialog: KProgressHUD
    protected var viewDataBinding : ViewDataBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding = DataBindingUtil.setContentView(this, getLayout())

       // setContentView(getLayout())
        hideKeyboard(handleKeyboard())
        setupUI()
    }

    protected abstract fun getLayout(): Int

    protected abstract fun setupUI()

    protected abstract fun handleKeyboard(): View

    private fun hideKeyboard(view: View) {
        view.setOnClickListener {
            Utilities.hideKeyboard(this@BaseActivity)
        }
    }


    /*
   * method to check internet connection
   * */
    fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
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
    }

    /*
     * method to show show progress
     * */
    fun showProgress() {
        dismissProgress()
        customDialog = KProgressHUD(this)
        customDialog
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.20f)
            .show()
    }

    /**
     * Dismiss the progress dialog
     */
     fun dismissProgress() {
        try {
            if (customDialog.isShowing) {
                customDialog.dismiss()
            }
        } catch (e: Exception) {
            Log.e("", e.message!!)
        }
    }

}
package com.ngo.ui.dashboard


import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.databinding.ActivityDashboardBinding
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity:BaseActivity(), View.OnClickListener {

    override fun getLayout(): Int {
        return R.layout.activity_dashboard
    }

    override fun setupUI() {
        viewDataBinding as ActivityDashboardBinding
        (toolbarLayout as CenteredToolbar).title = getString(R.string.dashboard)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        tvGeneralPublic.setOnClickListener(this)
        tvNGO.setOnClickListener(this)
        tvPolice.setOnClickListener(this)
    }

    override fun handleKeyboard(): View {
        return dashboardParentLayout
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvGeneralPublic -> {
               val intent=Intent(this@DashboardActivity,PassCodeActivity::class.java)
                intent.putExtra("user_type","1")
                startActivity(intent)
            }
            R.id.tvNGO->{
                val intent= Intent(this@DashboardActivity, PassCodeActivity::class.java)
                intent.putExtra("user_type","2")
                startActivity(intent)

            }
            R.id.tvPolice->{
                val intent=Intent(this@DashboardActivity,PassCodeActivity::class.java)
                intent.putExtra("user_type","3")
                startActivity(intent)
            }

        }
    }
}
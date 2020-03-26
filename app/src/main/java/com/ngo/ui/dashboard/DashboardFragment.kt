package com.ngo.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ngo.R
import com.ngo.customviews.CenteredToolbar
import com.ngo.databinding.FragmentDashboardBinding
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment:Fragment(), View.OnClickListener {

    lateinit var binding: FragmentDashboardBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    fun setupUI() {
        binding as FragmentDashboardBinding
        (toolbarLayout as CenteredToolbar).title = getString(R.string.dashboard)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        tvGeneralPublic.setOnClickListener(this)
        tvNGO.setOnClickListener(this)
        tvPolice.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvGeneralPublic -> {
               val intent=Intent(activity,PassCodeActivity::class.java)
                intent.putExtra("user_type","1")
                startActivity(intent)
            }
            R.id.tvNGO->{
                val intent= Intent(activity, PassCodeActivity::class.java)
                intent.putExtra("user_type","2")
                startActivity(intent)

            }
            R.id.tvPolice->{
                val intent=Intent(activity,PassCodeActivity::class.java)
                intent.putExtra("user_type","3")
                startActivity(intent)
            }

        }
    }
}
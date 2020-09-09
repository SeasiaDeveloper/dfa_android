package com.dfa.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.dfa.R
import com.dfa.databinding.FragmentHomeBinding
import com.dfa.pojo.response.AdvertisementInput
import com.dfa.pojo.response.AdvertisementResponse
import com.dfa.ui.generalpublic.view.GeneralPublicHomeFragment
import com.dfa.ui.home.fragments.home.presenter.AdvertisementCallback
import com.dfa.ui.home.fragments.home.presenter.AdvertisementHomePresenter
import com.dfa.ui.home.fragments.home.presenter.AdvertisementPresenter
import com.dfa.utils.Utilities

class HomeFragment : Fragment(), View.OnClickListener, AdvertisementCallback {
    var binding: FragmentHomeBinding? = null
    var slide = "0"
    var persenter:AdvertisementHomePresenter?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding!!.swipeView.setOnClickListener(this)
        callPresenter()
        replaceFragment(GeneralPublicHomeFragment())
        return binding!!.root

        // inflater.inflate(R.layout.fragment_home, container, false)

    }


//    fun viewPAger(){
//        val adapter = HomeTabLayoutAdapter(childFragmentManager)
//        adapter.addFragment(GeneralPublicHomeFragment(), "HomeSub")
//        adapter.addFragment(AdvertiseMentFragment(), "Advertisement")
//        viewPager?.adapter = adapter
//        tabs.setupWithViewPager(viewPager)
//    }


    fun callPresenter() {
        var input= AdvertisementInput()
        input.per_page="1"
        input.page="1"
        Utilities.showProgress(this.activity!!)
        persenter= AdvertisementHomePresenter(this)
        persenter!!.getAdvertisement(input)
    }


    fun replaceFragment(generalPublicHomeFragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        if (slide.equals("1")) {
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_right_in, R.anim.slide_left_out,
                R.anim.slide_left_in, R.anim.slide_right_out
            );

        } else if (slide.equals("2")) {
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_left_in, R.anim.slide_right_out,
                R.anim.slide_left_in, R.anim.slide_right_out
            );
        }
        fragmentTransaction.replace(R.id.details, generalPublicHomeFragment, "NewFragmentTag")
        fragmentTransaction.commit()
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.swipeView -> {
                if (binding!!.swipeView.text.equals("Recent Reported Incidents")) {
                    slide = "1"
                    replaceFragment(AdvertiseMentFragment())
                    binding!!.swipeView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_left,0,0,0);
                    binding!!.swipeView.text = "Advertisements"

                } else if(binding!!.swipeView.text.equals("Advertisements")) {
                    slide = "2"
                    binding!!.swipeView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_right,0);

                    binding!!.swipeView.text = "Recent Reported Incidents"
                    replaceFragment(GeneralPublicHomeFragment())
                }
            }
        }
    }

    override fun advertisementSuccess(responseObject: AdvertisementResponse) {
        Utilities.dismissProgress()
        if(responseObject.data!!.size!=null){
            if(responseObject!!.data!!.size==0){
                binding!!.swipeView.isEnabled=false
                binding!!.swipeView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
        }
    }

    override fun failer(serverError: String) {
        Utilities.dismissProgress()
    }


}





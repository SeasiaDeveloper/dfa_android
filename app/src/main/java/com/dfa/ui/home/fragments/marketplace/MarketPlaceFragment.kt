package com.dfa.ui.home.fragments.marketplace

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dfa.R
import com.dfa.adapters.MarketPlaceAdapter
import com.dfa.adapters.PhoneAdapter
import com.dfa.databinding.FragmentMarketPlaceBinding
import com.dfa.pojo.request.DeleteBusinessInput
import com.dfa.pojo.request.MarketPlaceInput
import com.dfa.pojo.response.MarketPlaceResponse
import com.dfa.ui.generalpublic.pagination.EndlessRecyclerViewScrollListenerImplementation
import com.dfa.utils.CheckRuntimePermissions
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities


class MarketPlaceFragment : Fragment(), MarketCallbacks,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    var binding: FragmentMarketPlaceBinding? = null
    var persenter: MarketPlacePresenter? = null
    var adapter: MarketPlaceAdapter? = null
    var marketList: ArrayList<MarketPlaceResponse.Data>? = null
    var endlessScrollListener: EndlessRecyclerViewScrollListenerImplementation? = null
    private var textChanged: Boolean = false
    var searchText = ""
    var filterValue = "product"
    var pw: PopupWindow? = null
    var radioGroup: RadioGroup? = null
    var layoutInflate: LayoutInflater? = null
    var latitude = ""
    var longitude = ""
    var pos: Int? = null
    var perPage = "10"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_market_place, container, false)

        layoutInflate =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var views = inflater!!.inflate(R.layout.filter_dialog_market_place, null, false)
        pw = PopupWindow(
            views,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        pw!!.setOutsideTouchable(true);
        var role = PreferenceHandler.readString(activity!!, PreferenceHandler.USER_ROLE, "0")!!
        if (role.equals("4")) {
            binding!!.btnAddBusiness.visibility=View.VISIBLE
        }

        radioGroup = views.findViewById<RadioGroup>(R.id.groupradio)

        marketList = ArrayList()
        setAdapter()
        searchText()
        latitude = PreferenceHandler.readString(this.activity!!, PreferenceHandler.LATITUDE, "")!!
        longitude = PreferenceHandler.readString(this.activity!!, PreferenceHandler.LONGITUDE, "")!!

        callApi(searchText, perPage)

        binding!!.ivFilter.setOnClickListener {
            openFilterDialog(binding!!.ivFilter)
        }


        binding!!.btnAdd.setOnClickListener {
            var intent = Intent(activity, AddBusinessActivity::class.java)
            startActivityForResult(intent, 12)
        }

        return binding!!.root
    }

    fun searchText() {
        binding!!.etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                endlessScrollListener?.resetState()
                //adapter!!.clearAdapter()
                if (binding!!.etSearch.text.toString().length != 0) {
                    textChanged = true
                    searchText = binding!!.etSearch.text.toString()
                    callApi(searchText, perPage)
                }
                return true
            }
        })

        binding!!.clickSearchIcon.setOnClickListener {
            if (binding!!.etSearch.text.toString().length != 0) {
                textChanged = true
                searchText = binding!!.etSearch.text.toString()
                callApi(searchText, perPage)
            }
        }


        binding!!.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (/*s.length >= 3 ||*/ s.length == 0) {
                    searchText = ""
                    if (textChanged) {
                        textChanged = false
                        //send search empty
                        callApi(searchText, perPage)

                    }

                }

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

    }

    var dialog: Dialog? = null

    fun openDialog(numberList: ArrayList<String>) {
        dialog = Dialog(activity!!) // Context, this, etc.
        dialog!!.setContentView(R.layout.phone_call_dialog)
        //  dialog.setTitle(R.string.dialog_title)

        var rvRecyclerView = dialog!!.findViewById<RecyclerView>(R.id.rvPhone)

        val layoutManager = LinearLayoutManager(activity)
        rvRecyclerView.setLayoutManager(layoutManager)
        var phomeAdapter = PhoneAdapter(this@MarketPlaceFragment, numberList!!)
        rvRecyclerView.adapter = phomeAdapter
        dialog!!.show()

    }

    private val REQUEST_PERMISSIONS = 1
    val PERMISSION_READ_STORAGE = arrayOf(Manifest.permission.CALL_PHONE)
    fun call(number: String) {
        dialog!!.dismiss()

        if (CheckRuntimePermissions.checkMashMallowPermissions(
                this.activity!! as AppCompatActivity?,
                PERMISSION_READ_STORAGE, REQUEST_PERMISSIONS
            )
        ) {
            val callIntent =
                Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + number)
            startActivity(callIntent)
        }
    }


    fun openFilterDialog(view: View?) {


        radioGroup!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btnProduct -> {
                    filterValue = "product"

                }
                R.id.btnCategory -> {
                    filterValue = "category"
                }

                R.id.btnBusinessName -> {
                    filterValue = "business_name"
                }
                R.id.btnShopId -> {
                    filterValue = "shop_id"
                }
            }
        })

        pw!!.showAsDropDown(view)
    }

    fun callApi(searchText: String, perPage: String) {
        Utilities.showProgress(activity!!)
        persenter = MarketPlacePresenter(this)
        val input = MarketPlaceInput()
        input.latitude = latitude
        input.longitude = longitude
        input.search_type = filterValue
        input.search = searchText
        input.per_page = perPage
        input.page = "1"
        persenter!!.getMarketPlaceData(input)
    }

    override fun onSuccess(responseObject: MarketPlaceResponse) {
        marketList = ArrayList()
        marketList!!.addAll(responseObject.data!!)
        adapter!!.setData(marketList!!)
        Utilities.dismissProgress()

        if (responseObject.data!!.size > 0) {
            binding!!.tvNoRecordFound.visibility = View.GONE
        } else {
            binding!!.tvNoRecordFound.visibility = View.VISIBLE

        }
    }

    override fun onDeleteBusiness(data: String) {
        Utilities.showMessage(this.activity!!, data)
        Utilities.dismissProgress()
        if (pos != null) {
            marketList!!.removeAt(pos!!)
            adapter!!.notifyDataSetChanged()

        }
    }

    override fun onFailer(s: String) {
        Utilities.dismissProgress()
    }

    fun setAdapter() {
        val layoutManager = LinearLayoutManager(activity)
        binding!!.rvMarket.setLayoutManager(layoutManager)
        adapter = MarketPlaceAdapter(this@MarketPlaceFragment, marketList!!)
        binding!!.rvMarket.adapter = adapter

        if (endlessScrollListener == null)
            endlessScrollListener =
                EndlessRecyclerViewScrollListenerImplementation(layoutManager, this)
        else
            endlessScrollListener?.setmLayoutManager(layoutManager)
        binding!!.rvMarket.addOnScrollListener(endlessScrollListener!!)
        endlessScrollListener?.resetState()

    }

    fun fullImageDisplaying(bannerPath: String?) {
      var  fullSizeImage = Dialog(activity!!)
        fullSizeImage!!.setContentView(R.layout.full_size_image)
        var image=fullSizeImage!!.findViewById<ImageView>(R.id.fullImage)
        Glide.with(activity!!).load(bannerPath)
            .placeholder(R.drawable.img_placeholder).into(image)
        fullSizeImage!!.show()
    }

    fun deleteBusiness(id: String?, position: Int) {
        val input = DeleteBusinessInput()
        input.businessId = id

        var builder = AlertDialog.Builder(activity!!);
        builder.setTitle("Delete Business");
        builder.setMessage("Would you like to delete your business?")
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, id ->
                dialog.cancel()
                pos = position
                Utilities.showProgress(activity!!)
                persenter!!.deleteBusiness(input)


            }
            .setNegativeButton("Cancel") { dialog, id -> //  Action for 'NO' Button
                dialog.cancel()

            }

        val alert = builder.create()
        alert.show()
    }


    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        if(5<=totalItemsCount){
            perPage=perPage+10
            Utilities.showProgress(activity!!)
            callApi(searchText, perPage)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12) {
            if (data != null) {
                if (data.getStringExtra("data")!!.equals("12")) {
                    marketList = ArrayList()
                    endlessScrollListener?.resetState()
                    callApi(searchText, perPage)
                }
            }

        }
    }

}
package com.dfa.ui.home.fragments.marketplace

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.application.MyApplication
import com.dfa.pojo.request.AddBusinessInput
import com.dfa.pojo.request.DeleteBusinessInput
import com.dfa.pojo.response.AddBusinessResponse
import com.dfa.pojo.response.CategoriesListResponse
import com.dfa.utils.PreferenceHandler
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.SocketTimeoutException

class AddBusinessPresenter(var addBusinessActivity: AddBusinessActivity) {
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun addBusiness(input: AddBusinessInput) {

        val token = PreferenceHandler.readString(MyApplication.instance, PreferenceHandler.AUTHORIZATION, "")
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["name"] = toRequestBody(input.name.toString())
        map["category_id"] = toRequestBody(input.category_id.toString())
        map["address"] = toRequestBody(input.address.toString())
        map["pincode"] = toRequestBody(input.pincode.toString())
        map["contact_person"] = toRequestBody(input.contact_person.toString())
        map["mobile"] = toRequestBody(input.mobile.toString())
        map["latitude"] = toRequestBody(input.latitude.toString())
        map["longitude"] = toRequestBody(input.longitude.toString())
        map["product"] = toRequestBody(input.product.toString())
        map["businessId"] = toRequestBody(input.businessId.toString())
        var parts = arrayOfNulls<MultipartBody.Part>(1)

        if(!input.business_pics.toString().isEmpty()){
            val file = File(input.business_pics.toString())
            val surveyBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
            parts[0] = MultipartBody.Part.createFormData("business_pics[]", file.name, surveyBody)
        } else{
            val surveyBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), "")
            parts[0] = MultipartBody.Part.createFormData("business_pics[]","", surveyBody)

        }


        retrofitApi.addBusiness(token, map, parts).enqueue(object : Callback<AddBusinessResponse> {

            override fun onResponse(
                call: Call<AddBusinessResponse>,
                response: Response<AddBusinessResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if(responseObject.code==200){
                        addBusinessActivity.onSuccess(responseObject.message)
                    } else{
                        addBusinessActivity.onError(responseObject!!.message!!)
                    }

                } else {
                    addBusinessActivity.onError("Somthing went wrong")
                }
            }

            override fun onFailure(call: Call<AddBusinessResponse>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    addBusinessActivity.onError("Socket Time error")
                } else {
                    addBusinessActivity.onError(t.message + "")
                }
            }

        })
    }



    fun deleteBusiness() {
        val token =
            PreferenceHandler.readString(
                MyApplication.instance,
                PreferenceHandler.AUTHORIZATION,
                ""
            )
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getBusinessCategory().enqueue(object :
            Callback<CategoriesListResponse> {

            override fun onResponse(
                call: Call<CategoriesListResponse>,
                response: Response<CategoriesListResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code==200) {
                        addBusinessActivity.getCategories(responseObject)
                    } else {
                        addBusinessActivity.onError("Somthing went wrong")
                    }
                } else {
                    addBusinessActivity.onError("Server Error")
                }
            }

            override fun onFailure(call: Call<CategoriesListResponse>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    addBusinessActivity.onError("Socket Time error")
                } else {
                    addBusinessActivity.onError(t.message + "")
                }
            }
        })
    }
}
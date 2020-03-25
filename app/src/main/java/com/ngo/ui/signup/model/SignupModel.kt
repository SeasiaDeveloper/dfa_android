package com.ngo.ui.signup.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.SignupRequest
import com.ngo.pojo.response.DistResponse
import com.ngo.pojo.response.SignupResponse
import com.ngo.ui.signup.presenter.SignupPresenterImplClass
import com.ngo.utils.Constants
import retrofit2.Callback
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class SignupModel(var signupPresenterImplClass: SignupPresenterImplClass) {

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    fun setValidation(signupRequest: SignupRequest) {
        if (!signupRequest.mobile.isEmpty()) {
            signupPresenterImplClass.onValidationSuccess(signupRequest)
        }
    }

    //hit api to register the user
    fun userRegisteration(signupRequest: SignupRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["username"] = toRequestBody(signupRequest.username)
        map["email"] = toRequestBody(signupRequest.email)
        map["password"] = toRequestBody(signupRequest.password)
        map["first_name"] = toRequestBody(signupRequest.first_name)
        map["last_name"] = toRequestBody(signupRequest.last_name)
        map["middle_name"] = toRequestBody(signupRequest.middle_name)
        map["address_1"] = toRequestBody(signupRequest.address_1)
        map["address_2"] = toRequestBody(signupRequest.address_2)
        map["mobile"] = toRequestBody(signupRequest.mobile) //mobile is mobile_2
        map["adhar_number"] = toRequestBody(signupRequest.adhar_number)
        map["district_id"] = toRequestBody(signupRequest.district_id)
        map["pin_code"] = toRequestBody(signupRequest.pin_code)

        //profile_img
        val file = File(signupRequest.profile_pic)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        // MultipartBody.Part is used to send also the actual filename
        val profileImg = MultipartBody.Part.createFormData("image", file.getName(), requestFile)

        retrofitApi.registerUser(map,profileImg).enqueue(object : Callback<SignupResponse> {
            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                signupPresenterImplClass.showError(t.message + "")
            }

            override fun onResponse(
                call: Call<SignupResponse>,
                response: Response<SignupResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        signupPresenterImplClass.onSaveDetailsSuccess(responseObject)
                    } else {
                        signupPresenterImplClass.onSaveDetailsFailed(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    signupPresenterImplClass.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }

    fun getDist() {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getDist().enqueue(object : Callback<DistResponse>{
            override fun onFailure(call: Call<DistResponse>, t: Throwable) {
                signupPresenterImplClass.showError(t.message + "")
            }

            override fun onResponse(call: Call<DistResponse>, response: Response<DistResponse>) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        signupPresenterImplClass.fetchDistList(responseObject)
                    } else {
                        signupPresenterImplClass.onSaveDetailsFailed(
                            Constants.SERVER_ERROR
                        )
                    }
                } else {
                    signupPresenterImplClass.showError(Constants.SERVER_ERROR)
                }
            }


        })
    }

}
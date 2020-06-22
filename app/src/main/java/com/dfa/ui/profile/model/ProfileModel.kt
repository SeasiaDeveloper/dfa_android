package com.dfa.ui.profile.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.request.SignupRequest
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.GetProfileResponse
import com.dfa.pojo.response.SignupResponse
import com.dfa.ui.profile.presenter.ProfilePresenterImplClass
import com.dfa.utils.Constants
import com.dfa.utils.Utilities
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.SocketTimeoutException

class ProfileModel(private var profilePresenterImplClass: ProfilePresenterImplClass) {

    private val imgMediaType = "image/*"

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value)
    }

    fun getDist() {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getDist().enqueue(object : Callback<DistResponse> {
            override fun onFailure(call: Call<DistResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    profilePresenterImplClass.showError("Socket Time error")
                }else{
                    profilePresenterImplClass.showError(t.message + "")
                }
            }

            override fun onResponse(call: Call<DistResponse>, response: Response<DistResponse>) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        profilePresenterImplClass.fetchDistList(responseObject)
                    } else {
                        profilePresenterImplClass.showError(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    profilePresenterImplClass.showError(Constants.SERVER_ERROR)
                }
            }


        })
    }

    fun setValidation(request: SignupRequest, isAdhaarNoAdded: Boolean) {
        if (request.username.isEmpty()) {
            profilePresenterImplClass.usernameEmptyValidation()
            return
        } else if (!(Utilities.isValidMobile(request.username))) {
            profilePresenterImplClass.usernameValidationFailure()
            return
        } else if (request.first_name.trim().isEmpty()) {
            profilePresenterImplClass.firstNameValidationFailure()
            return
        }

        else if (request.first_name.trim().length<3) {
            profilePresenterImplClass.firstNameLengthFailure()
            return
        }
        else if (!Utilities.isAlphabets(request.first_name.trim())) {
            profilePresenterImplClass.firstNameAlphabetFailure()
            return
        } else if (request.middle_name.trim().isNotEmpty() && request.middle_name.trim().length<3 ) {
            profilePresenterImplClass.middleNameLengthFailure()
            return
        }
        else if (request.middle_name.trim().isNotEmpty() && !Utilities.isAlphabets(request.middle_name.trim())) {
            profilePresenterImplClass.middleNameAlphabetFailure()
            return
        }

        else if (request.last_name.isEmpty()) {
            profilePresenterImplClass.lastNameValidationFailure()
            return
        }

        else if (request.last_name.trim().length<3) {
            profilePresenterImplClass.lastNameLengthFailure()
            return
        }
        else if (!Utilities.isAlphabets(request.last_name.trim())) {
            profilePresenterImplClass.lastNameAlphabetFailure()
            return
        }
        else if (request.address_1.isEmpty()) {
            profilePresenterImplClass.Address1ValidationFailure()
            return
        }

        else if (request.address_1.trim().length<3) {
            profilePresenterImplClass.addressLine1LengthFailure()
            return
        }
        else if (!(request.address_2.isEmpty() ) && request.address_2.trim().length<3) {
            profilePresenterImplClass.addressLine2LengthFailure()
            return
        }

        else if (request.pin_code.trim().isEmpty()) {
            profilePresenterImplClass.pinCodeValidationFailure()
            return
        }

        else if (request.pin_code.trim().length != 6) {
            profilePresenterImplClass.pinCodeLengthFailure()
            return
        }
        else {

            if ((request.mobile).isNotEmpty()) {
                if (!(Utilities.isValidMobile(request.mobile))) {
                    profilePresenterImplClass.mobileValidationFailure()
                    return
                }
            }

            if ((request.adhar_number.trim()).isNotEmpty()) {
                if (isAdhaarNoAdded) {
                    if (!(Utilities.validateAadharNumber(request.adhar_number))) {
                        profilePresenterImplClass.adhaarNoValidationFailure()
                        return
                    }
                }
            }

            if ((request.email).isNotEmpty()) {
                if (!(Utilities.isValidMail(request.email))) {
                    profilePresenterImplClass.emailValidationFailure()
                    return
                }
            }

            profilePresenterImplClass.onValidationSuccess(request)
        }
    }

    //hit update profile api
    fun updateProfile(request: SignupRequest, token: String?, isAdhaarNoAdded: Boolean) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        // map["username"] = toRequestBody(request.username)
        map["email"] = toRequestBody(request.email)
        // map["password"] = toRequestBody(request.password)
        map["first_name"] = toRequestBody(request.first_name)
        map["last_name"] = toRequestBody(request.last_name)
        map["middle_name"] = toRequestBody(request.middle_name)
        map["address_1"] = toRequestBody(request.address_1)
        map["address_2"] = toRequestBody(request.address_2)
        map["mobile"] = toRequestBody(request.mobile) //mobile is mobile_2

        map["district_id"] = toRequestBody(request.district_id)
        map["pin_code"] = toRequestBody(request.pin_code)

         if (isAdhaarNoAdded) {
        map["adhar_number"] = toRequestBody(request.adhar_number)
         }

        if (!request.profile_pic.equals("")) {
            val file = File(request.profile_pic)

            val profileImg = MultipartBody.Part.createFormData(
                "profile_pic", file.name,
                RequestBody.create(MediaType.parse(imgMediaType), file)
            )
            retrofitApi.updateProfile(map, profileImg, token)
                .enqueue(object : Callback<SignupResponse> {
                    override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                        if(t is SocketTimeoutException){
                            profilePresenterImplClass.showError("Socket Time error")
                        }else{
                            profilePresenterImplClass.showError(t.message + "")
                        }
                    }

                    override fun onResponse(
                        call: Call<SignupResponse>,
                        response: Response<SignupResponse>
                    ) {
                        val responseObject = response.body()
                        if (responseObject != null) {
                            if (responseObject.code == 200) {
                                profilePresenterImplClass.onSuccessfulUpdation(responseObject)
                            } else {
                                profilePresenterImplClass.showError(
                                    response.body()?.message ?: Constants.SERVER_ERROR
                                )
                            }
                        } else {
                            profilePresenterImplClass.showError(Constants.SERVER_ERROR)
                        }
                    }
                })
        } else {
            map["profile_pic"] = toRequestBody("")
            retrofitApi.updateProfileWithoutImage(map, token)
                .enqueue(object : Callback<SignupResponse> {
                    override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                        if(t is SocketTimeoutException){
                            profilePresenterImplClass.showError("Socket Time error")
                        }else{
                            profilePresenterImplClass.showError(t.message + "")
                        }
                    }

                    override fun onResponse(
                        call: Call<SignupResponse>,
                        response: Response<SignupResponse>
                    ) {
                        val responseObject = response.body()
                        if (responseObject != null) {
                            if (responseObject.code == 200) {
                                profilePresenterImplClass.onSuccessfulUpdation(responseObject)
                            } else {
                                profilePresenterImplClass.showError(
                                    response.body()?.message ?: Constants.SERVER_ERROR
                                )
                            }
                        } else {
                            profilePresenterImplClass.showError(Constants.SERVER_ERROR)
                        }
                    }
                })
        }
    }

    fun fetchUserInfo(userId: String) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["user_id"] = toRequestBody(userId)
        retrofitApi.getUserProfileData(map).enqueue(object :
            Callback<GetProfileResponse> {
            override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    profilePresenterImplClass.showError("Socket Time error")
                }else{
                    profilePresenterImplClass.showError(t.message + "")
                }
            }

            override fun onResponse(
                call: Call<GetProfileResponse>,
                response: Response<GetProfileResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        profilePresenterImplClass.getUserProfileSuccess(responseObject)
                    } else {
                        profilePresenterImplClass.showError(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    profilePresenterImplClass.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }
}
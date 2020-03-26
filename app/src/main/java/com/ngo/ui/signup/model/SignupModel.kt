package com.ngo.ui.signup.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.SignupRequest
import com.ngo.pojo.response.DistResponse
import com.ngo.pojo.response.SignupResponse
import com.ngo.ui.signup.presenter.SignupPresenterImplClass
import com.ngo.utils.Constants
import com.ngo.utils.Utilities
import retrofit2.Callback
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class SignupModel(var signupPresenterImplClass: SignupPresenterImplClass) {

    private val imgMediaType="image/*"

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value)
    }

    fun setValidation(request: SignupRequest) {

        if (request.username.isEmpty()) {
            signupPresenterImplClass.usernameEmptyValidation()
            return
        } else if (!(Utilities.isValidMobile(request.username))) {
            signupPresenterImplClass.usernameValidationFailure()
            return
        } else if (request.first_name.isEmpty()) {
            signupPresenterImplClass.firstNameValidationFailure()
            return
        } else if (request.last_name.isEmpty()) {
            signupPresenterImplClass.lastNameValidationFailure()
            return
        } else if (request.address_1.isEmpty()) {
            signupPresenterImplClass.Address1ValidationFailure()
            return
        } else if (request.pin_code.isEmpty()) {
            signupPresenterImplClass.pinCodeValidationFailure()
            return
        } else {

            if ((request.mobile).isNotEmpty()) {
                if (!(Utilities.isValidMobile(request.mobile))) {
                    signupPresenterImplClass.mobileValidationFailure()
                    return
                }
            }

            if ((request.adhar_number).isNotEmpty()) {
                if (!(Utilities.validateAadharNumber(request.mobile))) {
                    signupPresenterImplClass.adhaarNoValidationFailure()
                    return
                }
            }

            if ((request.email).isNotEmpty()) {
                if (!(Utilities.isValidMail(request.email))) {
                    signupPresenterImplClass.emailValidationFailure()
                    return
                }
            }

            if (request.password.isEmpty()) {
                signupPresenterImplClass.passwordEmptyValidation()
                return
            } else if ((request.password).length < 8) {
                signupPresenterImplClass.passwordLengthValidation()
                return
            } else if (request.confirmPass.isEmpty()) {
                signupPresenterImplClass.confirmPasswordEmptyValidation()
                return
            } else if ((request.confirmPass).length < 8) {
                signupPresenterImplClass.confirmPasswordLengthValidation()
                return
            } else if (!(request.password.equals(request.confirmPass))) {
                signupPresenterImplClass.confirmPasswordMismatchValidation()
                return
            }
            signupPresenterImplClass.onValidationSuccess(request)
        }
    }

    //hit api to register the user
    fun userRegisteration(signupRequest: SignupRequest) {
        val retrofitApi = ApiClient.getClientWithToken().create(CallRetrofitApi::class.java)
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

        val file = File(signupRequest.profile_pic)

        val profileImg = MultipartBody.Part.createFormData("profile_pic", file.name,
            RequestBody.create(MediaType.parse(imgMediaType), file)
        )


       /* //profile_img
        val file = File(signupRequest.profile_pic)
        val requestFile = RequestBody.create(MediaType.parse(imgMediaType), file)
        // MultipartBody.Part is used to send also the actual filename
        val profileImg = MultipartBody.Part.createFormData("image", file.getName(), requestFile)*/

        retrofitApi.registerUser(map, profileImg).enqueue(object : Callback<SignupResponse> {
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
        retrofitApi.getDist().enqueue(object : Callback<DistResponse> {
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
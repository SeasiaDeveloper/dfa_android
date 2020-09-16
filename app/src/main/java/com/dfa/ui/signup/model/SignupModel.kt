package com.dfa.ui.signup.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.request.SignupRequest
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.SignupResponse
import com.dfa.ui.signup.presenter.SignupPresenterImplClass
import com.dfa.utils.Constants
import com.dfa.utils.Utilities
import retrofit2.Callback
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.net.SocketTimeoutException

class SignupModel(var signupPresenterImplClass: SignupPresenterImplClass) {

    private val imgMediaType = "image/*"

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
        }
        else if (request.first_name.trim().length<3) {
            signupPresenterImplClass.firstNameLengthFailure()
            return
        }
        else if (!Utilities.isAlphabets(request.first_name.trim())) {
            signupPresenterImplClass.firstNameAlphabetFailure()
            return
        } else if (request.middle_name.trim().isNotEmpty() && request.middle_name.trim().length<3 ) {
            signupPresenterImplClass.middleNameLengthFailure()
            return
        }
        else if (request.middle_name.trim().isNotEmpty() && !Utilities.isAlphabets(request.middle_name.trim())) {
            signupPresenterImplClass.middleNameAlphabetFailure()
            return
        }
        else if (request.last_name.trim().isEmpty()) {
            signupPresenterImplClass.lastNameValidationFailure()
            return
        }
        else if (request.last_name.trim().length<3) {
            signupPresenterImplClass.lastNameLengthFailure()
            return
        }
        else if (!Utilities.isAlphabets(request.last_name.trim())) {
            signupPresenterImplClass.lastNameAlphabetFailure()
            return
        }
        else if (request.district_id.equals("-1") ){
            signupPresenterImplClass.districtValidationFailure()
            return
        }

        else if (request.address_1.isEmpty()) {
            signupPresenterImplClass.Address1ValidationFailure()
            return
        }
        else if (request.address_1.trim().length<3) {
            signupPresenterImplClass.addressLine1LengthFailure()
            return
        }
        else if (!(request.address_2.isEmpty() ) && request.address_2.trim().length<3) {
            signupPresenterImplClass.addressLine2LengthFailure()
            return
        }
        else if (request.pin_code.trim().isEmpty()) {
            signupPresenterImplClass.pinCodeValidationFailure()
            return
        }
        else if (request.pin_code.trim().length !=6) {
            signupPresenterImplClass.pinCodeLengthFailure()
            return
        }

        else if (request.email.trim().isEmpty()) {
            signupPresenterImplClass.emailValidationFailure()
            return
        }

      else  if (!(Utilities.isValidMail(request.email))) {
            signupPresenterImplClass.emailValidationFailure()
            return
        }

        else {

            if ((request.mobile).isNotEmpty()) {
                if (!(Utilities.isValidMobile(request.mobile))) {
                    signupPresenterImplClass.mobileValidationFailure()
                    return
                }
            }

            if ((request.adhar_number.trim()).isNotEmpty()) {
                if (!(Utilities.validateAadharNumber(request.adhar_number))) {
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
            }
           /* else if(!Utilities.isValidPassword(request.password.trim())){
                signupPresenterImplClass.passwordInvalidValidation()
                return
            }*/
            else if ((request.password).length < 6) {
                signupPresenterImplClass.passwordLengthValidation()
                return
            } else if (request.confirmPass.isEmpty()) {
                signupPresenterImplClass.confirmPasswordEmptyValidation()
                return
            } else if ((request.confirmPass).length < 6) {
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
    fun userRegisteration(signupRequest: SignupRequest, token: String?) {
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


        if (!signupRequest.profile_pic.equals("")) {
            val file = File(signupRequest.profile_pic)
            val profileImg = MultipartBody.Part.createFormData(
                "profile_pic", file.name,
                RequestBody.create(MediaType.parse(imgMediaType), file)
            )

            retrofitApi.registerUser(map, profileImg, token)
                .enqueue(object : Callback<SignupResponse> {
                    override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                        if(t is SocketTimeoutException){
                            signupPresenterImplClass.showError("Socket Time error")
                        }else{
                            signupPresenterImplClass.showError(t.message + "")
                        }
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
        } else {
            map["profile_pic"] = toRequestBody("")
            retrofitApi.registerUserWithoutProfile(map, token)
                .enqueue(object : Callback<SignupResponse> {
                    override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                        if(t is SocketTimeoutException){
                            signupPresenterImplClass.showError("Socket Time error")
                        }else{
                            signupPresenterImplClass.showError(t.message + "")
                        }
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

    }

    fun getDist() {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getDist().enqueue(object : Callback<DistResponse> {
            override fun onFailure(call: Call<DistResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    signupPresenterImplClass.showError("Socket Time error")
                }else{
                    signupPresenterImplClass.showError(t.message + "")
                }
            }

            override fun onResponse(call: Call<DistResponse>, response: Response<DistResponse>) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        signupPresenterImplClass.fetchDistList(responseObject)
                    } else {
                        signupPresenterImplClass.onSaveDetailsFailed(
                            response.body()?.message?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    signupPresenterImplClass.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }
}
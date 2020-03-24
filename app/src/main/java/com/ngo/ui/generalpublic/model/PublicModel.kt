package com.ngo.ui.generalpublic.model

import com.ngo.apis.ApiClient
import com.ngo.apis.CallRetrofitApi
import com.ngo.pojo.request.ComplaintRequest
import com.ngo.pojo.response.ComplaintResponse
import com.ngo.ui.generalpublic.presenter.PublicComplaintPresenter
import com.ngo.utils.Constants
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PublicModel(private var complaintsPresenter: PublicComplaintPresenter) {
    private val imgMediaType="image/*"
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }


    fun setValidation(level: Int, image: String,description:String) {
        if (level==0) {
            complaintsPresenter.onEmptyLevel()
            return
        }
        if (image.isEmpty()) {
            complaintsPresenter.onEmptyImage()
            return
        }

        if (description.isEmpty()) {
            complaintsPresenter.onEmptyDescription();return
        }
        complaintsPresenter.onValidationSuccess()
    }


    /*
       * hit api to save the complaints
       * */
    fun complaintsRequest(complaintsRequest: ComplaintRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["name"] = toRequestBody(complaintsRequest.name)
        map["phone"] = toRequestBody(complaintsRequest.phone)
        map["email"] = toRequestBody(complaintsRequest.email)
        map["crime"] = toRequestBody(complaintsRequest.crime)

        map["level"] = toRequestBody(complaintsRequest.level.toString())
        map["description"] = toRequestBody(complaintsRequest.description)
        map["device_token"] = toRequestBody(complaintsRequest.device_token)
        map["lat"] = toRequestBody(complaintsRequest.lat.toString())
        map["lng"] = toRequestBody(complaintsRequest.lng.toString())
        var file = File(complaintsRequest.image)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        // MultipartBody.Part is used to send also the actual filename
        val profileImg = MultipartBody.Part.createFormData("image", file.getName(), requestFile)


       // val profileImg = MultipartBody.Part.createFormData("image", "image", RequestBody.create(MediaType.parse(imgMediaType), complaintsRequest.image))
        retrofitApi.addComplaint(map,profileImg).enqueue(object :
            Callback<ComplaintResponse> {
            override fun onResponse(
                call: Call<ComplaintResponse>,
                response: Response<ComplaintResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.status == 200) {
                        complaintsPresenter.onSaveDetailsSuccess(responseObject)
                    } else {
                        complaintsPresenter.onSaveDetailsFailed(response.body()?.message ?: Constants.SERVER_ERROR)
                    }
                } else {
                    complaintsPresenter.showError(Constants.SERVER_ERROR)
                }
            }
            override fun onFailure(call: Call<ComplaintResponse>, t: Throwable) {
                complaintsPresenter.showError(t.message+"")
            }
        })
    }
}
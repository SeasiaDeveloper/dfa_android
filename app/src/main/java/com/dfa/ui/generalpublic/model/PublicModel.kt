package com.dfa.ui.generalpublic.model

import android.content.Context
import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.pojo.request.ComplaintRequest
import com.dfa.pojo.response.ComplaintResponse
import com.dfa.pojo.response.GetCrimeTypesResponse
import com.dfa.ui.generalpublic.presenter.PublicComplaintPresenter
import com.dfa.utils.Constants
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.SocketTimeoutException


class PublicModel(private var complaintsPresenter: PublicComplaintPresenter) {
    private val imgMediaType = "image/*"
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }


    fun setValidation(level: Int, image: ArrayList<String>, description: String) {
        if (level == 0) {
            complaintsPresenter.onEmptyLevel()
            return
        }
        if (image.size == 0) {
            complaintsPresenter.onEmptyImage()
            return
        }

        /* if (description.isEmpty()) {
             complaintsPresenter.onEmptyDescription();
             return
         }*/
        complaintsPresenter.onValidationSuccess()
    }

    fun getCrimeTypesList(token: String?) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getCrimeTypesList(token).enqueue(object :
            Callback<GetCrimeTypesResponse> {
            override fun onFailure(call: Call<GetCrimeTypesResponse>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    complaintsPresenter.showError("Socket Time error")
                } else {
                    complaintsPresenter.showError(t.message + "")
                }
            }

            override fun onResponse(
                call: Call<GetCrimeTypesResponse>,
                response: Response<GetCrimeTypesResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        complaintsPresenter.onGetCrimeTypesSuccess(responseObject)
                    } else {
                        complaintsPresenter.onGetCrimeTypesFailure(Constants.SERVER_ERROR)
                    }
                } else {
                    complaintsPresenter.showError(Constants.SERVER_ERROR)
                }
            }
        })
    }

    /*
       * hit api to save the complaints
       * */
    fun complaintsRequest(token: String?, complaintsRequest: ComplaintRequest, context: Context) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["crime_type_id"] = toRequestBody(complaintsRequest.crime)
        map["urgency"] = toRequestBody(complaintsRequest.level.toString())
        map["info"] = toRequestBody(complaintsRequest.description)
        // map["device_token"] = toRequestBody(complaintsRequest.device_token)
        map["latitude"] = toRequestBody(complaintsRequest.lat.toString())
        map["longitude"] = toRequestBody(complaintsRequest.lng.toString())
        map["media_type"] = toRequestBody(complaintsRequest.mediaType.toString())
        map["address"] = toRequestBody(complaintsRequest.address.toString())

        val parts = arrayOfNulls<MultipartBody.Part>(complaintsRequest.image.size)
        if (complaintsRequest.mediaType.equals("photos")) {
            for (i in 0 until complaintsRequest.image.size) {
                val file = File(complaintsRequest.image.get(i))
                val surveyBody: RequestBody =
                    RequestBody.create(MediaType.parse("image/*"), file)
                parts[i] =
                    MultipartBody.Part.createFormData("crime_pics[]", file.name, surveyBody)
            }
        } else {
            for (i in 0 until complaintsRequest.image.size) {
                val file = File(complaintsRequest.image.get(i))
                val surveyBody: RequestBody =
                    RequestBody.create(MediaType.parse("video/*"), file)
                parts[i] =
                    MultipartBody.Part.createFormData("crime_pics[]", file.name, surveyBody)
            }
        }


        retrofitApi.addComplaint(token, map, parts).enqueue(object :
            Callback<ComplaintResponse> {
            override fun onResponse(
                call: Call<ComplaintResponse>,
                response: Response<ComplaintResponse>
            ) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.code == 200) {
                        complaintsPresenter.onSaveDetailsSuccess(responseObject)
                    } else {
                        complaintsPresenter.onSaveDetailsFailed(
                            response.body()?.message ?: Constants.SERVER_ERROR
                        )
                    }
                } else {
                    complaintsPresenter.showError(Constants.SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<ComplaintResponse>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    complaintsPresenter.showError("Socket Time error")
                } else {
                    complaintsPresenter.showError(t.message + "")
                }
            }
        })
    }
}
package com.dfa.ui.home.fragments.cases.model

import com.dfa.apis.ApiClient
import com.dfa.apis.CallRetrofitApi
import com.dfa.application.MyApplication
import com.dfa.pojo.request.CasesRequest
import com.dfa.pojo.request.CreatePostRequest
import com.dfa.pojo.request.CrimeDetailsRequest
import com.dfa.pojo.request.PublicVisibilityRequest
import com.dfa.pojo.response.*
import com.dfa.ui.home.fragments.cases.presenter.CasesPresenterImplClass
import com.dfa.utils.Constants
import com.dfa.utils.PreferenceHandler
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.SocketTimeoutException

class CasesModel(private var presenter: CasesPresenterImplClass) {

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

    private val imgMediaType = "image/*"

    fun fetchComplaints(casesRequest: CasesRequest, token: String, userRole: String) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["all"] = toRequestBody(casesRequest.all) //zero for my case 1 for all case
        map["search"] = toRequestBody(casesRequest.search)
        map["type"] = toRequestBody(casesRequest.type) //type = 0 for cases
        map["page"] = toRequestBody(casesRequest.page)
        map["per_page"] = toRequestBody(casesRequest.perPage)

        //in case of Police
        if (userRole.equals("2")) {
            //my cases case for police
            if (casesRequest.all.equals("0")) {

                if (token.isEmpty()) {
                    map["guest_user"] = toRequestBody("1")
                }


                retrofitApi.getMyCasesForPolice(token, map)
                    .enqueue(object : Callback<GetCasesResponse> {
                        override fun onResponse(
                            call: Call<GetCasesResponse>,
                            response: Response<GetCasesResponse>
                        ) {
                            val responseObject = response.body()
                            if (responseObject != null) {
                                if (responseObject.code == 200) {
                                    presenter.onGetCompaintsSuccess(responseObject)
                                } else {
                                    presenter.onGetCompaintsFailed(
                                        response.body()?.message ?: Constants.SERVER_ERROR
                                    )
                                }
                            } else {
                                presenter.showError(Constants.SERVER_ERROR)
                            }
                        }

                        override fun onFailure(call: Call<GetCasesResponse>, t: Throwable) {
                            if (t is SocketTimeoutException) {
                                presenter.showError("Socket Time error")
                            } else {
                                presenter.showError("Somthing went wrong, please try again latter")
                            }
                        }
                    })


            }
            //all cases for police
            else {
                retrofitApi.getCasesForPolice(token, map)
                    .enqueue(object : Callback<GetCasesResponse> {
                        override fun onResponse(
                            call: Call<GetCasesResponse>,
                            response: Response<GetCasesResponse>
                        ) {
                            val responseObject = response.body()
                            if (responseObject != null) {
                                if (responseObject.code == 200) {
                                    presenter.onGetCompaintsSuccess(responseObject)
                                } else {
                                    presenter.onGetCompaintsFailed(
                                        response.body()?.message ?: Constants.SERVER_ERROR
                                    )
                                }
                            } else {
                                if (response.raw().code() == 403) {
                                    presenter.showError(Constants.TOKEN_ERROR)
                                } else {
                                    presenter.showError(Constants.SERVER_ERROR)
                                }
                            }
                        }

                        override fun onFailure(call: Call<GetCasesResponse>, t: Throwable) {
                            if (t is SocketTimeoutException) {
                                presenter.showError("Socket Time error")
                            } else {
                                presenter.showError("Somthing went wrong, please try again latter")
                            }
                        }
                    })
            }
        }

            else if(userRole.equals("3")){
            map["search_type"] = toRequestBody(casesRequest.filterValue)
            map["type"] = toRequestBody("") //type = 0 for cases
            retrofitApi.getNodalUserDetail(token, map)
                .enqueue(object : Callback<GetCasesResponse> {
                    override fun onResponse(
                        call: Call<GetCasesResponse>,
                        response: Response<GetCasesResponse>
                    ) {
                        val responseObject = response.body()
                        if (responseObject != null) {
                            if (responseObject.code == 200) {
                                presenter.onGetCompaintsSuccess(responseObject)
                            } else {
                                presenter.onGetCompaintsFailed(
                                    response.body()?.message ?: Constants.SERVER_ERROR
                                )
                            }
                        } else {
                            presenter.showError(Constants.SERVER_ERROR)
                        }
                    }

                    override fun onFailure(call: Call<GetCasesResponse>, t: Throwable) {
                        if (t is SocketTimeoutException) {
                            presenter.showError("Socket Time error")
                        } else {
                            presenter.showError("Somthing went wrong, please try again latter")
                        }
                    }
                })



        }
        //in case of NGO and normal user
        else {
            if (token.isEmpty()) {
                map["guest_user"] = toRequestBody("1")
            }

            retrofitApi.getCases(token, map).enqueue(object : Callback<GetCasesResponse> {
                override fun onResponse(
                    call: Call<GetCasesResponse>,
                    response: Response<GetCasesResponse>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            presenter.onGetCompaintsSuccess(responseObject)
                        } else {
                            presenter.onGetCompaintsFailed(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        if (response.raw().code() == 403) {
                            presenter.showError(Constants.TOKEN_ERROR)
                        } else {
                            presenter.showError(Constants.SERVER_ERROR)
                        }
                    }
                }

                override fun onFailure(call: Call<GetCasesResponse>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        presenter.showError("Socket Time error")
                    } else {
                        presenter.showError("Somthing went wrong, please try again latter")
                    }
                }
            })

        }
    }

    fun createPost(request: CreatePostRequest, token: String?) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["info"] = toRequestBody(request.info)
        if (request.post_pics.size > 0 && request.post_pics.get(0).isNotEmpty()) {
            map["media_type"] = toRequestBody(request.media_type)


            /*    val file = File(request.post_pics[0])
            val post_pics = MultipartBody.Part.createFormData("post_pics", file.name,
                RequestBody.create(MediaType.parse(imgMediaType), file)
            )*/

            val parts = arrayOfNulls<MultipartBody.Part>(request.post_pics.size)
            if (request.media_type.equals("photos")) {
                for (i in 0 until request.post_pics.size) {
                    val file = File(request.post_pics.get(i))
                    val surveyBody: RequestBody =
                        RequestBody.create(MediaType.parse("image/*"), file)
                    parts[i] =
                        MultipartBody.Part.createFormData("post_pics[]", file.name, surveyBody)
                }
            } else {
                for (i in 0 until request.post_pics.size) {
                    val file = File(request.post_pics.get(i))
                    val surveyBody: RequestBody =
                        RequestBody.create(MediaType.parse("video/*"), file)
                    parts[i] =
                        MultipartBody.Part.createFormData("post_pics[]", file.name, surveyBody)
                }
            }

            retrofitApi.addPost(token, map, parts).enqueue(object : Callback<CreatePostResponse> {
                override fun onFailure(call: Call<CreatePostResponse>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        presenter.showError("Socket Time error")
                    } else {
                        presenter.showError("Somthing went wrong, please try again latter")
                    }
                }

                override fun onResponse(
                    call: Call<CreatePostResponse>,
                    response: Response<CreatePostResponse>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            presenter.onPostAdded(responseObject)
                        } else {
                            presenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        presenter.showError(Constants.SERVER_ERROR)
                    }
                }
            })
        } else {
            retrofitApi.addPostWithoutMedia(token, map)
                .enqueue(object : Callback<CreatePostResponse> {
                    override fun onFailure(call: Call<CreatePostResponse>, t: Throwable) {
                        if (t is SocketTimeoutException) {
                            presenter.showError("Socket Time error")
                        } else {
                            presenter.showError("Somthing went wrong, please try again latter")
                        }
                    }

                    override fun onResponse(
                        call: Call<CreatePostResponse>,
                        response: Response<CreatePostResponse>
                    ) {
                        val responseObject = response.body()
                        if (responseObject != null) {
                            if (responseObject.code == 200) {
                                presenter.onPostAdded(responseObject)
                            } else {
                                presenter.showError(
                                    response.body()?.message ?: Constants.SERVER_ERROR
                                )
                            }
                        } else {
                            presenter.showError(Constants.SERVER_ERROR)
                        }
                    }
                })
        }
    }

    fun deleteComplaint(token: String, id: String) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["complaint_id"] = toRequestBody(id)
        retrofitApi.deleteComplaintOrPost(token, map)
            .enqueue(object : Callback<DeleteComplaintResponse> {
                override fun onFailure(call: Call<DeleteComplaintResponse>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        presenter.showError("Socket Time error")
                    } else {
                        presenter.showError("Somthing went wrong, please try again latter")
                    }
                }

                override fun onResponse(
                    call: Call<DeleteComplaintResponse>,
                    response: Response<DeleteComplaintResponse>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            presenter.onComplaintDeleted(responseObject)
                        } else {
                            presenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        presenter.showError(Constants.SERVER_ERROR)
                    }
                }
            })
    }





    fun hideComplaint(token: String, id: String) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["complaint_id"] = toRequestBody(id)
        retrofitApi.hideComplaintOrPost(token, map)
            .enqueue(object : Callback<DeleteComplaintResponse> {
                override fun onFailure(call: Call<DeleteComplaintResponse>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        presenter.showError("Socket Time error")
                    } else {
                        presenter.showError("Somthing went wrong, please try again latter")
                    }
                }

                override fun onResponse(
                    call: Call<DeleteComplaintResponse>,
                    response: Response<DeleteComplaintResponse>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            presenter.onComplaintDeleted(responseObject)
                        } else {
                            presenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        presenter.showError(Constants.SERVER_ERROR)
                    }
                }
            })
    }

    fun changeLikeStatus(token: String, id: String) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["complaint_id"] = toRequestBody(id)
        retrofitApi.changeLikeStatus(token, map)
            .enqueue(object : Callback<DeleteComplaintResponse> {
                override fun onFailure(call: Call<DeleteComplaintResponse>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        presenter.showError("Socket Time error")
                    } else {
                        presenter.showError("Somthing went wrong, please try again latter")
                    }
                }

                override fun onResponse(
                    call: Call<DeleteComplaintResponse>,
                    response: Response<DeleteComplaintResponse>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            presenter.onLikeStatusChanged(responseObject)
                        } else {
                            presenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        presenter.showError(Constants.SERVER_ERROR)
                    }
                }
            })
    }

    fun saveAdhaarNo(token: String, adhaarNo: String) {
        //hit api
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["adhar_number"] = toRequestBody(adhaarNo)
        retrofitApi.updateProfileWithoutImage(map, token)
            .enqueue(object : Callback<SignupResponse> {
                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        presenter.showError("Socket Time error")
                    } else {
                        presenter.showError("Somthing went wrong, please try again latter")
                    }
                }

                override fun onResponse(
                    call: Call<SignupResponse>,
                    response: Response<SignupResponse>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            presenter.adhaarSavedSuccess(responseObject)
                        } else {
                            presenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        presenter.showError(Constants.SERVER_ERROR)
                    }
                }
            })
    }

    fun fetchStatusList(token: String, userRole: String) {
        //hit api to fetch the status
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getStatus(token, userRole.toInt())
            .enqueue(object : Callback<GetStatusResponse> {
                override fun onFailure(call: Call<GetStatusResponse>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        presenter.showError("Socket Time error")
                    } else {
                        presenter.showError("Somthing went wrong, please try again latter")
                    }
                }

                override fun onResponse(
                    call: Call<GetStatusResponse>,
                    response: Response<GetStatusResponse>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            presenter.onListFetchedSuccess(responseObject)
                        } else {
                            presenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        presenter.showError(Constants.SERVER_ERROR)
                    }
                }
            })
    }

    fun updateStatus(token: String, complaintId: String, statusId: String, comment: String) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        map["complaint_id"] = toRequestBody(complaintId)
        map["status"] = toRequestBody(statusId)
        if (comment.isNotEmpty()) map["comment"] = toRequestBody(comment)
        retrofitApi.updateStatus(token, map)
            .enqueue(object : Callback<UpdateStatusSuccess> {
                override fun onFailure(call: Call<UpdateStatusSuccess>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        presenter.showError("Socket Time error")
                    } else {
                        presenter.showError("Somthing went wrong, please try again latter")
                    }
                }

                override fun onResponse(
                    call: Call<UpdateStatusSuccess>,
                    response: Response<UpdateStatusSuccess>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            presenter.statusUpdationSuccess(responseObject)
                        } else {
                            presenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        presenter.showError(Constants.SERVER_ERROR)
                    }
                }
            })
    }

    fun callFirImageApi(token: String, request: CrimeDetailsRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        //map["complaint_id"] = toRequestBody(request.complaintId)
        val complaintId = Integer.parseInt(request.complaintId)
        retrofitApi.getFirImage(token, complaintId)
            .enqueue(object : Callback<FirImageResponse> {
                override fun onFailure(call: Call<FirImageResponse>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        presenter.showError("Socket Time error")
                    } else {
                        presenter.showError("Somthing went wrong, please try again latter")
                    }
                }

                override fun onResponse(
                    call: Call<FirImageResponse>,
                    response: Response<FirImageResponse>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            presenter.getfirImageResponse(responseObject)
                        } else {
                            presenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        presenter.showError(Constants.SERVER_ERROR)
                    }
                }
            })
    }



    fun publicVisibility(token: String, request: PublicVisibilityRequest) {
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        val map = HashMap<String, RequestBody>()
        //map["complaint_id"] = toRequestBody(request.complaintId)
        retrofitApi.publicVisibility(token, request)
            .enqueue(object : Callback<PublicVisibilityResponse> {
                override fun onFailure(call: Call<PublicVisibilityResponse>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        presenter.showError("Socket Time error")
                    } else {
                        presenter.showError("Somthing went wrong, please try again latter")
                    }
                }

                override fun onResponse(
                    call: Call<PublicVisibilityResponse>,
                    response: Response<PublicVisibilityResponse>
                ) {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject.code == 200) {
                            presenter.publicVisibilityResponse(responseObject)
                        } else {
                            presenter.showError(
                                response.body()?.message ?: Constants.SERVER_ERROR
                            )
                        }
                    } else {
                        presenter.showError(Constants.SERVER_ERROR)
                    }
                }
            })
    }


    fun getAdvertisement(input: AdvertisementInput) {
        val token =
            PreferenceHandler.readString(MyApplication.instance, PreferenceHandler.AUTHORIZATION, "")
        val retrofitApi = ApiClient.getClient().create(CallRetrofitApi::class.java)
        retrofitApi.getDistAdvertisement(token,input).enqueue(object : Callback<AdvertisementResponse> {

            override fun onResponse(call: Call<AdvertisementResponse>, response: Response<AdvertisementResponse>) {
                val responseObject = response.body()
                if (responseObject != null) {
                    if (responseObject.data !=null) {
                        presenter.advertisementSuccess(responseObject)
                    } else {
                        presenter.showError("Somthing went wrong")
                    }
                } else {
                    presenter.showError("Server Error")
                }
            }
            override fun onFailure(call: Call<AdvertisementResponse>, t: Throwable) {
                if(t is SocketTimeoutException){
                    presenter.showError("Socket Time error")
                }else{
                    presenter.showError("Somthing went wrong, please try again latter")
                }
            }
        })
    }






}
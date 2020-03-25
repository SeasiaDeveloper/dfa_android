package com.ngo.apis

import com.ngo.pojo.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CallRetrofitApi {

    @Multipart
    @POST("v1/lodgeComplaint")
    fun addComplaint(@PartMap params: HashMap<String,RequestBody>, @Part licenseImg: MultipartBody.Part): Call<ComplaintResponse>

    @Multipart
    @POST("v1/forwardComplaint")
    fun addNGOData(@PartMap params: HashMap<String,RequestBody>): Call<NGOResponse>

    @Multipart
    @POST("v1/updateComplaint")
    fun savePoliceStatus(@PartMap params: HashMap<String,RequestBody>): Call<PoliceStatusResponse>

    @GET("v1/showallforwards")
    fun getNgoDetailsForPolice(): Call<GetPoliceFormData>

    @GET("v1/getcomplains")
    fun getcomplains(): Call<GetComplaintsResponse>

    @GET("v1/showcomplaintimage")
    fun getPoliceForm(@Query("id") id: Int): Call<GetPoliceFormResponse>

    @Multipart
    @POST("jwt-auth/v1/token")
    fun login(@PartMap params: HashMap<String,RequestBody>): Call<LoginResponse>

    @Multipart
    @POST("wp/v2/user/register")
    fun registerUser(@PartMap params: HashMap<String,RequestBody>, @Part profile_pic: MultipartBody.Part): Call<SignupResponse>


    @GET("wp/v2/districts")
    fun getDist(): Call<DistResponse>

    @Multipart
    @POST("jwt-auth/v1/edit_profile")
    fun updateProfile(@PartMap params: HashMap<String,RequestBody>, @Part profile_pic: MultipartBody.Part): Call<SignupResponse>

}
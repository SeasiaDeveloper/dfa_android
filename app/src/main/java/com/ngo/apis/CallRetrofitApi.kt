package com.ngo.apis

import com.ngo.pojo.request.ChangePasswordRequest
import com.ngo.pojo.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CallRetrofitApi {


    @Multipart
    @POST("jwt-auth/v1/report_crime")
    fun addComplaint(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>, @Part images: Array<MultipartBody.Part?>): Call<ComplaintResponse>

    /*@Multipart
    @POST("v1/lodgeComplaint")
    fun addComplaint(@PartMap params: HashMap<String,RequestBody>, @Part licenseImg: MultipartBody.Part): Call<ComplaintResponse>*/

    @Multipart
    @POST("v1/forwardComplaint")
    fun addNGOData(@PartMap params: HashMap<String, RequestBody>): Call<NGOResponse>

    @Multipart
    @POST("v1/updateComplaint")
    fun savePoliceStatus(@PartMap params: HashMap<String, RequestBody>): Call<PoliceStatusResponse>

    @GET("v1/showallforwards")
    fun getNgoDetailsForPolice(): Call<GetPoliceFormData>

    @GET("v1/getcomplains")
    fun getcomplains(): Call<GetComplaintsResponse>

    @GET("v1/showcomplaintimage")
    fun getPoliceForm(@Query("id") id: Int): Call<GetPoliceFormResponse>

    @Multipart
    @POST("jwt-auth/v1/token")
    fun login(@PartMap params: HashMap<String, RequestBody>): Call<LoginResponse>

    @POST("wp/v2/user/change-password")
    fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): Call<ChangePasswordResponse>

    @Multipart
    @POST("wp/v2/user/validate-byphone")
    fun verifyUser(@PartMap params: HashMap<String, RequestBody>): Call<VerifyUserResponse>

    @Multipart
    @POST("wp/v2/user/register")
    fun registerUser(
        @PartMap params: HashMap<String, RequestBody>, @Part profile_pic: MultipartBody.Part, @Header(
            "Authorization"
        ) authorization: String?
    ): Call<SignupResponse>

    @Multipart
    @POST("wp/v2/user/register")
    fun registerUserWithoutProfile(@PartMap params: HashMap<String, RequestBody>, @Header("Authorization") authorization: String?): Call<SignupResponse>

    @GET("wp/v2/districts")
    fun getDist(): Call<DistResponse>

    @GET("jwt-auth/v1/crime_types")
    fun getCrimeTypesList(@Header("Authorization") authorization: String?): Call<GetCrimeTypesResponse>

    @POST("jwt-auth/v1/profile")
    fun getProfileData(@Header("Authorization") authorization: String?): Call<GetProfileResponse>

    @Multipart
    @POST("jwt-auth/v1/edit_profile")
    fun updateProfile(
        @PartMap params: HashMap<String, RequestBody>, @Part profile_pic: MultipartBody.Part, @Header(
            "Authorization"
        ) authorization: String?
    ): Call<SignupResponse>

    @Multipart
    @POST("jwt-auth/v1/edit_profile")
    fun updateProfileWithoutImage(@PartMap params: HashMap<String, RequestBody>, @Header("Authorization") authorization: String?): Call<SignupResponse>

    @Multipart
    @POST("jwt-auth/v1/crime_list")
    fun getCases(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>): Call<GetCasesResponse>

    @Multipart
    @POST("jwt-auth/v1/create_post")
    fun addPost(@Header("Authorization") authorization: String, @PartMap params: HashMap<String, RequestBody>, @Part post_pics: MultipartBody.Part): Call<GetCasesResponse>

    @Multipart
    @POST("jwt-auth/v1/delete_complaint")
    fun deleteComplaintOrPost(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>): Call<DeleteComplaintResponse>
    @Multipart
    @POST("jwt-auth/v1/crime_media")
    fun getcrime_media(@PartMap params:HashMap<String, RequestBody>): Call<GetPhotosResponse>


}
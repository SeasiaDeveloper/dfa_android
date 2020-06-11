package com.ngo.apis

import com.ngo.pojo.request.ChangePasswordRequest
import com.ngo.pojo.request.UpdatePasswordRequest
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

    @GET("jwt-auth/v1/my_earning")
    fun getMyEarningsData(@Header("Authorization") authorization: String?,@Query("contact_number") id: Long): Call<MyEarningsResponse>

    @Multipart
    @POST("jwt-auth/v1/token")
    fun login(@PartMap params: HashMap<String, RequestBody>): Call<LoginResponse>

    @POST("wp/v2/user/change-password")
    fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): Call<ChangePasswordResponse>

    @POST("wp/v2/user/change-password")
    fun updatePassword(@Body changePasswordRequest: UpdatePasswordRequest): Call<ChangePasswordResponse>

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
    @POST("jwt-auth/v1/getprofile")
    fun getUserProfileData(@PartMap params: HashMap<String, RequestBody>): Call<GetProfileResponse>

    @Multipart
    @POST("jwt-auth/v1/update_location")
    fun postLocationData(@Header("Authorization") authorization: String?,@PartMap params: HashMap<String, RequestBody>): Call<PostLocationResponse>

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
    @POST("jwt-auth/v1/crime_list_police_copy")
    fun getCasesForPolice(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>): Call<GetCasesResponse>

    //get my cases for police user
    @Multipart
    @POST("jwt-auth/v1/police_case")
    fun getMyCasesForPolice(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>): Call<GetCasesResponse>

    @Multipart
    @POST("jwt-auth/v1/create_post")
    fun addPost(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>, @Part images: Array<MultipartBody.Part?>): Call<CreatePostResponse> //@Part post_pics: MultipartBody.Part

    @Multipart
    @POST("jwt-auth/v1/create_post")
    fun addPostWithoutMedia(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>): Call<CreatePostResponse> //@Part post_pics: MultipartBody.Part

    @Multipart
    @POST("jwt-auth/v1/delete_complaint")
    fun deleteComplaintOrPost(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>): Call<DeleteComplaintResponse>

    @Multipart
    @POST("jwt-auth/v1/hide_complaint")
    fun hideComplaintOrPost(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>): Call<DeleteComplaintResponse>

    @Multipart
    @POST("jwt-auth/v1/crime_media")
    fun getcrime_media(@Header("Authorization") authorization: String?,@PartMap params: HashMap<String, RequestBody>): Call<GetPhotosResponse>

    @Multipart
    @POST("jwt-auth/v1/like_complaint")
    fun changeLikeStatus(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>): Call<DeleteComplaintResponse>

    @Multipart
    @POST("jwt-auth/v1/crime_detail")
    fun getCrimeDetails(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>): Call<GetCrimeDetailsResponse>

    @GET("wp/v2/user/get_terms_condition")
    fun get_terms_condition(@Header("Authorization") authorization: String?): Call<GetTermsConditionsResponse>

    @GET("jwt-auth/v1/comment_list")
    fun getComments(@Header("Authorization") authorization: String?, @Query("complaint_id") id: Int): Call<GetCommentsResponse>

    @Multipart
    @POST("jwt-auth/v1/comment_likes")
    fun getLikes(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>): Call<GetCommentsResponse>

    @Multipart
    @POST("jwt-auth/v1/comment_complaint")
    fun addComment(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>): Call<DeleteComplaintResponse>

    @GET("jwt-auth/v1/status_list")
    fun getStatus(@Header("Authorization") authorization: String?, @Query("type") id: Int): Call<GetStatusResponse>

    @Multipart
    @POST("jwt-auth/v1/status_update")
    fun updateStatus(@Header("Authorization") authorization: String?, @PartMap params: HashMap<String, RequestBody>): Call<UpdateStatusSuccess>

    @POST("jwt-auth/v1/fir_imageapi")
    fun getFirImage(@Header("Authorization") authorization: String?, @Query("complaint_id") id: Int): Call<FirImageResponse>

    @POST("jwt-auth/v1/emergency_contact")
    fun getEmergencyData(@Header("Authorization") authorization: String?, @Query("distId") id: String): Call<EmergencyDataResponse>
}
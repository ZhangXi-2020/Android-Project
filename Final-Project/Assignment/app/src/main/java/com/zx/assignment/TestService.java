package com.zx.assignment;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface TestService {
//    @POST("/video")
//    Call<ResponseBody> post(@Query("address") String address);

    @Multipart
    @POST("/invoke/video")
    Call<ResponseBody> post(
            @Query("student_id") String studentId,
            @Query("user_name") String userName,
            @Part MultipartBody.Part coverImage,
            @Part MultipartBody.Part video
    );

    @GET("/invoke/video")
    Call<ResponseBody> get();
}

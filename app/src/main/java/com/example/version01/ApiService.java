package com.example.version01;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @FormUrlEncoded
    @POST("add_user.php")
    Call<ApiResponse> addUser(@Field("username") String username,
                              @Field("Email") String Email,
                              @Field("phone") String phone,
                              @Field("password") String password);

    @FormUrlEncoded
    @POST("check_user.php")
    Call<ApiResponse> checkUser(
            @Field("username") String username,
            @Field("password") String password
    );
    @GET("test_connection") // ایجاد یک endpoint برای تست اتصال
    Call<ResponseBody> testConnection();

    @POST("submitProfile.php")
    Call<ApiResponse> submitProfile(
            @Field("username") String username,
            @Field("fullname") String fullname,
            @Field("height") String height,
            @Field("weight") String weight,
            @Field("location") String location,
            @Field("job") String job,
            @Field("diseaseRecords") String diseaseRecords,
            @Field("hobby") String hobby,
            @Field("gender") String gender


    );
    @FormUrlEncoded
    @GET("get_user_data.php")
    Call<UserResponse> getUserData(@Query("username") String username);
}

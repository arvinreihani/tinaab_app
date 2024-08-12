package com.example.version01;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("add_user.php")
    Call<ApiResponse> addUser(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("check_user.php")
    Call<ApiResponse> checkUser(
            @Field("username") String username,
            @Field("password") String password
    );
    @GET("test_connection") // ایجاد یک endpoint برای تست اتصال
    Call<ResponseBody> testConnection();

}

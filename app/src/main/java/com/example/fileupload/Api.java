package com.example.fileupload;

import com.example.fileupload.model.LoginResponse;
import com.example.fileupload.model.LogoutResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface Api {
    @GET("auth/login")
    Call<LoginResponse> login(
            @Header("Cookie") String token,
            @Query("username") String username,
            @Query("password") String password
    );

    @GET("auth/login")
    Call<LogoutResponse> logout(
            @Header("Cookie") String token
    );
}

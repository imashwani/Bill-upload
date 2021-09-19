package com.example.fileupload;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientSingleton {

    public static final String BASE_LOGIN = "https://api.mocklets.com/p68332/";

    private static RetrofitClientSingleton mInstance;
    private Retrofit retrofit;

    // private constructor
    private RetrofitClientSingleton() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_LOGIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClientSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClientSingleton();
        }
        return mInstance;
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }
}

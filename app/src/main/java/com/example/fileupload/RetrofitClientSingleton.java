package com.example.fileupload;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientSingleton {

    public static final String BASE_LOGIN = "http://ocrv1.herokuapp.com/";

    private static RetrofitClientSingleton mInstance;
    private Retrofit retrofit;

    // private constructor
    private RetrofitClientSingleton() {
        getGsonBuilder();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_LOGIN)
                .addConverterFactory(GsonConverterFactory.create(getGsonBuilder()))
                .build();
    }

    private Gson getGsonBuilder() {
        return new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create();
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

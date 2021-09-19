package com.example.fileupload;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ConcurrentModificationException;

public class SharedprefManager {

    private static final String SHARED_PREF_NAME = "my_shared_pref";

    private static SharedprefManager mInstance;

    private Context context;

    private SharedprefManager(Context context){
        this.context= context;
    }
    public static synchronized SharedprefManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new SharedprefManager(context);
        }
        return mInstance;
    }
    public void saveUser(String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("token", token);
        editor.apply();
    }
    public  boolean isLoggedIn(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,context.MODE_PRIVATE);
       // if it is -1 then user is logged in
        // iska kuch krna padega
       /*if(sharedPreferences.getString("token",-1) ! = -1){
            return true;
        }*/
       return false;
    }
    public String getUser(){
        //we can also create new class here
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,context.MODE_PRIVATE);
       return sharedPreferences.getString("token",null);

    }
    public void clear(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();


    }

}

package com.example.fileupload;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedprefManager {

    private static final String SHARED_PREF_NAME = "my_shared_pref";

    private static SharedprefManager mInstance;

    private Context context;

    private static SharedPreferences sharedPreferences;

    private SharedprefManager(Context context) {
        this.context = context;
    }

    public static synchronized SharedprefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedprefManager(context);
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, context.MODE_PRIVATE);
        }
        return mInstance;
    }

    public void saveUser(String userName) {
        saveKeyValuePair("user_name", userName);
    }


    public void saveToken(String token) {
        saveKeyValuePair("token", token);
    }

    private void saveKeyValuePair(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, value);
        editor.apply();
    }

    public boolean isLoggedIn() {
        // if it is -1 then user is logged in
        // iska kuch krna padega
        if (sharedPreferences.getString("token", "").isEmpty()) {
            return false;
        }
        return true;
    }

    public String getUser() {
        //we can also create new class here
        return sharedPreferences.getString("token", null);

    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}

package com.example.fileupload;

import android.content.Context;
import android.content.SharedPreferences;

import static com.example.fileupload.Constants.TOKEN;

public class SharedprefManager {

    private static final String SHARED_PREF_NAME = "my_shared_pref";

    public static final String USER_NAME = "user_name";

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
        saveKeyValuePair(USER_NAME, userName);
    }


    public void saveToken(String token) {
        saveKeyValuePair(TOKEN, token);
    }

    private void saveKeyValuePair(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, value);
        editor.apply();
    }

    public boolean isLoggedIn() {
        // if token is present then user is logged in
        if (sharedPreferences.getString(TOKEN, "").isEmpty()) {
            return false;
        }
        return true;
    }

    public String getUser() {
        //we can also create new class here
        return sharedPreferences.getString(TOKEN, null);

    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getUserToken() {
        return sharedPreferences.getString(TOKEN, null);
    }
}

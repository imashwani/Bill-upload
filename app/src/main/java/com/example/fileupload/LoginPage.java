package com.example.fileupload;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fileupload.databinding.ActivityLoginPageBinding;
import com.example.fileupload.model.LoginResponse;
import com.example.fileupload.model.Payload;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPage extends AppCompatActivity {
    private ActivityLoginPageBinding binding;
    private String email;
    private String password;

    private SharedprefManager sharedprefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        signIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedprefManager = SharedprefManager.getInstance(this);
        if (sharedprefManager.isLoggedIn()) {
            openMainActivity();
        }
    }

    private void signIn() {
        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = binding.inemail.getText().toString().trim();
                password = binding.inpassword.getText().toString();
                // checking for empty email
                if (email.isEmpty()) {
                    binding.inemail.setError("Username is required");
                    binding.inemail.requestFocus();
                    return;
                }
                // for valid password
                if (password.isEmpty()) {
                    binding.inpassword.setError("Password is required");
                    binding.inpassword.requestFocus();
                    return;
                }
                makeRequest();
            }
        });
    }

    private void makeRequest() {
        String token = sharedprefManager.getUserToken();
        // creating user with email and password using retrofit
        Call<LoginResponse> call = RetrofitClientSingleton.getInstance()
                .getApi()
                .login(token, email, password);
        // make a queue to send request
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                SharedprefManager instance = SharedprefManager.getInstance(LoginPage.this);
                Payload payload = response.body().getPayload();
                if (payload != null) {
                    if (payload.getToken() != null && !payload.getToken().isEmpty()) {
                        String token = payload.getToken();
                        String userName = payload.getUserName();

                        //save data in shared preference
                        instance.saveUserName(userName);
                        instance.saveToken(token);
                        // then go to the MainActivity
                        openMainActivity();
                    } else {
                        showToast("login failed, token unavailable");
                    }
                } else {
                    showToast("login failed, response empty");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    private void openMainActivity() {
        Intent intent = new Intent(LoginPage.this, MainActivity.class);
        // for clearing tasks
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showToast(String msg) {
        Toast.makeText(LoginPage.this, msg, Toast.LENGTH_LONG).show();
    }

}
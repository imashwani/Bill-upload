package com.example.fileupload;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fileupload.databinding.ActivityLoginPageBinding;
import com.example.fileupload.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPage extends AppCompatActivity {
    ActivityLoginPageBinding binding;
    String email;
    String password;

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
        if (SharedprefManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
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
                    binding.inemail.setError("Email is required");
                    binding.inemail.requestFocus();
                    return;
                }
                // for valid email
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.inemail.setError("Email is invalid");
                    binding.inemail.requestFocus();
                    return;
                }
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                startActivity(intent);

                makeRequest();
            }
        });
    }

    private void makeRequest() {
        // creating user with email and password using retrofit
        Call<LoginResponse> call = RetrofitClientSingleton.getInstance()
                .getApi()
                .loginUser(email, password);
        // make a queue to send request
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                String token = response.body().getToken();

                // now save the data in shared prefernce
                SharedprefManager.getInstance(LoginPage.this)
                        .saveToken(token);
                // then go to the Mainactivity
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                // for clearing tasks
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginPage.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
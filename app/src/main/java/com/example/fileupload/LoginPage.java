package com.example.fileupload;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.fileupload.databinding.ActivityLoginPageBinding;

import okhttp3.ResponseBody;
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
        binding= ActivityLoginPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        signin();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(SharedprefManager.getInstance(this).isLoggedIn()){
            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void signin() {

        //email = binding.inemail.getText().toString();
       // password = binding.inpassword.getText().toString();

        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = binding.inemail.getText().toString().trim();
                password = binding.inpassword.getText().toString();
                // checking for empty email
                if(email.isEmpty()){
                    binding.inemail.setError("Email is required");
                    binding.inemail.requestFocus();
                    return;
                }
                // for valid email
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.inemail.setError("Email is required");
                    binding.inemail.requestFocus();
                    return;
                }
                Intent intent = new Intent(LoginPage.this,MainActivity.class);
                startActivity(intent);

                // makerequest();
            }
        });
    }

    private void makerequest() {
        // creating user with email and password using retrofit
        Call<ResponseBody> call = RetrofitClientSingleton.getInstance()
                .getApi()
                .createUser(email, password);
        // make a queue to send request
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String Token = response.body().toString();

                // now save the data in shared prefernce
                SharedprefManager.getInstance(LoginPage.this)
                        .saveUser(Token);
                // then go to the Mainactivity
                Intent intent = new Intent(LoginPage.this,MainActivity.class);
                // for clearing tasks
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginPage.this, t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


}
package com.example.furrytrackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.furrytrackapp.BasicPetsWind.PetActivity;
import com.example.furrytrackapp.Model.Users;
import com.example.furrytrackapp.Utils.ApiClient;
import com.example.furrytrackapp.Utils.ApiService;
import com.example.furrytrackapp.Welcome.LoginActivity;
import com.example.furrytrackapp.Welcome.RegisterActivity;
import com.example.furrytrackapp.R;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Button joinButton;
    private Button loginButton;
    private ProgressDialog loadingBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = ApiClient.getClient().create(ApiService.class);

        joinButton = findViewById(R.id.main_join_button);
        loginButton = findViewById(R.id.add_account_button);
        loadingBar = new ProgressDialog(this);
        Paper.init(this);

        loginButton.setOnClickListener(view -> {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });

        joinButton.setOnClickListener(v -> {
            Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        });

        if (Paper.book().contains("user_email") && Paper.book().contains("user_password")) {
            String userEmail = Paper.book().read("user_email");
            String userPassword = Paper.book().read("user_password");

            if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword)) {
                ProgressDialog loadingBar = new ProgressDialog(this);
                loadingBar.setTitle("Auto Login");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                autoLoginUser(userEmail, userPassword, loadingBar);
            }
        }
    }

    private void autoLoginUser(String email, String password, ProgressDialog loadingBar) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loadingBar.dismiss();
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        Users loginRequest = new Users();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        Call<Users> call = apiService.loginUser(loginRequest);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                loadingBar.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    Users user = response.body();
                    handleSuccessfulLogin(email, user);
                } else {
                    Toast.makeText(MainActivity.this,
                            "Login failed: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                loadingBar.dismiss();
                Toast.makeText(MainActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSuccessfulLogin(String email, Users user) {
        Paper.book().write("user_email", email);
        Paper.book().write("user_token", user.getToken());

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();

        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, PetActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
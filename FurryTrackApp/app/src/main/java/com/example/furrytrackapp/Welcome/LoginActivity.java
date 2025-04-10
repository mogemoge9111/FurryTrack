package com.example.furrytrackapp.Welcome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.furrytrackapp.Model.Users;
import com.example.furrytrackapp.BasicPetsWind.PetActivity;
import com.example.furrytrackapp.Prevalent.Prevalent;
import com.example.furrytrackapp.R;
import com.rey.material.widget.CheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private ProgressDialog loadingBar;
    private CheckBox checkBoxRememberMe;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String API_URL = "http://10.0.2.2:8080/api/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Paper.init(this);

        emailInput = findViewById(R.id.login_email_input);
        passwordInput = findViewById(R.id.login_password_input);
        checkBoxRememberMe = findViewById(R.id.login_checkbox);
        Button loginButton = findViewById(R.id.login_button);
        loadingBar = new ProgressDialog(this);

        if (Paper.book().contains(Prevalent.USER_EMAIL_KEY)) {
            String savedEmail = Paper.book().read(Prevalent.USER_EMAIL_KEY);
            String savedPassword = Paper.book().read(Prevalent.USER_PASSWORD_KEY);
            emailInput.setText(savedEmail);
            passwordInput.setText(savedPassword);
            checkBoxRememberMe.setChecked(true);
        }

        loginButton.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Введите email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Неверный формат email", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingBar.setTitle("Вход");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        sendLoginRequest(email, password);
    }

    private void sendLoginRequest(String email, String password) {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Ошибка подключения", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                runOnUiThread(() -> loadingBar.dismiss());

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONObject data = jsonResponse.getJSONObject("data");
                        String token = data.getString("token");
                        JSONObject userJson = data.getJSONObject("user");

                        Users user = new Users();
                        user.setId(userJson.getString("ID"));
                        user.setUsername(userJson.getString("username"));
                        user.setEmail(email);
                        user.setPassword(password);
                        user.setRole(userJson.getString("role"));

                        Prevalent.currentOnlineUser = user;
                        Prevalent.currentToken = token;

                        if (checkBoxRememberMe.isChecked()) {
                            Paper.book().write(Prevalent.USER_EMAIL_KEY, email);
                            Paper.book().write(Prevalent.USER_PASSWORD_KEY, password);
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.putString("token", token);
                        editor.apply();

                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Вход выполнен", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, PetActivity.class));
                            finish();
                        });

                    } catch (JSONException e) {
                        runOnUiThread(() ->
                                Toast.makeText(LoginActivity.this, "Ошибка обработки ответа", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> {
                        if (response.code() == 401) {
                            Toast.makeText(LoginActivity.this, "Неверный email или пароль", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Ошибка входа: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
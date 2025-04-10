package com.example.furrytrackapp.Welcome;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.furrytrackapp.BasicPetsWind.PetActivity;
import com.example.furrytrackapp.Model.Users;
import com.example.furrytrackapp.Prevalent.Prevalent;
import com.example.furrytrackapp.R;
import com.example.furrytrackapp.Utils.ApiClient;
import com.example.furrytrackapp.Utils.ApiService;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "FurryTrackPrefs";
    private static final String VET_NOTIFICATIONS_KEY = "vet_notifications";
    private static final String BIRTHDAY_NOTIFICATIONS_KEY = "birthday_notifications";
    private ApiService apiService;
    private UUID userId;
    private String authToken;
    private EditText usernameEditText, emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        apiService = ApiClient.getClient().create(ApiService.class);
        authToken = "Bearer " + Prevalent.currentToken;

        usernameEditText = findViewById(R.id.settings_fullname);
        emailEditText = findViewById(R.id.settings_email);

        Switch vetNotificationsSwitch = findViewById(R.id.vet_notifications_switch);
        Switch birthdayNotificationsSwitch = findViewById(R.id.birthday_notifications_switch);

        vetNotificationsSwitch.setChecked(sharedPreferences.getBoolean(VET_NOTIFICATIONS_KEY, false));
        birthdayNotificationsSwitch.setChecked(sharedPreferences.getBoolean(BIRTHDAY_NOTIFICATIONS_KEY, false));

        vetNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean(VET_NOTIFICATIONS_KEY, isChecked).apply());

        birthdayNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean(BIRTHDAY_NOTIFICATIONS_KEY, isChecked).apply());

        loadUserInfo();
    }

    private void loadUserInfo() {
        Users dummyUser = new Users();
        dummyUser.setUsername("Test User");
        dummyUser.setEmail("test@example.com");

        usernameEditText.setText(dummyUser.getUsername());
        emailEditText.setText(dummyUser.getEmail());

        Call<Users> call = apiService.getUser(userId.toString());
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Users user = response.body();
                    usernameEditText.setText(user.getUsername());
                    emailEditText.setText(user.getEmail());
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateUserInfo() {
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();

        Users user = new Users();
        user.setId(userId.toString());
        user.setUsername(username);
        user.setEmail(email);

        Call<Users> call = apiService.updateUser(userId.toString(), user);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, "Данные обновлены", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
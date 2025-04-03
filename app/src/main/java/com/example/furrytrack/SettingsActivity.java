package com.example.furrytrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "FurryTrackPrefs";
    private static final String VET_NOTIFICATIONS_KEY = "vet_notifications";
    private static final String BIRTHDAY_NOTIFICATIONS_KEY = "birthday_notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Switch vetNotificationsSwitch = findViewById(R.id.vet_notifications_switch);
        Switch birthdayNotificationsSwitch = findViewById(R.id.birthday_notifications_switch);

        vetNotificationsSwitch.setChecked(sharedPreferences.getBoolean(VET_NOTIFICATIONS_KEY, false));
        birthdayNotificationsSwitch.setChecked(sharedPreferences.getBoolean(BIRTHDAY_NOTIFICATIONS_KEY, false));

        vetNotificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(VET_NOTIFICATIONS_KEY, isChecked).apply();
            }
        });

        birthdayNotificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(BIRTHDAY_NOTIFICATIONS_KEY, isChecked).apply();
            }
        });
    }
}
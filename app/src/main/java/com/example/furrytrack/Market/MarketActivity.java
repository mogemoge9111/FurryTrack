package com.example.furrytrack.Market;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.furrytrack.BasicPetsWind.PetActivity;
import com.example.furrytrack.Market.CategoryPetActivity;
import com.example.furrytrack.Profile.ProfileUserActivity;
import com.example.furrytrack.R;
import com.example.furrytrack.SettingsActivity;
import com.example.furrytrack.Welcome.MainActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rey.material.widget.FloatingActionButton;

import io.paperdb.Paper;

public class MarketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);

        // Инициализация FAB
        FloatingActionButton fabCategory = findViewById(R.id.fab_category);
        fabCategory.setOnClickListener(v -> {
            Intent intent = new Intent(MarketActivity.this, CategoryPetActivity.class);
            startActivity(intent);
        });

        // Настройка нижней навигации
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_market);

        // Настройка тулбара
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_market) {
                        return true;
                    } else if (itemId == R.id.nav_pets) {
                        startActivity(new Intent(MarketActivity.this, PetActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (itemId == R.id.nav_profile) {
                        startActivity(new Intent(MarketActivity.this, ProfileUserActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }
                    return false;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MenuDebug", "Creating options menu");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutConfirmation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Да", (dialog, which) -> {
                    Paper.book().destroy();
                    getSharedPreferences("UserData", MODE_PRIVATE).edit().clear().apply();

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("О программе")
                .setMessage("НИУ ВШЭ Курсовая работа\n\nВыполнили: Замотаева Анастасия Владимировна, ФКН БПИ239\n\nЩуплова Анна Игоревна, ФКН БПИ239")
                .setPositiveButton("OK", null)
                .show();
    }
}
package com.example.furrytrackapp.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.furrytrackapp.BasicPetsWind.PetActivity;
import com.example.furrytrackapp.Market.MarketActivity;
import com.example.furrytrackapp.Model.Users;
import com.example.furrytrackapp.Posts.PostsActivity;
import com.example.furrytrackapp.R;
import com.example.furrytrackapp.Utils.ApiClient;
import com.example.furrytrackapp.Utils.ApiService;
import com.example.furrytrackapp.Welcome.SettingsActivity;
import com.example.furrytrackapp.MainActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUserActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private TextView userNameTextView, userEmailTextView;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_profile_user);

        apiService = ApiClient.getClient().create(ApiService.class);

        profileImageView = findViewById(R.id.user_profile_image);
        userNameTextView = findViewById(R.id.user_profile_name);
        userEmailTextView = findViewById(R.id.user_email);

        Paper.init(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        loadUserData();
    }

    private void loadUserData() {
        String token = Paper.book().read("token", "");
        if (token.isEmpty()) {
            redirectToLogin();
            return;
        }

        Call<Users> call = apiService.getCurrentUser("Bearer " + token);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayUserData(response.body());
                } else {
                    Toast.makeText(ProfileUserActivity.this,
                            "Failed to load user data",
                            Toast.LENGTH_SHORT).show();
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(ProfileUserActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUserData(Users user) {
        userNameTextView.setText(user.getName());
        userEmailTextView.setText(user.getEmail());

        if (user.getImage() != null && !user.getImage().isEmpty()) {
            Picasso.get()
                    .load(user.getImage())
                    .placeholder(R.drawable.menu_user)
                    .error(R.drawable.menu_user)
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.menu_user);
        }
    }

    private void redirectToLogin() {
        Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_profile) {
                        return true;
                    } else if (itemId == R.id.nav_posts) {
                        startActivity(new Intent(ProfileUserActivity.this, PostsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (itemId == R.id.nav_pets) {
                        startActivity(new Intent(ProfileUserActivity.this, PetActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (itemId == R.id.nav_market) {
                        startActivity(new Intent(ProfileUserActivity.this, MarketActivity.class));
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
                .setMessage("НИУ ВШЭ Курсовая работа" +
                        "\n\nВыполнили: Замотаева Анастасия Владимировна, ФКН БПИ239" +
                        "\n\nЩуплова Анна Игоревна, ФКН БПИ239")
                .setPositiveButton("OK", null)
                .show();
    }
}
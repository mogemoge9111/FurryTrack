package com.example.furrytrackapp.Market;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furrytrackapp.BasicPetsWind.PetActivity;
import com.example.furrytrackapp.Market.CategoryPetActivity;
import com.example.furrytrackapp.Model.Pet;
import com.example.furrytrackapp.Posts.PostsActivity;
import com.example.furrytrackapp.Profile.ProfileUserActivity;
import com.example.furrytrackapp.R;
import com.example.furrytrackapp.Utils.ApiClient;
import com.example.furrytrackapp.Utils.ApiService;
import com.example.furrytrackapp.Welcome.SettingsActivity;
import com.example.furrytrackapp.MainActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rey.material.widget.FloatingActionButton;

import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ApiService apiService;
    private MarketAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize FAB
        FloatingActionButton fabCategory = findViewById(R.id.fab_category);
        fabCategory.setOnClickListener(v -> {
            Intent intent = new Intent(MarketActivity.this, CategoryPetActivity.class);
            startActivity(intent);
        });

        // Setup bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_market);

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize adapter with empty list
        adapter = new MarketAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadPets();
    }

    private void loadPets() {
        Call<List<Pet>> call = apiService.getUserPets("Bearer " + getToken());
        call.enqueue(new Callback<List<Pet>>() {
            @Override
            public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setPetsList(response.body());
                } else {
                    Log.e("API Error", "Failed to load pets: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Pet>> call, Throwable t) {
                Log.e("API Error", "Failed to load pets", t);
            }
        });
    }

    private String getToken() {
        return Paper.book().read("token", "");
    }

    // Inner ViewHolder class
    public static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView txtPetName, txtPetInfo, txtPetPrice;
        ImageView imageView;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPetName = itemView.findViewById(R.id.pet_name);
            txtPetInfo = itemView.findViewById(R.id.pet_info);
            txtPetPrice = itemView.findViewById(R.id.pet_price);
            imageView = itemView.findViewById(R.id.pet_image);
        }
    }

    // Adapter class
    private class MarketAdapter extends RecyclerView.Adapter<PetViewHolder> {
        private List<Pet> petsList;

        public void setPetsList(List<Pet> petsList) {
            this.petsList = petsList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.market_pet_items_layout, parent, false);
            return new PetViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
            Pet pet = petsList.get(position);
            holder.txtPetName.setText(pet.getName());
            holder.txtPetInfo.setText(pet.getInfo());
            holder.txtPetPrice.setText("Стоимость = " + pet.getPrice() + "₽");

            if (pet.getImage() != null && !pet.getImage().isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(pet.getImage(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.imageView.setImageBitmap(decodedByte);
                } catch (IllegalArgumentException e) {
                    Log.e("ImageError", "Failed to decode image", e);
                }
            }
        }

        @Override
        public int getItemCount() {
            return petsList != null ? petsList.size() : 0;
        }
    }

    // Rest of the code remains the same...
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_market) {
                        return true;
                    } else if (itemId == R.id.nav_posts) {
                        startActivity(new Intent(MarketActivity.this, PostsActivity.class));
                        overridePendingTransition(0, 0);
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
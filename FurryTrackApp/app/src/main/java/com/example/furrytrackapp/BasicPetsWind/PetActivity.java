package com.example.furrytrackapp.BasicPetsWind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furrytrackapp.Market.MarketActivity;
import com.example.furrytrackapp.Model.Pet;
import com.example.furrytrackapp.Model.PetRecord;
import com.example.furrytrackapp.Model.Post;
import com.example.furrytrackapp.Model.Users;
import com.example.furrytrackapp.Posts.PostsActivity;
import com.example.furrytrackapp.Prevalent.Prevalent;
import com.example.furrytrackapp.Profile.ProfileUserActivity;
import com.example.furrytrackapp.R;
import com.example.furrytrackapp.Utils.PostRequest;
import com.example.furrytrackapp.Utils.PostResponse;
import com.example.furrytrackapp.Welcome.SettingsActivity;
import com.example.furrytrackapp.Utils.ApiService;
import com.example.furrytrackapp.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetActivity extends AppCompatActivity {
    private List<Pet> pets = new ArrayList<>();
    private PetAdapter adapter;
    private ApiService apiService;
    private String authToken;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        initializeMockData();

        setupViews();
        loadMockPets();
    }

    private void initializeMockData() {
        apiService = new MockApiService();
        authToken = "Bearer mockToken";
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
    }

    private void setupViews() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_pets) {
                return true;
            } else if (itemId == R.id.nav_posts) {
                startActivity(new Intent(PetActivity.this, PostsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_market) {
                startActivity(new Intent(PetActivity.this, MarketActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(PetActivity.this, ProfileUserActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
        bottomNav.setSelectedItemId(R.id.nav_pets);

        RecyclerView recyclerView = findViewById(R.id.pets_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        Button addButton = findViewById(R.id.add_pet_button);
        addButton.setOnClickListener(v ->
                startActivity(new Intent(this, AddPetActivity.class)));
    }

    private void loadMockPets() {
        new android.os.Handler().postDelayed(() -> {
            pets.clear();
            adapter.notifyDataSetChanged();
        }, 1000);
    }

    private void openPetDetails(Pet pet) {
        Intent intent = new Intent(this, PetDetailsActivity.class);
        intent.putExtra("pet", pet);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                    sharedPreferences.edit().clear().apply();
                    Prevalent.currentToken = null;
                    Prevalent.currentOnlineUser = null;
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

    private class MockApiService implements ApiService {
        @Override
        public Call<List<Pet>> getUserPets(String token) {
            return new MockCall<>(getMockPets());
        }

        private List<Pet> getMockPets() {
            return java.util.Collections.emptyList();
        }

        @Override
        public Call<PetRecord> createRecord(String token, PetRecord record) { return null; }
        @Override
        public Call<PetRecord> updateRecord(String token, String id, PetRecord record) { return null; }
        @Override
        public Call<Pet> getPetById(String petId) { return null; }
        @Override
        public Call<List<PetRecord>> getPetRecords(String petId) { return null; }
        @Override
        public Call<Void> deleteRecord(String recordId) { return null; }
        @Override
        public Call<Users> getCurrentUser(String token) { return null; }
        @Override
        public Call<Users> updateUser(String token, Users user) { return null; }
        @Override
        public Call<Pet> createPet(String token, Pet pet) { return null; }
        @Override
        public Call<PostResponse> createPost(String s, PostRequest postRequest) { return null; }
        @Override
        public Call<List<Post>> getFeed() { return null; }
    }

    private class MockCall<T> implements Call<T> {
        private final T response;

        MockCall(T response) {
            this.response = response;
        }

        @Override
        public void enqueue(Callback<T> callback) {
            callback.onResponse(this, Response.success(response));
        }

        @Override
        public Response<T> execute() { return null; }
        @Override
        public boolean isExecuted() { return false; }
        @Override
        public void cancel() {}
        @Override
        public boolean isCanceled() { return false; }
        @Override
        public Call<T> clone() { return null; }
        @Override
        public Request request() { return null; }

        @Override
        public Timeout timeout() {
            return null;
        }
    }
}
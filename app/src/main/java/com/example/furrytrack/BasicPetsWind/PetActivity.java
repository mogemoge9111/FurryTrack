package com.example.furrytrack.BasicPetsWind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.furrytrack.Market.MarketActivity;
import com.example.furrytrack.Profile.ProfileUserActivity;
import com.example.furrytrack.R;
import com.example.furrytrack.SettingsActivity;
import com.example.furrytrack.Welcome.MainActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;

public class PetActivity extends AppCompatActivity {

    private static final int ADD_PET_REQUEST_CODE = 1;
    private final List<Pet> pets = new ArrayList<>();
    private PetAdapter adapter;
    private SharedPreferences sharedPreferences;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_pets);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button addButton = findViewById(R.id.add_pet_button);
        RecyclerView recyclerView = findViewById(R.id.pets_recycler_view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Мои питомцы");
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new PetAdapter(pets, this::openPetDetails);
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v ->
                startActivityForResult(
                        new Intent(this, AddPetActivity.class),
                        ADD_PET_REQUEST_CODE
                )
        );

        sharedPreferences = getSharedPreferences("FurryTrackPrefs", MODE_PRIVATE);
        loadPets();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_pets) {
                        // Уже в PetActivity
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
                }
            };

    private void openPetDetails(Pet pet) {
        Intent intent = new Intent(this, PetDetailsActivity.class);
        intent.putExtra("pet", pet);
        startActivity(intent);
    }

    private void loadPets() {
        String json = sharedPreferences.getString("pets_" + currentUserEmail, null);
        if (json == null) return;

        try {
            JSONArray jsonArray = new JSONArray(json);
            pets.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                pets.add(new Pet(
                        obj.getString("name"),
                        obj.getString("type"),
                        obj.getString("gender"),
                        new Date(obj.getLong("birthDate")),
                        new Date(obj.getLong("adoptionDate")),
                        obj.getString("importantInfo"),
                        new Date(obj.getLong("lastVetVisit")),
                        new Date(obj.getLong("lastWalk")),
                        new Date(obj.getLong("lastWash"))
                ));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e("PetActivity", "Ошибка загрузки: ", e);
            Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == ADD_PET_REQUEST_CODE && resultCode == RESULT_OK) {
                if (data != null && data.hasExtra("new_pet")) {
                    Pet newPet = (Pet) data.getSerializableExtra("new_pet");
                    if (newPet != null) {
                        pets.add(newPet);
                        savePets();
                        adapter.notifyItemInserted(pets.size() - 1);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("PetActivity", "Ошибка получения данных", e);
            Toast.makeText(this, "Ошибка при добавлении питомца", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePets() {
        new Thread(() -> {
            try {
                JSONArray jsonArray = new JSONArray();
                for (Pet pet : pets) {
                    JSONObject obj = new JSONObject();
                    obj.put("name", pet.getName());
                    obj.put("type", pet.getType());
                    obj.put("gender", pet.getGender());
                    obj.put("birthDate", pet.getBirthDate().getTime());
                    obj.put("adoptionDate", pet.getAdoptionDate().getTime());
                    obj.put("importantInfo", pet.getImportantInfo());
                    obj.put("lastVetVisit", pet.getLastVetVisit().getTime());
                    obj.put("lastWalk", pet.getLastWalk().getTime());
                    obj.put("lastWash", pet.getLastWash().getTime());
                    jsonArray.put(obj);
                }

                sharedPreferences.edit()
                        .putString("pets_" + currentUserEmail, jsonArray.toString())
                        .apply();

            } catch (Exception e) {
                Log.e("PetActivity", "Ошибка сохранения", e);
            }
        }).start();
    }
    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MenuDebug", "Creating options menu");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("MenuDebug", "Preparing options menu");
        return super.onPrepareOptionsMenu(menu);
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
                        "\n\nВыполнили: Замотаева Анастасия Владимировна, ФКН БПИ239"
                        +
                        "\n\nЩуплова Анна Игоревна, ФКН БПИ239")
                .setPositiveButton("OK", null)
                .show();
    }

    private void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void logout() {
        finish();
    }
}
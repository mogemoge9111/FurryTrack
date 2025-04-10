package com.example.furrytrackapp.BasicPetsWind;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furrytrackapp.Model.Pet;
import com.example.furrytrackapp.Model.PetRecord;
import com.example.furrytrackapp.R;
import com.example.furrytrackapp.Utils.ApiClient;
import com.example.furrytrackapp.Utils.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetDetailsActivity extends AppCompatActivity {
    private static final int ADD_RECORD_REQUEST_CODE = 100;
    private static final int EDIT_RECORD_REQUEST_CODE = 101;

    private Pet pet;
    private List<PetRecord> records = new ArrayList<>();
    private PetRecordsAdapter adapter;
    private ApiService apiService;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_details);

        pet = (Pet) getIntent().getSerializableExtra("pet");
        if (pet == null) {
            finish();
            return;
        }

        apiService = ApiClient.getClient().create(ApiService.class);
        initViews();
        setupRecyclerView();
        setupButtons();
        setupFilterSpinner();
        loadRecords();
    }

    private void initViews() {
        TextView petNameTextView = findViewById(R.id.pet_name_text_view);
        TextView petTypeTextView = findViewById(R.id.pet_type_text_view);
        petNameTextView.setText(pet.getName());
        petTypeTextView.setText(String.format("%s, %s", pet.getType(), pet.getBreed()));
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.records_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PetRecordsAdapter(records, new PetRecordsAdapter.OnRecordClickListener() {
            @Override
            public void onRecordClick(PetRecord record) {
                openEditRecordActivity(record);
            }

            @Override
            public void onRecordLongClick(PetRecord record) {
                deleteRecord(record);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupFilterSpinner() {
        Spinner filterSpinner = findViewById(R.id.filter_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.record_filter_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                currentFilter = selected.equals("Все записи") ? "all" : selected.toLowerCase();
                applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadRecords() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Загрузка записей...");
        progressDialog.show();

        Call<List<PetRecord>> call = apiService.getPetRecords(pet.getId());
        call.enqueue(new Callback<List<PetRecord>>() {
            @Override
            public void onResponse(Call<List<PetRecord>> call, Response<List<PetRecord>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    records.clear();
                    records.addAll(response.body());
                    applyFilter();
                } else {
                    Toast.makeText(PetDetailsActivity.this, "Ошибка загрузки записей", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PetRecord>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PetDetailsActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter() {
        List<PetRecord> filteredRecords = new ArrayList<>();

        if (currentFilter.equals("all")) {
            filteredRecords.addAll(records);
        } else {
            for (PetRecord record : records) {
                if (record.getCategory().equalsIgnoreCase(currentFilter)) {
                    filteredRecords.add(record);
                }
            }
        }

        adapter.updateRecords(filteredRecords);
    }

    private void setupButtons() {
        FloatingActionButton addRecordFab = findViewById(R.id.add_record_fab);
        addRecordFab.setOnClickListener(v -> openAddRecordActivity());
    }

    private void openAddRecordActivity() {
        Intent intent = new Intent(this, AddPetRecordActivity.class);
        intent.putExtra("petId", pet.getId());
        startActivityForResult(intent, ADD_RECORD_REQUEST_CODE);
    }

    private void openEditRecordActivity(PetRecord record) {
        Intent intent = new Intent(this, AddPetRecordActivity.class);
        intent.putExtra("petId", pet.getId());
        intent.putExtra("record", (CharSequence) record);
        startActivityForResult(intent, EDIT_RECORD_REQUEST_CODE);
    }

    private void deleteRecord(PetRecord record) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление записи")
                .setMessage("Вы уверены, что хотите удалить эту запись?")
                .setPositiveButton("Да", (dialog, which) -> {
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Удаление...");
                    progressDialog.show();

                    Call<Void> call = apiService.deleteRecord(record.getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            progressDialog.dismiss();
                            if (response.isSuccessful()) {
                                loadRecords();
                            } else {
                                Toast.makeText(PetDetailsActivity.this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(PetDetailsActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&
                (requestCode == ADD_RECORD_REQUEST_CODE || requestCode == EDIT_RECORD_REQUEST_CODE)) {
            loadRecords();
        }
    }
}
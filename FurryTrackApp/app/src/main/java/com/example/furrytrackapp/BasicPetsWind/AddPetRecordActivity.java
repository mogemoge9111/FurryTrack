package com.example.furrytrackapp.BasicPetsWind;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.furrytrackapp.Model.PetRecord;
import com.example.furrytrackapp.Prevalent.Prevalent;
import com.example.furrytrackapp.R;
import com.example.furrytrackapp.Utils.ApiClient;
import com.example.furrytrackapp.Utils.ApiService;

import java.io.IOException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPetRecordActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Spinner categorySpinner;
    private EditText titleEditText, descriptionEditText;
    private ImageView recordImageView;
    private Button addPhotoButton, saveButton;

    private Bitmap recordImageBitmap;
    private String recordImageBase64;
    private PetRecord currentRecord;
    private boolean isEditMode = false;
    private String petId;
    private ApiService apiService;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet_record);

        apiService = ApiClient.getClient().create(ApiService.class);
        authToken = "Bearer " + Prevalent.currentToken;
        petId = getIntent().getStringExtra("petId");

        initViews();
        setupSpinner();
        checkEditMode();
        setupButtons();
    }

    private void initViews() {
        categorySpinner = findViewById(R.id.category_spinner);
        titleEditText = findViewById(R.id.record_title_edit_text);
        descriptionEditText = findViewById(R.id.record_description_edit_text);
        recordImageView = findViewById(R.id.record_image_view);
        addPhotoButton = findViewById(R.id.add_record_photo_button);
        saveButton = findViewById(R.id.save_record_button);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pet_record_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void checkEditMode() {
        if (getIntent().hasExtra("record")) {
            isEditMode = true;
            currentRecord = (PetRecord) getIntent().getSerializableExtra("record");
            fillFormWithRecordData();
        } else {
            currentRecord = new PetRecord();
            currentRecord.setPetId(petId);
            currentRecord.setDate(new Date());
        }
    }

    private void fillFormWithRecordData() {
        titleEditText.setText(currentRecord.getTitle());
        descriptionEditText.setText(currentRecord.getDescription());

        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) categorySpinner.getAdapter();
        int position = adapter.getPosition(currentRecord.getCategory());
        if (position >= 0) {
            categorySpinner.setSelection(position);
        }

        if (currentRecord.getImageBase64() != null) {
            Bitmap image = null;
            recordImageView.setImageBitmap(image);
            recordImageBase64 = currentRecord.getImageBase64();
        }
    }

    private void setupButtons() {
        addPhotoButton.setOnClickListener(v -> openImageChooser());

        saveButton.setOnClickListener(v -> {
            if (validateInput()) {
                saveRecord();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                recordImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                recordImageView.setImageBitmap(recordImageBitmap);
                recordImageBase64 = PetRecord.bitmapToBase64(recordImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInput() {
        if (titleEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Введите название записи", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveRecord() {
        currentRecord.setTitle(titleEditText.getText().toString().trim());
        currentRecord.setDescription(descriptionEditText.getText().toString().trim());
        currentRecord.setCategory(categorySpinner.getSelectedItem().toString());
        currentRecord.setImageBase64(recordImageBase64);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Сохранение...");
        progressDialog.show();

        Call<PetRecord> call;
        if (isEditMode) {
            call = apiService.updateRecord(authToken, currentRecord.getId(), currentRecord);
        } else {
            call = apiService.createRecord(authToken, currentRecord);
        }

        call.enqueue(new Callback<PetRecord>() {
            @Override
            public void onResponse(Call<PetRecord> call, Response<PetRecord> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddPetRecordActivity.this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PetRecord> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AddPetRecordActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
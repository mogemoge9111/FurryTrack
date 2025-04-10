package com.example.furrytrackapp.BasicPetsWind;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.furrytrackapp.Model.Pet;
import com.example.furrytrackapp.Prevalent.Prevalent;
import com.example.furrytrackapp.R;
import com.example.furrytrackapp.Utils.ApiClient;
import com.example.furrytrackapp.Utils.ApiService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPetActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText petNameEditText, importantInfoEditText;
    private Spinner petTypeSpinner, petGenderSpinner;
    private ImageView petImageView;
    private Button birthDateButton, adoptionDateButton, saveButton, addPhotoButton;
    private Calendar birthDate = Calendar.getInstance();
    private Calendar adoptionDate = Calendar.getInstance();
    private Bitmap petImageBitmap;
    private String petImageBase64;
    private ApiService apiService;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        apiService = ApiClient.getClient().create(ApiService.class);
        authToken = "Bearer " + Prevalent.currentToken;

        initViews();
        setupDatePickers();
        setupButtons();
    }

    private void initViews() {
        petNameEditText = findViewById(R.id.pet_name_edit_text);
        importantInfoEditText = findViewById(R.id.important_info_edit_text);
        petTypeSpinner = findViewById(R.id.pet_type_spinner);
        petGenderSpinner = findViewById(R.id.pet_gender_spinner);
        petImageView = findViewById(R.id.pet_image_view);
        birthDateButton = findViewById(R.id.birth_date_button);
        adoptionDateButton = findViewById(R.id.adoption_date_button);
        saveButton = findViewById(R.id.save_pet_button);
        addPhotoButton = findViewById(R.id.add_photo_button);
    }

    private void setupDatePickers() {
        birthDateButton.setOnClickListener(v -> showDatePicker(birthDate, birthDateButton, "Дата рождения"));
        adoptionDateButton.setOnClickListener(v -> showDatePicker(adoptionDate, adoptionDateButton, "Дата появления в семье"));
        updateButtonText(birthDateButton, birthDate);
        updateButtonText(adoptionDateButton, adoptionDate);
    }

    private void showDatePicker(Calendar calendar, Button button, String title) {
        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    updateButtonText(button, calendar);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateButtonText(Button button, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        button.setText(sdf.format(calendar.getTime()));
    }

    private void setupButtons() {
        addPhotoButton.setOnClickListener(v -> openImageChooser());
        saveButton.setOnClickListener(v -> {
            if (validateInput()) {
                savePet();
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
                petImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                petImageView.setImageBitmap(petImageBitmap);
                petImageBase64 = Pet.bitmapToBase64(petImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(petNameEditText.getText().toString().trim())) {
            Toast.makeText(this, "Введите имя питомца", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void savePet() {
        String name = petNameEditText.getText().toString().trim();
        String type = petTypeSpinner.getSelectedItem().toString();
        String gender = petGenderSpinner.getSelectedItem().toString();
        String importantInfo = importantInfoEditText.getText().toString().trim();

        if (petImageBase64 == null) {
            petImageBase64 = getDefaultPetImageBase64();
        }

        Pet newPet = new Pet(
                null,
                name,
                type,
                gender,
                birthDate.getTime(),
                adoptionDate.getTime(),
                importantInfo,
                petImageBase64,
                Prevalent.currentOnlineUser.getId()
        );

        Call<Pet> call = apiService.createPet(authToken, newPet);
        call.enqueue(new Callback<Pet>() {
            @Override
            public void onResponse(Call<Pet> call, Response<Pet> response) {
                if (response.isSuccessful()) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("new_pet", response.body());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(AddPetActivity.this, "Ошибка создания питомца", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) {
                Toast.makeText(AddPetActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getDefaultPetImageBase64() {
        Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.select_pet_image);
        return Pet.bitmapToBase64(defaultBitmap);
    }
}
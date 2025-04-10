package com.example.furrytrackapp.Market;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.furrytrackapp.Model.Pet;
import com.example.furrytrackapp.R;
import com.example.furrytrackapp.Utils.ApiClient;
import com.example.furrytrackapp.Utils.ApiService;
import com.rey.material.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewPetActivity extends AppCompatActivity {

    private static final int GALLERYPICK = 1;
    private String categoryName, breed, price, description, name;
    private ImageView petImage;
    private EditText petName, petBreed, petPrice, petDescription;
    private Spinner petGenderSpinner;
    private Button addNewPetButton, birthDateButton;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private Calendar birthDate = Calendar.getInstance();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_pet);

        apiService = ApiClient.getClient().create(ApiService.class);

        initViews();
        setupListeners();
    }

    private void initViews() {
        petImage = findViewById(R.id.select_pet_image);
        petName = findViewById(R.id.pet_name);
        petBreed = findViewById(R.id.pet_type);
        petPrice = findViewById(R.id.price);
        petDescription = findViewById(R.id.pet_info);
        petGenderSpinner = findViewById(R.id.pet_gender_spinner);
        addNewPetButton = findViewById(R.id.btn_app_new_pet);
        birthDateButton = findViewById(R.id.birth_date_button);

        loadingBar = new ProgressDialog(this);
        loadingBar.setCancelable(false);

        categoryName = getIntent().getStringExtra("category");
    }

    private void setupListeners() {
        petImage.setOnClickListener(v -> openGallery());
        addNewPetButton.setOnClickListener(v -> validatePetData());
        birthDateButton.setOnClickListener(v -> showDatePickerDialog());
        updateBirthDateButtonText();
    }

    private void validatePetData() {
        breed = petBreed.getText().toString().trim();
        price = petPrice.getText().toString().trim();
        description = petDescription.getText().toString().trim();
        name = petName.getText().toString().trim();
        String gender = petGenderSpinner.getSelectedItem().toString();

        if (ImageUri == null) {
            showToast("Добавьте изображение питомца");
            return;
        }

        if (TextUtils.isEmpty(name)) {
            showToast("Введите кличку питомца");
            return;
        }

        if (TextUtils.isEmpty(breed)) {
            showToast("Укажите породу питомца");
            return;
        }

        if (TextUtils.isEmpty(price)) {
            showToast("Введите цену питомца");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            showToast("Добавьте описание питомца");
            return;
        }

        storePetInformation(gender);
    }

    private void storePetInformation(String gender) {
        showLoadingDialog("Сохранение данных", "Пожалуйста, подождите...");

        String encodedImage = encodeImage(ImageUri);
        if (encodedImage == null) {
            loadingBar.dismiss();
            showToast("Ошибка обработки изображения");
            return;
        }

        // Create pet object
        Pet pet = new Pet();
        pet.setName(name);
        pet.setBreed(breed);
        pet.setType(categoryName);
        pet.setGender(gender);
        pet.setPrice(price);
        pet.setDescription(description);
        pet.setImage(encodedImage);

        Call<Pet> call = apiService.createPet("Bearer " + getAuthToken(), pet);
        call.enqueue(new Callback<Pet>() {
            @Override
            public void onResponse(Call<Pet> call, Response<Pet> response) {
                loadingBar.dismiss();
                if (response.isSuccessful()) {
                    showToast("Объявление добавлено");
                    Intent intent = new Intent(AddNewPetActivity.this, MarketActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    showToast("Ошибка: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) {
                loadingBar.dismiss();
                showToast("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private String getAuthToken() {
        return "";
    }

    private String encodeImage(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFormattedBirthDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return sdf.format(birthDate.getTime());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), GALLERYPICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERYPICK && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            petImage.setImageURI(ImageUri);
        }
    }

    private void showDatePickerDialog() {
        new android.app.DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    birthDate.set(year, month, dayOfMonth);
                    updateBirthDateButtonText();
                },
                birthDate.get(Calendar.YEAR),
                birthDate.get(Calendar.MONTH),
                birthDate.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateBirthDateButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        birthDateButton.setText(sdf.format(birthDate.getTime()));
    }

    private void showLoadingDialog(String title, String message) {
        loadingBar.setTitle(title);
        loadingBar.setMessage(message);
        loadingBar.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
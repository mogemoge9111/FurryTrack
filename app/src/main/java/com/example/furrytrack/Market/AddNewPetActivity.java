package com.example.furrytrack.Market;

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

import com.example.furrytrack.R;
import com.example.furrytrack.Welcome.LoginActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rey.material.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AddNewPetActivity extends AppCompatActivity {

    private static final int GALLERYPICK = 1;
    private String categoryName, Type, Price, Info, PName, saveCurrentDate, saveCurrentTime, productRandomKey;
    private ImageView petImage;
    private EditText petName, petType, petPrice, petInfo;
    private Spinner petGenderSpinner;
    private Button addNewPetButton, birthDateButton;
    private Uri ImageUri;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;
    private Calendar birthDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_pet);

        // Инициализация компонентов
        initViews();
        setupFirebase();
        setupListeners();
    }

    private void initViews() {
        petImage = findViewById(R.id.select_pet_image);
        petName = findViewById(R.id.pet_name);
        petType = findViewById(R.id.pet_type);
        petPrice = findViewById(R.id.price);
        petInfo = findViewById(R.id.pet_info);
        petGenderSpinner = findViewById(R.id.pet_gender_spinner);
        addNewPetButton = findViewById(R.id.btn_app_new_pet);
        birthDateButton = findViewById(R.id.birth_date_button);

        // Настройка ProgressDialog
        loadingBar = new ProgressDialog(this);
        loadingBar.setCancelable(false);
    }

    private void setupFirebase() {
        try {
            ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        } catch (Exception e) {
            showToast("Ошибка инициализации базы данных");
            finish();
        }
    }

    private void setupListeners() {
        petImage.setOnClickListener(v -> openGallery());
        addNewPetButton.setOnClickListener(v -> validatePetData());
        birthDateButton.setOnClickListener(v -> showDatePickerDialog());
        updateBirthDateButtonText();
    }

    private void validatePetData() {
        categoryName = getIntent().getStringExtra("category");
        Type = petType.getText().toString().trim();
        Price = petPrice.getText().toString().trim();
        Info = petInfo.getText().toString().trim();
        PName = petName.getText().toString().trim();

        if (ImageUri == null) {
            showToast("Добавьте изображение питомца");
            return;
        }

        if (TextUtils.isEmpty(PName)) {
            showToast("Введите кличку питомца");
            return;
        }

        if (TextUtils.isEmpty(Type)) {
            showToast("Укажите породу питомца");
            return;
        }

        if (TextUtils.isEmpty(Price)) {
            showToast("Введите цену питомца");
            return;
        }

        if (TextUtils.isEmpty(Info)) {
            showToast("Добавьте описание питомца");
            return;
        }

        storePetInformation();
    }

    private void storePetInformation() {
        showLoadingDialog("Сохранение данных", "Пожалуйста, подождите...");

        // Генерация ID
        productRandomKey = generateProductId();

        // Кодирование изображения
        String encodedImage = encodeImage(ImageUri);
        if (encodedImage == null) {
            loadingBar.dismiss();
            showToast("Ошибка обработки изображения");
            return;
        }

        // Сохранение в базу данных
        saveProductToDatabase(encodedImage);
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

    private String generateProductId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss", Locale.getDefault());
        saveCurrentDate = dateFormat.format(Calendar.getInstance().getTime());
        saveCurrentTime = timeFormat.format(Calendar.getInstance().getTime());
        return saveCurrentDate + saveCurrentTime;
    }

    private void saveProductToDatabase(String encodedImage) {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("type", Type);
        productMap.put("image", encodedImage);
        productMap.put("category", categoryName);
        productMap.put("price", Price);
        productMap.put("pname", PName);
        productMap.put("info", Info);
        productMap.put("birthDate", getFormattedBirthDate());

        ProductsRef.child(productRandomKey).setValue(productMap)
                .addOnSuccessListener(aVoid -> {
                    loadingBar.dismiss();
                    showToast("Объявление добавлено");
                    Intent intent = new Intent(AddNewPetActivity.this, MarketActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    loadingBar.dismiss();
                    showToast("Ошибка: " + e.getMessage());
                });
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
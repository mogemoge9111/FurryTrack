package com.example.furrytrack.Market;

import android.app.ComponentCaller;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.furrytrack.R;
import com.rey.material.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNewPetActivity extends AppCompatActivity {

    private String categoryName, Type, Price, Info, PName;

    private ImageView petImage;

    private EditText petName, petType, petPrice, petInfo;

    private Spinner petGenderSpinner;

    private Calendar birthDate = Calendar.getInstance();

    private Button addNewPetButton;

    private static final int GALLERYPICK = 1;

    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_pet);

        init();

        petGenderSpinner = findViewById(R.id.pet_gender_spinner);
        Button birthDateButton = findViewById(R.id.birth_date_button);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this, R.array.pet_genders, android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petGenderSpinner.setAdapter(genderAdapter);

        setupDatePicker(birthDateButton, birthDate, "Дата рождения");

        petImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        addNewPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePetData();
            }
        });


    }

    private void ValidatePetData() {
        Type = petType.getText().toString();
        Price = petPrice.getText().toString();
        Info = petInfo.getText().toString();
        PName = petName.getText().toString();

        if(ImageUri == null){
            Toast.makeText(this, "Добавьте изображение питомца", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Type)){
            Toast.makeText(this, "Добавьте породу питомца", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Price)){
            Toast.makeText(this, "Добавьте цена питомца", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Info)){
            Toast.makeText(this, "Добавьте описание питомца", Toast.LENGTH_SHORT).show();
        }

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERYPICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, @NonNull ComponentCaller caller) {
        super.onActivityResult(requestCode, resultCode, data, caller);

        if(requestCode == GALLERYPICK && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();

            petImage.setImageURI(ImageUri);
        }
    }

    private void init() {
        categoryName = getIntent().getExtras().get("category").toString();

        Toast.makeText(this, "Выбрана категория " + categoryName, Toast.LENGTH_SHORT).show();

        petImage = findViewById(R.id.select_pet_image);
        petName = findViewById(R.id.pet_name);
        petType = findViewById(R.id.pet_type);
        petType = findViewById(R.id.pet_type);
        petPrice = findViewById(R.id.price);
        petInfo = findViewById(R.id.pet_info);
        addNewPetButton = findViewById(R.id.btn_app_new_pet);
    }

    private void setupDatePicker(Button button, Calendar calendar, String title) {
        button.setOnClickListener(v -> showDatePicker(calendar, button, title));
        updateButtonText(button, calendar);
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
}
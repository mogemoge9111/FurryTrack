package com.example.furrytrack.BasicPetsWind;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.furrytrack.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddPetActivity extends AppCompatActivity {
    private EditText petNameEditText, importantInfoEditText;
    private Spinner petTypeSpinner, petGenderSpinner;
    private Calendar birthDate = Calendar.getInstance();
    private Calendar adoptionDate = Calendar.getInstance();
    private Calendar lastVetVisit = Calendar.getInstance();
    private Calendar lastWalk = Calendar.getInstance();
    private Calendar lastWash = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        petNameEditText = findViewById(R.id.pet_name_edit_text);
        importantInfoEditText = findViewById(R.id.important_info_edit_text);
        petTypeSpinner = findViewById(R.id.pet_type_spinner);
        petGenderSpinner = findViewById(R.id.pet_gender_spinner);

        Button birthDateButton = findViewById(R.id.birth_date_button);
        Button adoptionDateButton = findViewById(R.id.adoption_date_button);
        Button saveButton = findViewById(R.id.save_pet_button);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.pet_types, android.R.layout.simple_spinner_item
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petTypeSpinner.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this, R.array.pet_genders, android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petGenderSpinner.setAdapter(genderAdapter);

        setupDatePicker(birthDateButton, birthDate, "Дата рождения");
        setupDatePicker(adoptionDateButton, adoptionDate, "Дата появления в семье");

        saveButton.setOnClickListener(v -> savePet());
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

        private void savePet() {
            try {
                String name = petNameEditText.getText().toString().trim();
                String type = petTypeSpinner.getSelectedItem().toString();
                String gender = petGenderSpinner.getSelectedItem().toString();
                String importantInfo = importantInfoEditText.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(this, "Введите имя питомца", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (birthDate == null || adoptionDate == null) {
                    Toast.makeText(this, "Выберите все даты", Toast.LENGTH_SHORT).show();
                    return;
                }

                Pet newPet = new Pet(
                        name,
                        type,
                        gender,
                        birthDate.getTime(),
                        adoptionDate.getTime(),
                        importantInfo,
                        lastVetVisit.getTime(),
                        lastWalk.getTime(),
                        lastWash.getTime()
                );

                Intent resultIntent = new Intent();
                resultIntent.putExtra("new_pet", newPet);
                setResult(RESULT_OK, resultIntent);
                finish();

            } catch (Exception e) {
                Log.e("AddPetActivity", "Ошибка сохранения", e);
                Toast.makeText(this, "Ошибка при сохранении", Toast.LENGTH_SHORT).show();
            }
        }
    }

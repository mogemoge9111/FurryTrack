package com.example.furrytrack.BasicPetsWind;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.furrytrack.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PetDetailsActivity extends AppCompatActivity {
    private Pet pet;
    private Calendar lastVetVisit = Calendar.getInstance();
    private Calendar lastWalk = Calendar.getInstance();
    private Calendar lastWash = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_details);

        pet = (Pet) getIntent().getSerializableExtra("pet");

        TextView petNameTextView = findViewById(R.id.pet_name_text_view);
        TextView petTypeTextView = findViewById(R.id.pet_type_text_view);
        TextView petGenderTextView = findViewById(R.id.pet_gender_text_view);
        TextView birthDateTextView = findViewById(R.id.birth_date_text_view);
        TextView adoptionDateTextView = findViewById(R.id.adoption_date_text_view);
        TextView importantInfoTextView = findViewById(R.id.important_info_text_view);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        petNameTextView.setText(pet.getName());
        petTypeTextView.setText(pet.getType());
        petGenderTextView.setText(pet.getGender());
        birthDateTextView.setText(sdf.format(pet.getBirthDate()));
        adoptionDateTextView.setText(sdf.format(pet.getAdoptionDate()));
        importantInfoTextView.setText(pet.getImportantInfo());

        Button lastVetVisitButton = findViewById(R.id.last_vet_visit_button);
        Button lastWalkButton = findViewById(R.id.last_walk_button);
        Button lastWashButton = findViewById(R.id.last_wash_button);
        Button addRecordButton = findViewById(R.id.add_record_button);

        lastVetVisitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(lastVetVisit, lastVetVisitButton);
            }
        });

        lastWalkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(lastWalk, lastWalkButton);
            }
        });

        lastWashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(lastWash, lastWashButton);
            }
        });

        addRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecord();
            }
        });
    }

    private void showDatePickerDialog(final Calendar calendar, final Button button) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateButtonText(calendar, button);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateButtonText(Calendar calendar, Button button) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        button.setText(sdf.format(calendar.getTime()));
    }

    private void saveRecord() {
        Toast.makeText(this, "Запись сохранена", Toast.LENGTH_SHORT).show();
    }
}
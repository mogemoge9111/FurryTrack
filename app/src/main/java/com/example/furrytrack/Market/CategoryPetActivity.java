package com.example.furrytrack.Market;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.furrytrack.R;

public class CategoryPetActivity extends AppCompatActivity {

    private ImageView cat, dog, rabbit;
    private ImageView owl, pig, cow;
    private ImageView rooster, chicken, makaka;
    private ImageView pingvin, elephant, fox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_pet);

        init();

        cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryPetActivity.this, AddNewPetActivity.class);
                intent.putExtra("category", "cat");
                startActivity(intent);
            }
        });

        dog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryPetActivity.this, AddNewPetActivity.class);
                intent.putExtra("category", "dog");
                startActivity(intent);
            }
        });

        rabbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryPetActivity.this, AddNewPetActivity.class);
                intent.putExtra("category", "rabbit");
                startActivity(intent);
            }
        });

        owl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryPetActivity.this, AddNewPetActivity.class);
                intent.putExtra("category", "owl");
                startActivity(intent);
            }
        });

        pig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryPetActivity.this, AddNewPetActivity.class);
                intent.putExtra("category", "pig");
                startActivity(intent);
            }
        });

        cow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryPetActivity.this, AddNewPetActivity.class);
                intent.putExtra("category", "cow");
                startActivity(intent);
            }
        });

        rooster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryPetActivity.this, AddNewPetActivity.class);
                intent.putExtra("category", "rooster");
                startActivity(intent);
            }
        });

        chicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryPetActivity.this, AddNewPetActivity.class);
                intent.putExtra("category", "chicken");
                startActivity(intent);
            }
        });

        makaka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryPetActivity.this, AddNewPetActivity.class);
                intent.putExtra("category", "makaka");
                startActivity(intent);
            }
        });

        pingvin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryPetActivity.this, AddNewPetActivity.class);
                intent.putExtra("category", "pingvin");
                startActivity(intent);
            }
        });

        elephant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryPetActivity.this, AddNewPetActivity.class);
                intent.putExtra("category", "elephant");
                startActivity(intent);
            }
        });

        fox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryPetActivity.this, AddNewPetActivity.class);
                intent.putExtra("category", "fox");
                startActivity(intent);
            }
        });
    }

    private void init() {
        cat = findViewById(R.id.cat);
        dog = findViewById(R.id.dog);
        rabbit = findViewById(R.id.rabbit);

        owl = findViewById(R.id.owl);
        pig = findViewById(R.id.pig);
        cow = findViewById(R.id.cow);

        rooster = findViewById(R.id.rooster);
        chicken = findViewById(R.id.chicken);
        makaka = findViewById(R.id.makaka);

        pingvin = findViewById(R.id.pingvin);
        elephant = findViewById(R.id.elephant);
        fox = findViewById(R.id.fox);
    }
}
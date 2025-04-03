package com.example.furrytrack.Welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.furrytrack.Model.Users;
import com.example.furrytrack.BasicPetsWind.PetActivity;
import com.example.furrytrack.Prevalent.Prevalent;
import com.example.furrytrack.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinButton;
    private Button loginButton;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinButton = (Button) findViewById(R.id.main_join_button);
        loginButton = (Button) findViewById(R.id.add_account_button);

        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        String UserEmailKey = Paper.book().read(Prevalent.UserEmailKey);

        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if(UserEmailKey != "" && UserPasswordKey != ""){
            if(!TextUtils.isEmpty(UserEmailKey) && !TextUtils.isEmpty(UserPasswordKey)){
                ValidateUser(UserEmailKey, UserPasswordKey);


                loadingBar.setTitle("Вход в приложение");
                loadingBar.setMessage("Пожалуйста, подождите...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }

    }

    private void ValidateUser(String email, String password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loadingBar.dismiss();
            Toast.makeText(this, "Некорректный формат email", Toast.LENGTH_SHORT).show();
            return;
        }

        String safeEmailKey = email.replace(".", ",");

        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.child("Users").child(safeEmailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingBar.dismiss();

                if (snapshot.exists()) {
                    Users usersData = snapshot.getValue(Users.class);

                    if (usersData.getPassword().equals(password)) {
                        Toast.makeText(MainActivity.this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.apply();

                        Intent intent = new Intent(MainActivity.this, PetActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {

                    }
                } else {

                    Toast.makeText(MainActivity.this,
                            "Аккаунт с email " + email + " не существует",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingBar.dismiss();
                Toast.makeText(MainActivity.this,
                        "Ошибка: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
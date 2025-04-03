package com.example.furrytrack.Welcome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.furrytrack.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText usernameInput, emailInput, passwordInput;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = (Button) findViewById(R.id.register_button);

        usernameInput = (EditText) findViewById(R.id.register_username_input);

        emailInput = (EditText) findViewById(R.id.register_email_input);

        passwordInput = (EditText) findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });

    }

    private void CreateAccount() {
        String username = usernameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Введите имя",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Введите электронную почту",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Введите пароль",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Создание аккаунта");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateEmail(username, email, password);
        }
    }

    private void ValidateEmail(String username, String email, String password) {
        // Создаем безопасный ключ
        String userKey = email.replace(".", ",");

        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();

        // Проверяем валидность email перед записью
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Некорректный email", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
            return;
        }

        RootRef.child("Users").orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            HashMap<String, Object> userDataMap = new HashMap<>();
                            userDataMap.put("email", email);
                            userDataMap.put("name", username);
                            userDataMap.put("password", password);

                            RootRef.child("Users").child(userKey).updateChildren(userDataMap)
                                    .addOnCompleteListener(task -> {
                                        loadingBar.dismiss();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Ошибка: " + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(RegisterActivity.this, "Email уже зарегистрирован", Toast.LENGTH_SHORT).show();

                            Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(registerIntent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Ошибка: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
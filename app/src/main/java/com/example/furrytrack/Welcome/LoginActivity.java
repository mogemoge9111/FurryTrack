package com.example.furrytrack.Welcome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.furrytrack.Market.AdminDeletePostsActivity;
import com.example.furrytrack.Model.Users;
import com.example.furrytrack.BasicPetsWind.PetActivity;
import com.example.furrytrack.Prevalent.Prevalent;
import com.example.furrytrack.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText emailInput, passwordInput;

    private ProgressDialog loadingBar;

    private TextView AdminLink, NotAdminLink;

    private String parentDbName = "Users";

    private CheckBox checkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login_button);

        emailInput = (EditText) findViewById(R.id.login_email_input);

        passwordInput = (EditText) findViewById(R.id.login_password_input);
        loadingBar = new ProgressDialog(this);

        checkBoxRememberMe = (CheckBox)findViewById(R.id.login_checkbox);
        Paper.init(this);

        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                loginButton.setText("Вход для админа");
                parentDbName = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                loginButton.setText("Войти");
                parentDbName = "Users";
            }
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Введите электронную почту",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Введите пароль",Toast.LENGTH_SHORT).show();
        }
        else {

            loadingBar.setTitle("Вход в приложение");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUser(email, password);
        }
    }

    private void ValidateUser(String email, String password) {

        if(checkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserEmailKey, email);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loadingBar.dismiss();
            Toast.makeText(this, "Некорректный формат email", Toast.LENGTH_SHORT).show();
            return;
        }

        String safeEmailKey = email.replace(".", ",");

        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.child(parentDbName).child(safeEmailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingBar.dismiss();

                if (snapshot.exists()) {
                    Users usersData = snapshot.getValue(Users.class);

                    if (usersData.getPassword().equals(password)) {
                        Toast.makeText(LoginActivity.this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.apply();

                        if(parentDbName.equals("Users")){
                            Intent intent = new Intent(LoginActivity.this, PetActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        else if(parentDbName.equals("Admins")){
                            Toast.makeText(LoginActivity.this, "Успешный вход админа!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, AdminDeletePostsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    } else {

                        Toast.makeText(LoginActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                    }
                } else {


                    Toast.makeText(LoginActivity.this,
                            "Аккаунт с email " + email + " не существует",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this,
                        "Ошибка: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.example.healthcareproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcareproject.R;
import com.example.healthcareproject.utils.FirebaseClient;
import com.example.healthcareproject.utils.SessionManager;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegister, tvForgotPassword;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            redirectToAppropriatePage(sessionManager.getUserType());
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvSignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });


    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        DatabaseReference ref = FirebaseClient.getInstance()
                .getRealtimeDB()
                .child("users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean userFound = false;

                for (DataSnapshot child : snapshot.getChildren()) {
                    String dbEmail = child.child("email").getValue(String.class);
                    String dbPass = child.child("password").getValue(String.class);

                    if (email.equals(dbEmail) && pass.equals(dbPass)) {
                        userFound = true;

                        String userId = child.child("uid").getValue(String.class);
                        String userName = child.child("name").getValue(String.class);
                        String userType = child.child("userType").getValue(String.class);
                        String userPhone = child.child("phone").getValue(String.class);

                        if (userId == null || userName == null || userType == null) {
                            Toast.makeText(LoginActivity.this, "Error: User data incomplete", Toast.LENGTH_SHORT).show();
                            resetLoginButton();
                            return;
                        }

                        if (userPhone == null) {
                            userPhone = "";
                        }

                        sessionManager.saveUserSession(userId, userName, email, userPhone, userType);

                        redirectToAppropriatePage(userType);
                        break;
                    }
                }

                if (!userFound) {
                    Toast.makeText(LoginActivity.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
                    resetLoginButton();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                resetLoginButton();
            }
        });
    }

    private void redirectToAppropriatePage(String userType) {
        Intent intent;

        if ("DOCTOR".equals(userType)) {
            intent = new Intent(LoginActivity.this, DoctorMainPageActivity.class);
        } else if ("ADMIN".equals(userType)) {
            intent = new Intent(LoginActivity.this, AdminMainPageActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, MainPageActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void resetLoginButton() {
        btnLogin.setEnabled(true);
        btnLogin.setText("Login");
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetLoginButton();
    }
}
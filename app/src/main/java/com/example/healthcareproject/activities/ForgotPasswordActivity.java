package com.example.healthcareproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcareproject.R;
import com.example.healthcareproject.utils.FirebaseClient;
import com.google.firebase.database.*;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnReset;
    ImageView ivBack;
    private String userUid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnReset = findViewById(R.id.btnReset);
        ivBack = findViewById(R.id.ivBack);


        ivBack.setOnClickListener(v -> {
            finish();
        });

        btnReset.setOnClickListener(v -> verifyEmail());
    }

    private void verifyEmail() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }


        btnReset.setEnabled(false);
        btnReset.setText("Verifying...");

        DatabaseReference ref = FirebaseClient.getInstance()
                .getRealtimeDB()
                .child("users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean emailFound = false;

                for (DataSnapshot child : snapshot.getChildren()) {
                    String dbEmail = child.child("email").getValue(String.class);

                    if (email.equals(dbEmail)) {
                        emailFound = true;
                        userUid = child.child("uid").getValue(String.class);

                        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("USER_UID", userUid);
                        intent.putExtra("USER_EMAIL", email);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }

                if (!emailFound) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Email not found. Please check and try again.",
                            Toast.LENGTH_SHORT).show();
                    resetButton();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ForgotPasswordActivity.this,
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                resetButton();
            }
        });
    }

    private void resetButton() {
        btnReset.setEnabled(true);
        btnReset.setText("Reset Password");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
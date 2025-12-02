package com.example.healthcareproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcareproject.R;
import com.example.healthcareproject.utils.FirebaseClient;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        try {
            FirebaseClient.init(getApplicationContext());
        } catch (Exception e) {
            Toast.makeText(this, "Firebase initialization error", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        new Handler().postDelayed(() -> {
            if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            } else {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            }
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}

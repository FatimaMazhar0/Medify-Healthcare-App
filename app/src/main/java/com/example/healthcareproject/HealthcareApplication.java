package com.example.healthcareproject;

import android.app.Application;
import com.example.healthcareproject.utils.FirebaseClient;

public class HealthcareApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize FirebaseClient
        FirebaseClient.init(this);
    }
}
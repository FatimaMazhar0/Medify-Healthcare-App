package com.example.healthcareproject.utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseClient {

    private static FirebaseClient instance;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseDatabase rtdb; // Realtime Database instance

    private static final String TAG = "FirebaseClient";

    private FirebaseClient(Context context) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context);
        }
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        rtdb = FirebaseDatabase.getInstance(); // initialize Realtime DB
        Log.d(TAG, "Firebase initialized successfully");
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new FirebaseClient(context.getApplicationContext());
        }
    }

    public static FirebaseClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("FirebaseClient not initialized. Call init(context) first.");
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    // Add this method to get Realtime Database reference
    public DatabaseReference getRealtimeDB() {
        return rtdb.getReference();
    }
}

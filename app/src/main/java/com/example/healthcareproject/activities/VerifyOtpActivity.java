//package com.example.healthcareproject.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.healthcareproject.R;
//import com.example.healthcareproject.models.User;
//import com.example.healthcareproject.utils.FirebaseClient;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.PhoneAuthCredential;
//import com.google.firebase.auth.PhoneAuthProvider;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.ValueEventListener;
//
//public class VerifyOtpActivity extends AppCompatActivity {
//
//    private FirebaseAuth mAuth;
//    private DatabaseReference dbRef;
//
//    private EditText etOtp;
//    private Button btnVerifyOtp;
//    private TextView tvSubtitle;
//
//    private String receivedVerificationId;
//    private String phone, name, password, flow;
//
//    private final User.UserType USER_TYPE = User.UserType.PATIENT;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_verify_otp);
//
//        mAuth = FirebaseClient.getInstance().getAuth();
//        dbRef = FirebaseClient.getInstance().getDbRef();
//
//        etOtp = findViewById(R.id.etOtp);
//        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
//        tvSubtitle = findViewById(R.id.tvSubtitle);
//
//        loadIntentData();
//
//        btnVerifyOtp.setOnClickListener(v -> verifyCode());
//    }
//
//    private void loadIntentData() {
//        Intent intent = getIntent();
//        receivedVerificationId = intent.getStringExtra(RegisterActivity.EXTRA_VERIFICATION_ID);
//        phone = intent.getStringExtra(RegisterActivity.EXTRA_PHONE_NUMBER);
//        name = intent.getStringExtra(RegisterActivity.EXTRA_NAME);
//        password = intent.getStringExtra(RegisterActivity.EXTRA_PASSWORD);
//        flow = intent.getStringExtra("flow"); // "REGISTER" or "FORGOT_PASSWORD"
//
//        if (phone != null) {
//            tvSubtitle.setText("Enter the 6-digit OTP sent to " + phone);
//        } else {
//            Toast.makeText(this, "Error: Phone number missing.", Toast.LENGTH_LONG).show();
//            finish();
//        }
//    }
//
//    private void verifyCode() {
//        String code = etOtp.getText().toString().trim();
//
//        if (code.isEmpty() || code.length() < 6) {
//            Toast.makeText(this, "Please enter the 6-digit code.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(receivedVerificationId, code);
//
//        // Use the credential to sign into a temporary Firebase Auth account.
//        // This is necessary to confirm the OTP without a custom server.
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(VerifyOtpActivity.this, "Verification Success!", Toast.LENGTH_SHORT).show();
//
//                        // Determine next step based on the flow
//                        if ("REGISTER".equals(flow)) {
//                            saveUserDataToRealtimeDB(task.getResult().getUser().getUid());
//                        } else if ("FORGOT_PASSWORD".equals(flow)) {
//                            navigateToResetPassword();
//                        }
//
//                    } else {
//                        Toast.makeText(VerifyOtpActivity.this, "Verification Failed: Invalid code.", Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
//
//    private void saveUserDataToRealtimeDB(String uid) {
//        // First, check if the phone number already exists in the database
//        Query phoneQuery = dbRef.child(FirebaseClient.USERS_NODE)
//                .orderByChild("phone")
//                .equalTo(phone)
//                .limitToFirst(1);
//
//        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    // Phone number is already registered in RTDB
//                    Toast.makeText(VerifyOtpActivity.this, "This phone number is already registered.", Toast.LENGTH_LONG).show();
//                    mAuth.getCurrentUser().delete(); // Clean up the temporary Firebase Auth account
//                    startActivity(new Intent(VerifyOtpActivity.this, LoginActivity.class));
//                    finishAffinity();
//                } else {
//                    // Phone number is unique, proceed with save
//                    User newUser = new User(uid, password, name, USER_TYPE);
//                    newUser.setPhone(phone);
//
//                    dbRef.child(FirebaseClient.USERS_NODE)
//                            .child(uid)
//                            .setValue(newUser)
//                            .addOnSuccessListener(aVoid -> {
//                                Toast.makeText(VerifyOtpActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(VerifyOtpActivity.this, MainPageActivity.class));
//                                finishAffinity();
//                            })
//                            .addOnFailureListener(e -> {
//                                Toast.makeText(VerifyOtpActivity.this, "Data save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                                mAuth.getCurrentUser().delete(); // Clean up Auth if DB save fails
//                            });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Toast.makeText(VerifyOtpActivity.this, "Database check failed.", Toast.LENGTH_LONG).show();
//                mAuth.getCurrentUser().delete();
//            }
//        });
//    }
//
//    private void navigateToResetPassword() {
//        Intent intent = new Intent(VerifyOtpActivity.this, ResetPasswordActivity.class);
//        intent.putExtra(ResetPasswordActivity.EXTRA_PHONE_NUMBER, phone);
//        startActivity(intent);
//        finishAffinity();
//    }
//}
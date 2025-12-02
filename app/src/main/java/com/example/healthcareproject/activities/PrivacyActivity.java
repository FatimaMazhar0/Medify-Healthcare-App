package com.example.healthcareproject.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.healthcareproject.R;
import com.example.healthcareproject.utils.PreferenceManager;

public class PrivacyActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private ImageView ivBack;
    private SwitchCompat switchProfileVisibility;
    private SwitchCompat switchLocationSharing;
    private SwitchCompat switchActivityStatus;
    private SwitchCompat switchReadReceipts;
    private SwitchCompat switchDataCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        preferenceManager = new PreferenceManager(this);

        initializeViews();
        loadPrivacySettings();
        setupClickListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.ivBack);
        switchProfileVisibility = findViewById(R.id.switchProfileVisibility);
        switchLocationSharing = findViewById(R.id.switchLocationSharing);
        switchActivityStatus = findViewById(R.id.switchActivityStatus);
        switchReadReceipts = findViewById(R.id.switchReadReceipts);
        switchDataCollection = findViewById(R.id.switchDataCollection);
    }

    private void loadPrivacySettings() {
        switchProfileVisibility.setChecked(preferenceManager.getBoolean("privacy_profile", true));
        switchLocationSharing.setChecked(preferenceManager.getBoolean("privacy_location", false));
        switchActivityStatus.setChecked(preferenceManager.getBoolean("privacy_activity", true));
        switchReadReceipts.setChecked(preferenceManager.getBoolean("privacy_read_receipts", true));
        switchDataCollection.setChecked(preferenceManager.getBoolean("privacy_data", false));
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        switchProfileVisibility.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferenceManager.putBoolean("privacy_profile", isChecked)
        );

        switchLocationSharing.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferenceManager.putBoolean("privacy_location", isChecked)
        );

        switchActivityStatus.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferenceManager.putBoolean("privacy_activity", isChecked)
        );

        switchReadReceipts.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferenceManager.putBoolean("privacy_read_receipts", isChecked)
        );

        switchDataCollection.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferenceManager.putBoolean("privacy_data", isChecked)
        );
    }
}
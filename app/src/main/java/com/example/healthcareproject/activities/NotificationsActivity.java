package com.example.healthcareproject.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.healthcareproject.R;
import com.example.healthcareproject.utils.PreferenceManager;

public class NotificationsActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private ImageView ivBack;
    private SwitchCompat switchPushNotifications;
    private SwitchCompat switchEmailNotifications;
    private SwitchCompat switchSmsNotifications;
    private SwitchCompat switchAppUpdates;
    private SwitchCompat switchPromotions;
    private SwitchCompat switchNewMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        preferenceManager = new PreferenceManager(this);

        initializeViews();
        loadNotificationSettings();
        setupClickListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.ivBack);
        switchPushNotifications = findViewById(R.id.switchPushNotifications);
        switchEmailNotifications = findViewById(R.id.switchEmailNotifications);
        switchSmsNotifications = findViewById(R.id.switchSmsNotifications);
        switchAppUpdates = findViewById(R.id.switchAppUpdates);
        switchPromotions = findViewById(R.id.switchPromotions);
        switchNewMessages = findViewById(R.id.switchNewMessages);
    }

    private void loadNotificationSettings() {
        switchPushNotifications.setChecked(preferenceManager.getBoolean("notif_push", true));
        switchEmailNotifications.setChecked(preferenceManager.getBoolean("notif_email", true));
        switchSmsNotifications.setChecked(preferenceManager.getBoolean("notif_sms", false));
        switchAppUpdates.setChecked(preferenceManager.getBoolean("notif_updates", true));
        switchPromotions.setChecked(preferenceManager.getBoolean("notif_promotions", false));
        switchNewMessages.setChecked(preferenceManager.getBoolean("notif_messages", true));
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        switchPushNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferenceManager.putBoolean("notif_push", isChecked)
        );

        switchEmailNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferenceManager.putBoolean("notif_email", isChecked)
        );

        switchSmsNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferenceManager.putBoolean("notif_sms", isChecked)
        );

        switchAppUpdates.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferenceManager.putBoolean("notif_updates", isChecked)
        );

        switchPromotions.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferenceManager.putBoolean("notif_promotions", isChecked)
        );

        switchNewMessages.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferenceManager.putBoolean("notif_messages", isChecked)
        );
    }
}
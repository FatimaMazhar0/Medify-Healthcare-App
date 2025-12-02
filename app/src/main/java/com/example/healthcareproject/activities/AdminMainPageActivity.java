package com.example.healthcareproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.AdminPagerAdapter;
import com.example.healthcareproject.utils.PreferenceManager;
import com.example.healthcareproject.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminMainPageActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AdminPagerAdapter pagerAdapter;
    private SessionManager sessionManager;
    private PreferenceManager preferenceManager;
    private ImageView btnLogout;
    private TextView tvAdminTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main_page);

        preferenceManager = new PreferenceManager(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupViewPager();
        setupClickListeners();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnLogout = findViewById(R.id.btnLogout);
        tvAdminTitle = findViewById(R.id.tvAdminTitle);
    }

    private void setupViewPager() {
        pagerAdapter = new AdminPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Approvals");
                    tab.setIcon(R.drawable.ic_approval);
                    break;
                case 1:
                    tab.setText("View Data");
                    tab.setIcon(R.drawable.ic_view_data);
                    break;
            }
        }).attach();
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        preferenceManager.clearAll();
        sessionManager.logout();

        Intent intent = new Intent(AdminMainPageActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        logout();
    }
}
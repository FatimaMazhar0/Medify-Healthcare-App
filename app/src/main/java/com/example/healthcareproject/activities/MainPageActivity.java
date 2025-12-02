package com.example.healthcareproject.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.healthcareproject.R;
import com.example.healthcareproject.fragments.HomeFragment;
import com.example.healthcareproject.fragments.MessageFragment;
import com.example.healthcareproject.fragments.ScheduleFragment;
import com.example.healthcareproject.fragments.SettingsFragment;

public class MainPageActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout navHome, navSchedule, navMessage, navSetting;
    private ImageView iconHome, iconSchedule, iconMessage, iconSetting;
    private TextView labelHome, labelSchedule, labelMessage, labelSetting;

    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        initViews();
        setupViewPager();
        setupBottomNavigation();

        updateBottomNavSelection(0);
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);

        navHome = findViewById(R.id.navHome);
        navSchedule = findViewById(R.id.navSchedule);
        navMessage = findViewById(R.id.navMessage);
        navSetting = findViewById(R.id.navSetting);

        iconHome = findViewById(R.id.iconHome);
        iconSchedule = findViewById(R.id.iconSchedule);
        iconMessage = findViewById(R.id.iconMessage);
        iconSetting = findViewById(R.id.iconSetting);

        labelHome = findViewById(R.id.labelHome);
        labelSchedule = findViewById(R.id.labelSchedule);
        labelMessage = findViewById(R.id.labelMessage);
        labelSetting = findViewById(R.id.labelSetting);
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(true);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPage = position;
                updateBottomNavSelection(position);
            }
        });
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> viewPager.setCurrentItem(0, true));
        navSchedule.setOnClickListener(v -> viewPager.setCurrentItem(1, true));
        navMessage.setOnClickListener(v -> viewPager.setCurrentItem(2, true));
        navSetting.setOnClickListener(v -> viewPager.setCurrentItem(3, true));
    }

    private void updateBottomNavSelection(int position) {

        int unselectedColor = ContextCompat.getColor(this, R.color.nav_unselected);
        int selectedColor = ContextCompat.getColor(this, R.color.nav_selected);

        labelHome.setTextColor(unselectedColor);
        labelSchedule.setTextColor(unselectedColor);
        labelMessage.setTextColor(unselectedColor);
        labelSetting.setTextColor(unselectedColor);

        switch (position) {
            case 0:
                labelHome.setTextColor(selectedColor);
                break;
            case 1:
                labelSchedule.setTextColor(selectedColor);
                break;
            case 2:
                labelMessage.setTextColor(selectedColor);
                break;
            case 3:
                labelSetting.setTextColor(selectedColor);
                break;
        }
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new ScheduleFragment();
                case 2:
                    return new MessageFragment();
                case 3:
                    return new SettingsFragment();
                default:
                    return new HomeFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    @Override
    public void onBackPressed() {
        if (currentPage != 0) {
            viewPager.setCurrentItem(0, true);
        } else {
            super.onBackPressed();
        }
    }


}
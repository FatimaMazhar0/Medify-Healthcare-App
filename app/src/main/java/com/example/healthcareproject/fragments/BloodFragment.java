package com.example.healthcareproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.BloodViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BloodFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BloodViewPagerAdapter viewPagerAdapter;
    private ImageView ivBackArrow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood, container, false);

        initViews(view);
        setupClickListeners();
        setupViewPager();
        setupTabLayout();

        return view;
    }

    private void initViews(View view) {
        ivBackArrow = view.findViewById(R.id.ivBackArrow);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
    }

    private void setupClickListeners() {
        ivBackArrow.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.HomeLayout, new HomeFragment())
                        .commit();
            }
        });
    }

    private void setupViewPager() {
        viewPagerAdapter = new BloodViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Blood Donors");
                    break;
                case 1:
                    tab.setText("My Requests");
                    break;
            }
        }).attach();
    }
}
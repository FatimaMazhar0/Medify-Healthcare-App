package com.example.healthcareproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.ViewDataPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ViewDataFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewDataPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_data, container, false);

        initViews(view);
        setupViewPager();

        return view;
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayoutViewData);
        viewPager = view.findViewById(R.id.viewPagerData);
    }

    private void setupViewPager() {
        pagerAdapter = new ViewDataPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Patients");
                    break;
                case 1:
                    tab.setText("Doctors");
                    break;
                case 2:
                    tab.setText("Hospitals");
                    break;
            }
        }).attach();
    }
}
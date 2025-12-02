package com.example.healthcareproject.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.healthcareproject.fragments.BloodDonorsFragment;
import com.example.healthcareproject.fragments.BloodRequestsFragment;

public class BloodViewPagerAdapter extends FragmentStateAdapter {

    public BloodViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public BloodViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new BloodDonorsFragment();
            case 1:
                return new BloodRequestsFragment();
            default:
                return new BloodDonorsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
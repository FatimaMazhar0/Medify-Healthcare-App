package com.example.healthcareproject.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.healthcareproject.fragments.DoctorsListFragment;
import com.example.healthcareproject.fragments.HospitalsListFragment;
import com.example.healthcareproject.fragments.PatientsListFragment;

public class ViewDataPagerAdapter extends FragmentStateAdapter {

    public ViewDataPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PatientsListFragment();
            case 1:
                return new DoctorsListFragment();
            case 2:
                return new HospitalsListFragment();
            default:
                return new PatientsListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
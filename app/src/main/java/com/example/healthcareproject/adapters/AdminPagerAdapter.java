package com.example.healthcareproject.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.healthcareproject.fragments.ApprovalFragment;
import com.example.healthcareproject.fragments.ViewDataFragment;

public class AdminPagerAdapter extends FragmentStateAdapter {

    public AdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ApprovalFragment();
            case 1:
                return new ViewDataFragment();
            default:
                return new ApprovalFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
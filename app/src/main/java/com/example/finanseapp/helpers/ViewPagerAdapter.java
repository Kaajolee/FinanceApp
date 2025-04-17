package com.example.finanseapp.Helpers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.finanseapp.Tabs.Graphs.TabGraphMonth;
import com.example.finanseapp.Tabs.Graphs.TabGraphToday;
import com.example.finanseapp.Tabs.Graphs.TabGraphWeek;

public class ViewPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

public Fragment fragment;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                fragment = new TabGraphWeek();
                return fragment;
            case 2:
                fragment = new TabGraphMonth();
                return fragment;
            default:
                fragment = new TabGraphToday();
                return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

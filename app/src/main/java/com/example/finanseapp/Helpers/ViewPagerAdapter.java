package com.example.finanseapp.Helpers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.finanseapp.Tabs.Graphs.TabGraphMonth;
import com.example.finanseapp.Tabs.Graphs.TabGraphToday;
import com.example.finanseapp.Tabs.Graphs.TabGraphWeek;

public class ViewPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

    public Fragment todayFragment = new TabGraphToday();
    public Fragment weekFragment = new TabGraphWeek();
    public Fragment monthFragment = new TabGraphMonth();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return weekFragment;
            case 2: return monthFragment;
            default: return todayFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

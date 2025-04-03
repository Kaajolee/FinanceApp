package com.example.finanseapp.Helpers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.finanseapp.Tabs.Graphs.TabGraphMonth;
import com.example.finanseapp.Tabs.Graphs.TabGraphToday;
import com.example.finanseapp.Tabs.Graphs.TabGraphWeek;

public class ViewPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {


    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TabGraphToday();
            case 1:
                return new TabGraphWeek();
            case 2:
                return new TabGraphMonth();
            default:
                return new TabGraphToday();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

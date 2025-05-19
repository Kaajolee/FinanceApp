package com.example.finanseapp;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finanseapp.Enums.SourceType;
import com.example.finanseapp.Helpers.ViewPagerAdapter;
import com.example.finanseapp.Tabs.Graphs.TabGraphMonth;
import com.example.finanseapp.Tabs.Graphs.TabGraphToday;
import com.example.finanseapp.Tabs.Graphs.TabGraphWeek;
import com.google.android.material.tabs.TabLayout;


public class GraphsActivity extends AppCompatActivity {

    ActionBar actionBar;
    TabLayout tabLayout;
    ViewPagerAdapter viewPagerAdapter;
    ViewPager2 viewPager2;
    Button incomeButton, expensesButton;
    LinearLayout todayLayout, weekLayout, monthLayout;
    ImageView todayTrendImage, weekTrendImage, monthTrendImage;
    TextView todayAmount, weekAmount, monthAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_graphs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        incomeButton = findViewById(R.id.incomeButton);
        expensesButton = findViewById(R.id.expensesButton);

        SetGraphSwitch(incomeButton, SourceType.Income);
        SetGraphSwitch(expensesButton, SourceType.Expense);


        //-----LABELS
        todayAmount = findViewById(R.id.textView3);
        weekAmount = findViewById(R.id.textView5);
        monthAmount = findViewById(R.id.textView7);

        animateAlpha(todayAmount, 1000);
        animateAlpha(weekAmount, 1200);
        animateAlpha(monthAmount, 1400);


        //-----LAYOUTS
        todayLayout = findViewById(R.id.todayLayout);
        weekLayout = findViewById(R.id.weekLayout);
        monthLayout = findViewById(R.id.monthLayout);

        animateStatistics(todayLayout, 400);
        animateStatistics(weekLayout, 600);
        animateStatistics(monthLayout, 800);

        //-----TREND MINI ICONS
        todayTrendImage = findViewById(R.id.imageView5);
        weekTrendImage = findViewById(R.id.imageView6);
        monthTrendImage = findViewById(R.id.imageView7);

        animateAlpha(todayTrendImage, 1000);
        animateAlpha(weekTrendImage, 1200);
        animateAlpha(monthTrendImage, 1400);

        setLabelData(-214, todayAmount, todayTrendImage);
        setLabelData(5348, weekAmount, weekTrendImage);
        setLabelData(21548741, monthAmount, monthTrendImage);

        //-----TOP ACTION BAR
        actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Data Visualization");
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.topbar_box));

        }

        //----TAB LAYOUT
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition(), true);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();

            }
        });

    }

    private void SetGraphSwitch(Button button, SourceType sourceType){
        switch (sourceType){
            case Income:
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setFilter(true);
                    }
                });
                break;

            case Expense:
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setFilter(false);
                    }
                });
                break;
        }
    }

    void setFilter(boolean isPositiveTrend){
        ((TabGraphToday)viewPagerAdapter.todayFragment).setFilter(isPositiveTrend);
        ((TabGraphWeek)viewPagerAdapter.weekFragment).setFilter(isPositiveTrend);
        ((TabGraphMonth)viewPagerAdapter.monthFragment).setFilter(isPositiveTrend);
    }
    void setLabelData(int moneyAmount, TextView label, ImageView imageView){
        if(moneyAmount >= 0){
            label.setText("+" + Integer.toString(moneyAmount) + "€");
            label.setTextColor(getColor(R.color.green_005));
            imageView.setImageDrawable(getDrawable(R.drawable.positive_trend_icon));
        }
        else {
            label.setText(Integer.toString(moneyAmount) + "€");
            label.setTextColor(getColor(R.color.red));
            imageView.setImageDrawable(getDrawable(R.drawable.negative_trend_icon));
        }

    }
    void animateStatistics(View view, int delay){
        DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
        int screenY = displayMetrics.heightPixels;

        view.setTranslationY(screenY);

        view.postDelayed(()->{

            ObjectAnimator animator;
            animator = ObjectAnimator.ofFloat(view, "translationY", 0f);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(1000);

            animator.start();

        }, delay);
    }

    void animateAlpha(View view, int delay){
        view.setAlpha(0f);

        view.postDelayed(()->{


            ObjectAnimator animator;
            animator = ObjectAnimator.ofFloat(view, "alpha",  1f);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(2000);

            animator.start();

        }, delay);
    }
}
package com.example.finanseapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class GraphsActivity extends AppCompatActivity {

    ActionBar actionBar;

    PieChart pieChart;

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

        //-----TOP ACTION BAR
        actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.topbar_box));

        }

        pieChart = findViewById(R.id.pieChart);

        if(pieChart != null){

            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            pieEntries.add(new PieEntry(100f, "Part1"));
            pieEntries.add(new PieEntry(5f, "Part2"));
            pieEntries.add(new PieEntry(250f, "Part3"));
            pieEntries.add(new PieEntry(400f, "Part4"));
            pieEntries.add(new PieEntry(100f, "Part5"));

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "Siaip text");

            PieData pieData = new PieData(pieDataSet);

            pieChart.setData(pieData);
        }
    }
}
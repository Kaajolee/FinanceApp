package com.example.finanseapp;

import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class GraphsActivity extends AppCompatActivity {

    ActionBar actionBar;

    PieChart pieChart;
    LineChart lineChart;

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

        //-----LINE CHART
        lineChart = findViewById(R.id.lineChart);

        if(lineChart != null){

            ArrayList<Entry> lineEntries = GenerateData(25);
            LineDataSet lineDataSet = new LineDataSet(lineEntries, "TestLabel");
            LineData lineData = new LineData(lineDataSet);
            lineChart.setData(lineData);

        }

        //----PIE CHART
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
    private ArrayList<Entry> GenerateData(int amount){

        ArrayList<Entry> entries = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < amount; i++) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                Entry entry = new Entry(
                        (float)i,
                        random.nextFloat(10000f)
                );
            }
            Entry entry = new Entry(i, i * 2);

            entries.add(entry);
        }

        return entries;
    }

}
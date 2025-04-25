package com.example.finanseapp.Helpers;
import android.graphics.Color;
import android.os.Build;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {

    public static ArrayList<Entry> GenerateChartEntries(int amount, boolean isPositiveTrend) {
        ArrayList<Entry> entries = new ArrayList<>();
        Random random = new Random(System.nanoTime());

        float lastY = random.nextFloat() * 100;

        for (int i = 0; i < amount; i++) {

            float delta = (random.nextFloat() - 0.5f) * 20f;

            if (isPositiveTrend) {
                delta = Math.abs(delta) + random.nextFloat() * 5f;
            } else {
                delta = -Math.abs(delta) - random.nextFloat() * 5f;
            }

            lastY += delta;
            entries.add(new Entry(i, lastY));
        }

        return entries;
    }

    public static LineData GenerateLineData(boolean isPositiveTrend){

        ArrayList<Entry> lineEntries = DataGenerator.GenerateChartEntries(10, isPositiveTrend);
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "TestLabel");

        lineDataSet.setValueTextColor(Color.WHITE);

        return new LineData(lineDataSet);

    }

}

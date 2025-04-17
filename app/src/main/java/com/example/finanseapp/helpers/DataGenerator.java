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

    public static ArrayList<Entry> GenerateChartEntries(int amount, boolean isPositiveTrend){

        ArrayList<Entry> entries = new ArrayList<>();
        Random random = new Random();

        if(isPositiveTrend){
            for (int i = 0; i < amount; i++) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    Entry entry = new Entry(
                            (float) i,
                            random.nextFloat(10000f)
                    );
                }
                Entry entry = new Entry(i, i * 2);

                entries.add(entry);
            }
        }
        else {
            for (int i = 0; i < amount; i++) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    Entry entry = new Entry(
                            (float) i,
                            random.nextFloat(10000f) * -1
                    );
                }
                Entry entry = new Entry(i, i * -2);

                entries.add(entry);
            }
        }


        return entries;
    }
    public static LineData GenerateLineData(boolean isPositiveTrend){

        ArrayList<Entry> lineEntries = DataGenerator.GenerateChartEntries(3, isPositiveTrend);
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "TestLabel");

        lineDataSet.setValueTextColor(Color.WHITE);

        return new LineData(lineDataSet);

    }

}

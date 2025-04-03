package com.example.finanseapp.Helpers;
import android.os.Build;

import com.github.mikephil.charting.data.Entry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {

    public static ArrayList<Entry> GenerateChartEntries(int amount){

        ArrayList<Entry> entries = new ArrayList<>();
        Random random = new Random();
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

        return entries;
    }

}

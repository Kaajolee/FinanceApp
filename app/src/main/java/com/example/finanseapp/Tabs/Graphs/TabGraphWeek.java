package com.example.finanseapp.Tabs.Graphs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.finanseapp.Helpers.DataGenerator;
import com.example.finanseapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class TabGraphWeek extends Fragment {

    LineChart lineChart;
    boolean isPositive;
    public void setFilter(boolean isPositiveTrend){
        isPositive = isPositiveTrend;
        updateChart();
    }
    private void updateChart(){
        if(lineChart != null){

            lineChart.setData(DataGenerator.GenerateLineData(isPositive));
            int color = getContext().getColor(R.color.white_text);
            lineChart.invalidate();

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_tab_graph_week, container, false);

        lineChart = view.findViewById(R.id.lineChart);

        updateChart();

        return view;


    }
}
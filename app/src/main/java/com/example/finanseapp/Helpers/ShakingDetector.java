package com.example.finanseapp.Helpers;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finanseapp.R;
import com.google.android.material.tabs.TabLayout;
import com.squareup.seismic.ShakeDetector;

public class ShakingDetector implements ShakeDetector.Listener {

    Context context;
    Dialog deleteDialog;
    TabLayout tabLayout;
    int selectedTab;
    Button cancelButton, deleteButton;
    ShakeDetector detector;

    public ShakingDetector(Context context) {
        this.context = context;

        deleteDialog = new Dialog(context);
        deleteDialog.setContentView(R.layout.dialog_delete_all);
        deleteDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_all_corners_small_nontrans);

        configureButtons();
        configureTabLayout();

        //SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        detector = new ShakeDetector(this);
    }

    public void startShakeDetection() {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        boolean isDetecting = detector.start(sensorManager);

        if (!isDetecting)
            Log.w("SHAKING DETECTOR", "Device does not support shaking");
    }

    @Override
    public void hearShake() {
        VibrationCaller.vibrate(context, 200);
        deleteDialog.show();
    }

    public void configureTabLayout() {
        tabLayout = deleteDialog.findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
                
            }
        });
    }

    public void configureButtons() {
        cancelButton = deleteDialog.findViewById(R.id.buttonCancelDialog);
        deleteButton = deleteDialog.findViewById(R.id.buttonDelete);

        cancelButton.setOnClickListener(v -> deleteDialog.hide());
        deleteButton.setOnClickListener(v -> {
            //TODO: istrint is db ir atnaujint recycler view data+ui
            deleteDialog.hide();
        });
    }
}

package com.example.finanseapp.Helpers;
import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.seismic.ShakeDetector;

public class ShakingDetector implements ShakeDetector.Listener{

    Context context;
    public ShakingDetector(Context context){

        this.context = context;
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        ShakeDetector detector = new ShakeDetector(this);

        int sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;

        detector.start(sensorManager);
    }
    @Override
    public void hearShake(){
        Toast.makeText(context, "asd", Toast.LENGTH_SHORT).show();
        VibrationCaller.vibrate(context, 200);
    }
}
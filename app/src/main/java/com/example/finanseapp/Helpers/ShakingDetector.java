package com.example.finanseapp.Helpers;
import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.seismic.ShakeDetector;

public class ShakingDetector extends Activity implements ShakeDetector.Listener{
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector detector = new ShakeDetector(this);

        int sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;

        detector.start(sensorManager);

        TextView tv = new TextView(this);
        tv.setGravity(0);
        tv.setText("Shake me, bro!");
        setContentView(tv);

    }
    @Override public void hearShake(){
        Toast.makeText(this, "Shake detected", Toast.LENGTH_SHORT).show();
    }
}
package com.example.finanseapp.Helpers;

import android.content.Context;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

public class BlurOnFlipManager implements SensorEventListener {

    private final Context context;
    private final SensorManager sensorManager;
    private final Sensor rotationSensor;
    private final TextView[] viewsToBlur;

    private boolean isBlurred = false;
    private boolean wasFlipped = false;

    public BlurOnFlipManager(Context context, TextView... viewsToBlur) {
        this.context = context;
        this.viewsToBlur = viewsToBlur;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void register() {
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void unregister() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientation);

        float pitch = orientation[2];
        double pitchDegrees = Math.toDegrees(pitch);

        if ((pitchDegrees > 150 || pitchDegrees < -150)) {
            wasFlipped = true;
        }

        if ((pitchDegrees > -30 && pitchDegrees < 30) && wasFlipped && !isBlurred) {
            wasFlipped = false;
            triggerBlur();
        }
    }

    private void triggerBlur() {
        isBlurred = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffect blurEffect = RenderEffect.createBlurEffect(40f, 40f, Shader.TileMode.CLAMP);
            for (TextView view : viewsToBlur) {
                view.setRenderEffect(blurEffect);
            }
        } else {
            Toast.makeText(context, "Real blur requires Android 12+", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}

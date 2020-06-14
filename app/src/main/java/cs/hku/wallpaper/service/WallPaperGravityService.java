package cs.hku.wallpaper.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class WallPaperGravityService {
    public static void StartGravityListener(Activity activity) {
        SensorManager sm = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
        int sensorType = Sensor.TYPE_GRAVITY;

        assert sm != null;
        Sensor s = sm.getDefaultSensor(sensorType);

        sm.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                System.out.println("gravity: " + event.values[0] + " , " + event.values[1] + ", "+ event.values[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, s, SensorManager.SENSOR_DELAY_NORMAL);
    }
}

package cs.hku.wallpaper.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

import cs.hku.wallpaper.R;

public class WallPaperOrientationService {
    private static final String TAG = "WallPaper";
    private static int CurrentWallPaper = -1;
    private static SensorManager sm;
    //需要两个Sensor
    private static Sensor aSensor;
    private static Sensor mSensor;
    private static float currentDirection = 0;
    private static float[] accelerometerValues = new float[3];
    private static float[] magneticFieldValues = new float[3];
    private static double previous_z = 200;
    private static WallpaperManager wallpaperManager;

    final static SensorEventListener myListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                magneticFieldValues = sensorEvent.values.clone();
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                accelerometerValues = sensorEvent.values.clone();
            try {
                calculateOrientation();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    public static void StartGYROSCOPEListener(Activity activity) {
        wallpaperManager = WallpaperManager.getInstance(activity);
        sm = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        assert sm != null;
        Sensor gSensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sm.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] values = event.values;
                currentDirection += values[1];
                changeWallPaper(-90, (float) Math.toDegrees(currentDirection));
                Log.i(TAG, Arrays.toString(values));
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        }, gSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static void StartOrientationListener(Activity activity) {
        wallpaperManager = WallpaperManager.getInstance(activity);
        sm = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        assert sm != null;
        aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private static void calculateOrientation() throws IOException {
        float[] values = new float[3];
        float[] Res = new float[9];
        SensorManager.getRotationMatrix(Res, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(Res, values);

        // 要经过一次数据格式的转换，转换为度
        double z = (float) Math.toDegrees(values[0]);
        double x = (float) Math.toDegrees(values[1]);
        double y = (float) Math.toDegrees(values[2]);
        Log.i(TAG, z + ":" + x + ":" + y);

    }

    @SuppressLint("ResourceType")
    private static void changeWallPaper(float x, float y) {
        Log.i(TAG, String.valueOf(y));
        if (-100 <= x && x <= -80) {
            // 此时手机应该垂直于地面
            if (-10 <= y && y < 10) {
                // 表示正对北方
                setWallPaper(R.drawable.wall01);
            } else if (10 <= y && y < 30) {
                setWallPaper(R.drawable.wall02);
            } else if (30 <= y && y < 50 ) {
                setWallPaper(R.drawable.wall03);
            } else if (50 <= y && y < 70) {
                setWallPaper(R.drawable.wall04);
            } else if (70 <= y && y < 90) {
                setWallPaper(R.drawable.wall05);
            } else if (90 <= y && y < 110) {
                setWallPaper(R.drawable.wall06);
            } else if (110 <= y && y < 130) {
                setWallPaper(R.drawable.wall07);
            } else if (130 <= y && y < 150) {
                setWallPaper(R.drawable.wall08);
            } else if (150 <= y && y < 170) {
                setWallPaper(R.drawable.wall09);
            } else if (170 <= y && y < -170) {
                setWallPaper(R.drawable.wall10);
            } else if (-170 <= y && y < -150) {
                setWallPaper(R.drawable.wall11);
            } else if (-150 <= y && y < -130) {
                setWallPaper(R.drawable.wall12);
            } else if (-130 <= y && y < -110) {
                setWallPaper(R.drawable.wall01);
            } else if (-110 <= y && y < -90) {
                setWallPaper(R.drawable.wall02);
            } else if (-90 <= y && y < -70) {
                setWallPaper(R.drawable.wall03);
            } else if (-70 <= y && y < -50) {
                setWallPaper(R.drawable.wall04);
            } else if (-50 <= y && y < -30) {
                setWallPaper(R.drawable.wall05);
            } else if (-30 <= y && y < -10) {
                setWallPaper(R.drawable.wall06);
            }
        }
    }
    private static void setWallPaper(int Resource){
        if (Resource == CurrentWallPaper) return;
        CurrentWallPaper = Resource;
        try {
            wallpaperManager.setResource(Resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

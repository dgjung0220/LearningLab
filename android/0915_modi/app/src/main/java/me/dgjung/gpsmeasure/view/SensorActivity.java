package me.dgjung.gpsmeasure.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.dgjung.gpsmeasure.R;
import me.dgjung.gpsmeasure.db.DatabaseContract;
import me.dgjung.gpsmeasure.db.DatabaseHelper;
import me.dgjung.gpsmeasure.service.FileLogger;

import static me.dgjung.gpsmeasure.service.Utils.getCurrentTimes;
import static me.dgjung.gpsmeasure.service.Utils.getFilename;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private String TAG = "SensorActivity";
    private TextView stepCount, logView;
    private Button finishButton;

    // Sensor...
    private LocationManager mLocationManager;
    private SensorManager sensorManager;
    private Sensor mLinearAcceleration, mAccelerometer, mGyro, mMagnetic;
    private float[] mGravity, mGeomagnetic;

    // Sensor value variables
    private float accel_x, accel_y, accel_z, gyro_x, gyro_y, gyro_z, azimuth, pitch, roll;
    private static final long LOCATION_RATE_GPS_MS = TimeUnit.MILLISECONDS.toMillis(1L);
    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;

    // Log ...
    private ArrayList<FileLogger> loggers;
    private FileLogger mFileLogger_location, mFileLogger_acceleration, mFileLogger_gyroscope, mFileLogger_orientation;

    // DB ..
    private DatabaseHelper dbhelper;
    SQLiteDatabase db;

    // Spend time
    private int spendTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        finishButton = findViewById(R.id.finish_button);
        stepCount = findViewById(R.id.stepCount_value);
        logView = findViewById(R.id.log_view);

        prepareSensors();

        loggers = new ArrayList<>();
        mFileLogger_location = new FileLogger(getApplicationContext());
        mFileLogger_location.startNewLog(FileLogger.LOCATION_PREFIX);
        loggers.add(mFileLogger_location);

        mFileLogger_acceleration = new FileLogger(getApplicationContext());
        mFileLogger_acceleration.startNewLog(FileLogger.ACCELERATION_PREFIX);
        loggers.add(mFileLogger_acceleration);

        mFileLogger_gyroscope = new FileLogger(getApplicationContext());
        mFileLogger_gyroscope.startNewLog(FileLogger.GYROSCOPE_PREFIX);
        loggers.add(mFileLogger_gyroscope);

        mFileLogger_orientation = new FileLogger(getApplicationContext());
        mFileLogger_orientation.startNewLog(FileLogger.ORIENTATION_PREFIX);
        loggers.add(mFileLogger_orientation);

        dbhelper = new DatabaseHelper(this);
        db = dbhelper.getWritableDatabase();

        finishButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                unregisterAll();

                ArrayList<File> file_list = new ArrayList<>();
                for (FileLogger logger : loggers) {
                    logger.send();

                    file_list.add(logger.getMfile());
                }

                Random rnd = new Random();

                ContentValues values = new ContentValues();
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_1, 0000);
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_2, spendTime);
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_3, getCurrentTimes());
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_4, getFilename(String.valueOf(file_list.get(0))));
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_5, getFilename(String.valueOf(file_list.get(1))) + "\n" + getFilename(String.valueOf(file_list.get(2))));
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_6, "null");
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_7, "null");
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_8, "null");

                long rowId = db.insert(DatabaseContract.MeasurementsDatabase.TABLE_NAME, null, values);
                if (rowId != -1) {
                    Toast.makeText(SensorActivity.this, "Add successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SensorActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(SensorActivity.this, MainActivity.class);
                intent.putExtra("FILES", file_list);
                startActivity(intent);
                finish();
            }
        });
    }

    public void prepareSensors() {

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        registerAll();
    }

    public void registerAll() {
        spendTime = 0;
        // register sensor...
        registerLocation();
        registerSensors();
    }

    public void unregisterAll() {
        mLocationManager.removeUpdates(mLocationListener);
        sensorManager.unregisterListener(this);
    }

    public void registerSensors() {
        mLinearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(this, mLinearAcceleration, SENSOR_DELAY);
        sensorManager.registerListener(this, mAccelerometer, SENSOR_DELAY);
        sensorManager.registerListener(this, mGyro, SENSOR_DELAY);
        sensorManager.registerListener(this, mMagnetic, SENSOR_DELAY);
    }

    public void registerLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGPSEnabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_RATE_GPS_MS,
                    0.0f, mLocationListener);
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationChanged(Location location) {

            spendTime ++;

            String locationStream =
                    String.format(
                            Locale.US,
                            "%d,%f,%f,%f,%f,%f,%f,%f",
                            location.getTime(),
                            location.getLatitude(),
                            location.getLongitude(),
                            location.getBearing(),
                            location.getSpeed(),
                            azimuth,
                            pitch,
                            roll
                    );

            StringBuilder log = new StringBuilder();
            log.append(logView.getText());
            log.append('\n');
            log.append(locationStream);

            logView.setText(log);

            Log.d(TAG, locationStream);

            try {
                mFileLogger_location.writeLogs(locationStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {

        int sensorType = event.sensor.getType();

        if (sensorType == Sensor.TYPE_LINEAR_ACCELERATION) {
            accel_x = event.values[0];
            accel_y = event.values[1];
            accel_z = event.values[2];

            String logStream = String.format(Locale.US, "%d,%f,%f,%f", new Date().getTime(), accel_x, accel_y, accel_z);
            try {
                mFileLogger_acceleration.writeLogs(logStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (sensorType == Sensor.TYPE_GYROSCOPE) {
            gyro_x = event.values[0];
            gyro_y = event.values[1];
            gyro_z = event.values[2];

            String logStream = String.format(Locale.US, "%d,%f,%f,%f", new Date().getTime(), gyro_x, gyro_y, gyro_z);
            try {
                mFileLogger_gyroscope.writeLogs(logStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (sensorType == Sensor.TYPE_ACCELEROMETER) { mGravity = event.values; }
        if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) { mGeomagnetic = event.values; }

        if (mGravity != null && mGeomagnetic != null) {

            //Log.d(TAG, "test");
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {

                float[] outGravity = new float[9];
                SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Z, outGravity);
                float orientationData[] = new float[3];
                SensorManager.getOrientation(outGravity, orientationData);

                azimuth = orientationData[0] * 57.2957795f;
                pitch = orientationData[1] * 57.2957795f;
                roll = orientationData[2] * 57.2957795f;

                String logStream = String.format(Locale.US, "%d,%f,%f,%f", new Date().getTime(), azimuth, pitch, roll);
                try {
                    mFileLogger_orientation.writeLogs(logStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}

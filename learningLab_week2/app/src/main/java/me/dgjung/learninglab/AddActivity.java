package me.dgjung.learninglab;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class AddActivity extends AppCompatActivity {

    private String TAG = "AddActivity";
    private TextView stepCount, logView;
    private Button finishButton;

    // Sensor...
    private LocationManager mLocationManager;

    private static final long LOCATION_RATE_GPS_MS = TimeUnit.SECONDS.toMillis(1L);
    private static final long LOCATION_RATE_NETWORK_MS = TimeUnit.SECONDS.toMillis(60L);

    // Log ...
    private String locationStream = null;
    private FileLogger mFileLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        finishButton = findViewById(R.id.finish_button);
        stepCount = findViewById(R.id.stepCount_value);
        logView = findViewById(R.id.log_view);

        Log.d(TAG, "onCreate");
        prepareSensors();

        mFileLogger = new FileLogger(getApplicationContext());
        mFileLogger.startNewLog();


        finishButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFileLogger.send();

                File mFile = mFileLogger.getMfile();

                if (mFile != null) {
//                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
//                    emailIntent.setType("*/*");
//                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SensorLog");
//                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
//
//                    // attach the file
//                    Uri fileURI =
//                            FileProvider.getUriForFile(AddActivity.this, BuildConfig.APPLICATION_ID, mFile);
//                    emailIntent.putExtra(Intent.EXTRA_STREAM, fileURI);
//
//                    emailIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
//                    emailIntent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
//
//                    startActivity(Intent.createChooser(emailIntent, "Send logs..."));

                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    intent.putExtra("FILE", mFile);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }


    public void prepareSensors() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Log.d(TAG, "prepareSensors");
        registerAll();
    }

    public void registerAll() {
        // register sensor...
        registerLocation();
    }

    public void registerLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGPSEnabled) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_RATE_NETWORK_MS,
                    0.0f, mLocationListener);

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

            locationStream=
                    String.format(
                            Locale.US,
                            "%s,%s,%d,%f,%f,%f,%f,%f,%f,%f,%f",
                            location.getProvider(),
                            getCurrentTimes(),
                            location.getTime(),
                            location.getLatitude(),
                            location.getLongitude(),
                            location.getAltitude(),
                            location.getBearing(),
                            location.getBearingAccuracyDegrees(),
                            location.getAccuracy(),
                            location.getSpeed(),
                            location.getSpeedAccuracyMetersPerSecond()
                    );

            Log.d(TAG, locationStream);

            StringBuilder log = new StringBuilder();
            log.append(logView.getText());
            log.append('\n');
            log.append(locationStream);

            logView.setText(log);
            try {
                mFileLogger.writeLogs(locationStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    };

    private final String getCurrentTimes() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formatDate = sdfNow.format(date);

        return formatDate;
    }
}

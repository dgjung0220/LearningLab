package me.dgjung.learninglab;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AddActivity extends AppCompatActivity {

    private String TAG = "AddActivity";
    private TextView stepCount, logView;
    private Button finishButton;

    // Sensor...
    private LocationManager mLocationManager;

    private static final long LOCATION_RATE_GPS_MS = TimeUnit.SECONDS.toMillis(1L);
    private static final long LOCATION_RATE_NETWORK_MS = TimeUnit.SECONDS.toMillis(60L);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        finishButton = findViewById(R.id.finish_button);
        stepCount = findViewById(R.id.stepCount_value);
        logView = findViewById(R.id.log_view);

        Log.d(TAG, "onCreate");
        prepareSensors();

        finishButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

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

            String locationStream=
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

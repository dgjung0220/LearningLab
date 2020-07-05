package me.dgjung.gpsmeasure.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.dgjung.gpsmeasure.R;
import me.dgjung.gpsmeasure.db.DatabaseContract;
import me.dgjung.gpsmeasure.db.DatabaseHelper;
import me.dgjung.gpsmeasure.service.FileLogger;

import static me.dgjung.gpsmeasure.service.Utils.getCurrentTimes;
import static me.dgjung.gpsmeasure.service.Utils.getFilename;

public class SensorActivity extends AppCompatActivity {

    private String TAG = "SensorActivity";
    private TextView stepCount, logView;
    private Button finishButton;

    // Sensor...
    private LocationManager mLocationManager;

    private static final long LOCATION_RATE_GPS_MS = TimeUnit.SECONDS.toMillis(1L);
    private static final long LOCATION_RATE_NETWORK_MS = TimeUnit.SECONDS.toMillis(60L);

    // Log ...
    private ArrayList<FileLogger> loggers;
    private FileLogger mFileLogger_location, mFileLogger_measurement, mFileLogger_gnssClock;

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

        loggers = new ArrayList<>();
        prepareSensors();

        mFileLogger_location = new FileLogger(getApplicationContext());
        mFileLogger_location.startNewLog(FileLogger.LOCATION_PREFIX);
        loggers.add(mFileLogger_location);

        new JSONLoggerTask().execute("http://172.30.1.16:3000/post","SOF", "SOF");

        mFileLogger_measurement = new FileLogger(getApplicationContext());
        mFileLogger_measurement.startNewLog(FileLogger.MEASUREMENT_PREFIX);
        loggers.add(mFileLogger_measurement);

        mFileLogger_gnssClock = new FileLogger(getApplicationContext());
        mFileLogger_gnssClock.startNewLog(FileLogger.CLOCK_PREFIX);
        loggers.add(mFileLogger_gnssClock);

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

                new JSONLoggerTask().execute("http://172.30.1.16:3000/post","EOF", "EOF");

                Random rnd = new Random();

                ContentValues values = new ContentValues();
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_1, rnd.nextInt(5000) + 2000);
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_2, spendTime);
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_3, getCurrentTimes());
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_4, getFilename(String.valueOf(file_list.get(0))));
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_5, getFilename(String.valueOf(file_list.get(1))) + "\n" + getFilename(String.valueOf(file_list.get(2))));
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_6, "navigation_file_path.csv");
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_7, "gpsStatus_file_path.csv");
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_8, "nmea_file_path.csv");

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

        registerAll();
    }

    public void registerAll() {
        spendTime = 0;

        // register sensor...
        registerLocation();
        registerMeasurements();
    }

    public void unregisterAll() {

        mLocationManager.removeUpdates(mLocationListener);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.registerGnssMeasurementsCallback(gnssMeasurementsEventListener);
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

            spendTime ++;

            String locationStream =
                    String.format(
                            Locale.US,
                            "%s,%s,%d,%f,%f,%f,%f,%f,%f",
                            location.getProvider(),
                            getCurrentTimes(),
                            location.getTime(),
                            location.getLatitude(),
                            location.getLongitude(),
                            location.getAltitude(),
                            location.getBearing(),
                            location.getAccuracy(),
                            location.getSpeed()
                    );

            Log.d(TAG, locationStream);

            StringBuilder log = new StringBuilder();
            log.append(logView.getText());
            log.append('\n');
            log.append(locationStream);

            logView.setText(log);
            try {
                mFileLogger_location.writeLogs(locationStream);
                new JSONLoggerTask().execute("http://172.30.1.16:3000/post","location",locationStream);
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

    public void registerMeasurements() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.registerGnssMeasurementsCallback(gnssMeasurementsEventListener);
    }

    private final GnssMeasurementsEvent.Callback gnssMeasurementsEventListener =
            new GnssMeasurementsEvent.Callback() {
                @Override
                public void onGnssMeasurementsReceived(GnssMeasurementsEvent event) {

                    GnssClock gnssClock = event.getClock();
                    for (GnssMeasurement measurement : event.getMeasurements()) {
                        String clockStream =
                                String.format(
                                        "Raw,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                                        SystemClock.elapsedRealtime(),
                                        gnssClock.getTimeNanos(),
                                        gnssClock.hasLeapSecond() ? gnssClock.getLeapSecond() : "",
                                        gnssClock.hasTimeUncertaintyNanos() ? gnssClock.getTimeUncertaintyNanos() : "",
                                        gnssClock.getFullBiasNanos(),
                                        gnssClock.hasBiasNanos() ? gnssClock.getBiasNanos() : "",
                                        gnssClock.hasBiasUncertaintyNanos() ? gnssClock.getBiasUncertaintyNanos() : "",
                                        gnssClock.hasDriftNanosPerSecond() ? gnssClock.getDriftNanosPerSecond() : "",
                                        gnssClock.hasDriftUncertaintyNanosPerSecond()
                                                ? gnssClock.getDriftUncertaintyNanosPerSecond()
                                                : "",
                                        gnssClock.getHardwareClockDiscontinuityCount() + ",");

                        try {
                            mFileLogger_gnssClock.writeLogs(clockStream);
                            //new JSONLoggerTask().execute("http://172.30.1.16:3000/post","gnssClock",clockStream);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String measurementStream =
                                String.format(
                                        "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                                        measurement.getSvid(),
                                        measurement.getTimeOffsetNanos(),
                                        measurement.getState(),
                                        measurement.getReceivedSvTimeNanos(),
                                        measurement.getReceivedSvTimeUncertaintyNanos(),
                                        measurement.getCn0DbHz(),
                                        measurement.getPseudorangeRateMetersPerSecond(),
                                        measurement.getPseudorangeRateUncertaintyMetersPerSecond(),
                                        measurement.getAccumulatedDeltaRangeState(),
                                        measurement.getAccumulatedDeltaRangeMeters(),
                                        measurement.getAccumulatedDeltaRangeUncertaintyMeters(),
                                        measurement.hasCarrierFrequencyHz() ? measurement.getCarrierFrequencyHz() : "",
                                        measurement.hasCarrierCycles() ? measurement.getCarrierCycles() : "",
                                        measurement.hasCarrierPhase() ? measurement.getCarrierPhase() : "",
                                        measurement.hasCarrierPhaseUncertainty()
                                                ? measurement.getCarrierPhaseUncertainty()
                                                : "",
                                        measurement.getMultipathIndicator(),
                                        measurement.hasSnrInDb() ? measurement.getSnrInDb() : "",
                                        measurement.getConstellationType(),
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                                                && measurement.hasAutomaticGainControlLevelDb()
                                                ? measurement.getAutomaticGainControlLevelDb()
                                                : "",
                                        measurement.hasCarrierFrequencyHz() ? measurement.getCarrierFrequencyHz() : "");

                        try {
                            mFileLogger_measurement.writeLogs(measurementStream);
                            //new JSONLoggerTask().execute("http://172.30.1.16:3000/post","measurement",measurementStream);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onStatusChanged(int status) {

                }
            };


    public class JSONLoggerTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("type", strings[1]);
                jsonObject.accumulate("data", strings[2]);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    URL url = new URL(strings[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");

                    con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "text/html");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.connect();

                    OutputStream outStream = con.getOutputStream();

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    InputStream stream = null;
                    int status = con.getResponseCode();

                    if (status != HttpURLConnection.HTTP_OK) {
                        stream = con.getErrorStream();
                    } else {
                        stream = con.getInputStream();
                    }

                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }
                    return buffer.toString();
                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                    if(reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
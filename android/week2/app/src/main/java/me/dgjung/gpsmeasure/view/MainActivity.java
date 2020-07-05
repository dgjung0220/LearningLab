package me.dgjung.gpsmeasure.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import me.dgjung.gpsmeasure.BuildConfig;
import me.dgjung.gpsmeasure.R;
import me.dgjung.gpsmeasure.adapter.MeasureAdapter;
import me.dgjung.gpsmeasure.db.DatabaseContract;
import me.dgjung.gpsmeasure.db.DatabaseHelper;
import me.dgjung.gpsmeasure.service.Utils;
import me.dgjung.gpsmeasure.vo.Measurements;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static me.dgjung.gpsmeasure.service.Utils.getCurrentTimes;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbhelper;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter measureAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView hardwareName, hardwareYear;

    Button btnMeasureStart;
    private List<Measurements> measurementsList;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Permission  check
        ActivityCompat.requestPermissions(this, Utils.permissionUtils.REQUIRED_PERMISSIONS, Utils.permissionUtils.PERMISSION_REQUEST_CODE);


        dbhelper = new DatabaseHelper(this);
        btnMeasureStart = findViewById(R.id.measure_start_btn);
        db = dbhelper.getReadableDatabase();
        mRecyclerView = findViewById(R.id.main_recyclerview);
        hardwareName = findViewById(R.id.hardware_name_value);
        hardwareYear = findViewById(R.id.hardware_year_value);

        btnMeasureStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SensorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        ArrayList<File> file_list = (ArrayList<File>) intent.getSerializableExtra("FILES");

        if (file_list != null ){
            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            emailIntent.setType("*/*");
            //emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SensorLog");
            //emailIntent.putExtra(Intent.EXTRA_TEXT, "");

            ArrayList<Parcelable> urls = new ArrayList<>();

            for(File mFile : file_list) {

                Uri fileURI =
                        FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, mFile);
                urls.add(fileURI);
            }

            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, urls);
            emailIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivity(Intent.createChooser(emailIntent, "Send logs..."));
        }

        measurementsList = new ArrayList<>();
        measurementsList.clear();

        measurementsList = showDBs();

        layoutManager = new LinearLayoutManager(this);
        measureAdapter = new MeasureAdapter(measurementsList);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(measureAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public List<Measurements> showDBs() {

        ArrayList<Measurements> resultList = new ArrayList<>();
        Cursor c1 = db.query(DatabaseContract.MeasurementsDatabase.TABLE_NAME, null, null, null, null, null, DatabaseContract.MeasurementsDatabase._ID + " desc");

        if (c1 != null && c1.getCount() != 0) {
            resultList.clear();

            while (c1.moveToNext()) {
                Measurements measureItems = new Measurements();
                measureItems.set_id(c1.getInt(c1.getColumnIndex(DatabaseContract.MeasurementsDatabase._ID)));
                measureItems.setStepCount(c1.getInt(c1.getColumnIndex(DatabaseContract.MeasurementsDatabase.COL_NAME_1)));
                measureItems.setSpendTime(c1.getInt(c1.getColumnIndex(DatabaseContract.MeasurementsDatabase.COL_NAME_2)));
                measureItems.setMeasureDate(c1.getString(c1.getColumnIndex(DatabaseContract.MeasurementsDatabase.COL_NAME_3)));
                measureItems.setLocationFilePath(c1.getString(c1.getColumnIndex(DatabaseContract.MeasurementsDatabase.COL_NAME_4)));
                measureItems.setMeasurementFilePath(c1.getString(c1.getColumnIndex(DatabaseContract.MeasurementsDatabase.COL_NAME_5)));
                measureItems.setNavigationFilePath(c1.getString(c1.getColumnIndex(DatabaseContract.MeasurementsDatabase.COL_NAME_6)));
                measureItems.setGpsStatusFilePath(c1.getString(c1.getColumnIndex(DatabaseContract.MeasurementsDatabase.COL_NAME_7)));
                measureItems.setNmeaFilePath(c1.getString(c1.getColumnIndex(DatabaseContract.MeasurementsDatabase.COL_NAME_8)));

                resultList.add(measureItems);
            }
        }

        c1.close();

        return resultList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Utils.permissionUtils.PERMISSION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "위치 서비스 제한으로 앱을 수행할 수 없습니다.", Toast.LENGTH_SHORT).show();
                btnMeasureStart.setEnabled(false);
            } else {
                btnMeasureStart.setEnabled(true);

                LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        hardwareName.setText(mLocationManager.getGnssHardwareModelName());
                        hardwareYear.setText(String.valueOf(mLocationManager.getGnssYearOfHardware()));
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
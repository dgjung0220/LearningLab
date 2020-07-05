package me.dgjung.gpsmeasure.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import me.dgjung.gpsmeasure.R;
import me.dgjung.gpsmeasure.adapter.MeasureAdapter;
import me.dgjung.gpsmeasure.db.DatabaseContract;
import me.dgjung.gpsmeasure.db.DatabaseHelper;
import me.dgjung.gpsmeasure.vo.Measurements;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbhelper;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter measureAdapter;
    private RecyclerView.LayoutManager layoutManager;

    Button btnMeasureStart;
    private List<Measurements> measurementsList;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbhelper = new DatabaseHelper(this);
        btnMeasureStart = findViewById(R.id.measure_start_btn);
        db = dbhelper.getReadableDatabase();
        mRecyclerView = findViewById(R.id.main_recyclerview);

        btnMeasureStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // add
                // RANDOM DATA
                Random rnd = new Random();

                ContentValues values = new ContentValues();
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_1, rnd.nextInt(5000) + 200);
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_2, rnd.nextInt(500) + 200);
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_3, getCurrentTimes());

                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_4, "location_file_path.csv");
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_5, "measure_file_path.csv");
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_6, "navigation_file_path.csv");
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_7, "gpsStatus_file_path.csv");
                values.put(DatabaseContract.MeasurementsDatabase.COL_NAME_8, "nmea_file_path.csv");

                long rowId = db.insert(DatabaseContract.MeasurementsDatabase.TABLE_NAME, null, values);
                if (rowId != -1) {
                    Toast.makeText(MainActivity.this, "Add successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }

                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

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

    private final String getCurrentTimes() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formatDate = sdfNow.format(date);

        return formatDate;
    }
}
package me.dgjung.learninglab;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button addButton;
    private TextView hardwareName, hardwareYear;
    public static View.OnClickListener itemsOnClickListener;

    // DB
    private DBOpenHelper mDBOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Permission  check
        ActivityCompat.requestPermissions(this, PermissionUtils.REQUIRED_PERMISSIONS, PermissionUtils.PERMISSION_REQUEST_CODE);

        itemsOnClickListener = new ItemsOnClickListener(this);

        mRecyclerView = findViewById(R.id.main_recyclerview);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        addButton = findViewById(R.id.main_button);
        hardwareName = findViewById(R.id.hardware_name_value);
        hardwareYear = findViewById(R.id.hardware_year_value);

        /* make test data */
        //List<Items> data = initializeData();

        mDBOpenHelper = new DBOpenHelper(this);
        mDBOpenHelper.open();
        mDBOpenHelper.create();

        List<Items> data = showDBs("_ID");
        mAdapter = new ItemAdapter(this,data);
        mRecyclerView.setAdapter(mAdapter);

        addButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
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
    }

    private static class ItemsOnClickListener implements View.OnClickListener {

        private final Context context;

        private ItemsOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            // click 시 생성...
            Toast.makeText(context, "TEST", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtils.PERMISSION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "위치 서비스 제한으로 앱을 수행할 수 없습니다.", Toast.LENGTH_SHORT).show();
                addButton.setEnabled(false);
            } else {
                addButton.setEnabled(true);

                LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                try {
                    hardwareName.setText(mLocationManager.getGnssHardwareModelName());
                    hardwareYear.setText(String.valueOf(mLocationManager.getGnssYearOfHardware()));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Items> showDBs(String sort) {

        ArrayList<Items> list = new ArrayList<>();
        Cursor iCursor = mDBOpenHelper.sortColumns(sort);

        while(iCursor.moveToNext()) {

            String id = iCursor.getString(iCursor.getColumnIndex("_id"));
            int stepCount = iCursor.getInt(iCursor.getColumnIndex(Databases.CreateDB.STEPCOUNT));
            int spendTime = iCursor.getInt(iCursor.getColumnIndex(Databases.CreateDB.SPENDTIME));
            String measureDate = iCursor.getString(iCursor.getColumnIndex(Databases.CreateDB.MESUREDATE));
            String locationFilePath = iCursor.getString(iCursor.getColumnIndex(Databases.CreateDB.LOCATIONFILEPATH));
            String measurementFilePath = iCursor.getString(iCursor.getColumnIndex(Databases.CreateDB.MEASUREMENTFILEPATH));
            String navigationFilePath = iCursor.getString(iCursor.getColumnIndex(Databases.CreateDB.NAVIGATIONFILEPATH));
            String gpsStatusFilePath = iCursor.getString(iCursor.getColumnIndex(Databases.CreateDB.GPSSTATUSFILEPATH));
            String nmeaFilePath = iCursor.getString(iCursor.getColumnIndex(Databases.CreateDB.NMEAFILEPATH));

            Log.d("DBS", id +","+ stepCount +","+ spendTime +","+measureDate+","+locationFilePath+","+measurementFilePath+","+navigationFilePath+","+gpsStatusFilePath+","+nmeaFilePath);
            list.add(new Items(Integer.parseInt(id), stepCount, spendTime, measureDate, locationFilePath, measurementFilePath, navigationFilePath, gpsStatusFilePath, nmeaFilePath));
        }
        return list;
    }

    public boolean deleteColumns(int index) {

        boolean result = mDBOpenHelper.deleteColumn(index);
        return result;
    }
}
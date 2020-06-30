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
import java.util.Date;
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
    public ArrayList<String> arrayIndex =  new ArrayList<String>();
    public ArrayList<String> arrayData = new ArrayList<String>();
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

        //mAdapter = new ItemAdapter(data);
        //mRecyclerView.setAdapter(mAdapter);

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



//        if (item != null) {
//            Log.d("JDG", String.valueOf(item.getSpendTime()));
//            Log.d("JDG", item.getMeasureDate());
//            Log.d("JDG", item.getLocationFilePath());
//        }
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

    public void showDatabase() {

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
}
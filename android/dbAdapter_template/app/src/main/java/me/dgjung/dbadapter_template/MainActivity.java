package me.dgjung.dbadapter_template;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter userAdapter;
    private RecyclerView.LayoutManager layoutManager;

    Button btnRegister;
    List<UserDetails> userDetailsList;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 생성
        dbHelper = new DatabaseHelper(this);
        btnRegister = findViewById(R.id.bt_register);
        db = dbHelper.getReadableDatabase();
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_users);


        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        userDetailsList = new ArrayList<>();
        userDetailsList.clear();

        Cursor c1 = db.query(DatabaseContract.UserDatabase.TABLE_NAME, null, null, null, null, null, null);
        if (c1 != null && c1.getCount() != 0) {
            userDetailsList.clear();

            while (c1.moveToNext()) {
                UserDetails userDetailsItem = new UserDetails();
                userDetailsItem.setUserId(c1.getInt(c1.getColumnIndex(DatabaseContract.UserDatabase._ID)));
                userDetailsItem.setName(c1.getString(c1.getColumnIndex(DatabaseContract.UserDatabase.COLUMN_NAME_COLS1)));
                userDetailsItem.setAddress(c1.getString(c1.getColumnIndex(DatabaseContract.UserDatabase.COLUMN_NAME_COLS2)));
                userDetailsItem.setMobileNo(c1.getString(c1.getColumnIndex(DatabaseContract.UserDatabase.COLUMN_NAME_COLS3)));
                userDetailsItem.setProfessiion(c1.getString(c1.getColumnIndex(DatabaseContract.UserDatabase.COLUMN_NAME_COLS4)));
                userDetailsList.add(userDetailsItem);
            }
        }

        c1.close();

        layoutManager = new LinearLayoutManager(this);
        userAdapter = new UserDetailsAdapter(userDetailsList);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(userAdapter);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
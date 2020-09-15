package me.dgjung.dbadapter_template;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    String name, address, phone, profession;
    SQLiteDatabase db;
    private EditText etName, etAddress, etPhone, etProfession;
    private Button btRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeration_activity);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        etName = (EditText) findViewById(R.id.et_name);
        etAddress = (EditText) findViewById(R.id.et_address);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etProfession = (EditText) findViewById(R.id.et_pro);
        btRegister = (Button) findViewById(R.id.bt_registration);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                address = etAddress.getText().toString();
                phone = etPhone.getText().toString();
                profession = etProfession.getText().toString();

                ContentValues values = new ContentValues();
                values.put(DatabaseContract.UserDatabase.COLUMN_NAME_COLS1, name);
                values.put(DatabaseContract.UserDatabase.COLUMN_NAME_COLS2, address);
                values.put(DatabaseContract.UserDatabase.COLUMN_NAME_COLS3, phone);
                values.put(DatabaseContract.UserDatabase.COLUMN_NAME_COLS4, profession);
                long rowId = db.insert(DatabaseContract.UserDatabase.TABLE_NAME, null, values);

                if (rowId != -1) {
                    Toast.makeText(RegistrationActivity.this, "User regstered succesfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegistrationActivity.this, "Something Went Wrong! ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
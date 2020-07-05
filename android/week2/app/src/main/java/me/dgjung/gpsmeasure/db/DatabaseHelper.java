package me.dgjung.gpsmeasure.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME= "MeasurementGPS.db";

    private static final String CREATE_USER_TABLE = "CREATE TABLE " + DatabaseContract.MeasurementsDatabase.TABLE_NAME + "( "
            + DatabaseContract.MeasurementsDatabase._ID + " INTEGER PRIMARY KEY,"
            + DatabaseContract.MeasurementsDatabase.COL_NAME_1 + " INTEGER NOT NULL,"
            + DatabaseContract.MeasurementsDatabase.COL_NAME_2 + " INTEGER NOT NULL,"
            + DatabaseContract.MeasurementsDatabase.COL_NAME_3 + " TEXT NOT NULL,"
            + DatabaseContract.MeasurementsDatabase.COL_NAME_4 + " TEXT NOT NULL,"
            + DatabaseContract.MeasurementsDatabase.COL_NAME_5 + " TEXT NOT NULL,"
            + DatabaseContract.MeasurementsDatabase.COL_NAME_6 + " TEXT NOT NULL,"
            + DatabaseContract.MeasurementsDatabase.COL_NAME_7 + " TEXT NOT NULL,"
            + DatabaseContract.MeasurementsDatabase.COL_NAME_8 + " TEXT NOT NULL"
            + ")";

    private static final String DELETE_USER_TABLE = "DROP TABLE IF EXISTS " + DatabaseContract.MeasurementsDatabase.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_USER_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

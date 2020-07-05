package me.dgjung.dbadapter_template;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "UserRegisteration.db";

    private static final String CREATE_USER_TABLE = "CREATE TABLE " + DatabaseContract.UserDatabase.TABLE_NAME + "( "
            + DatabaseContract.UserDatabase._ID + " INTEGER PRIMARY KEY,"
            + DatabaseContract.UserDatabase.COLUMN_NAME_COLS1 + " text,"
            + DatabaseContract.UserDatabase.COLUMN_NAME_COLS2 + " text,"
            + DatabaseContract.UserDatabase.COLUMN_NAME_COLS3 + " text,"
            + DatabaseContract.UserDatabase.COLUMN_NAME_COLS4 + " text"+ ")";
    private static final String DELETE_USER_TABLE = "DROP TABLE IF EXISTS " + DatabaseContract.UserDatabase.TABLE_NAME;


    public DatabaseHelper(@Nullable Context context) {
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

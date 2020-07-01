package me.dgjung.learninglab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* https://m.blog.naver.com/nife0719/221035148567 */

public class DBOpenHelper {

    private static final String DATABASE_NAME = "measureSQLite.db";
    private static final int DATABASE_VERSION = 1;

    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mContext;


    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);

        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Databases.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Databases.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }

    public DBOpenHelper(Context context) {
        this.mContext = context;
    }

    public DBOpenHelper open() throws SQLException {

        mDBHelper = new DatabaseHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create() {
        mDBHelper.onCreate(mDB);
    }

    public void close() {
        mDB.close();
    }

    /* INSERT */

   /* private int stepCount;
    private int spendTime;
    private Long measureDate;

    private String locationFilePath;
    private String measurementFilePath;
    private String navigationFilePath;
    private String gpsStatusFilePath;
    private String nmeaFilePath;*/

    public long insertColumn(int stepCount, int spendTime, String measureDate, String locationFilePath, String measureFilePath,
                             String navigationFilePath, String gpsStatusFilePath, String nmeaFilePath) {

        ContentValues values = new ContentValues();
        values.put(Databases.CreateDB.STEPCOUNT, stepCount);
        values.put(Databases.CreateDB.SPENDTIME, spendTime);
        values.put(Databases.CreateDB.MESUREDATE, measureDate);
        values.put(Databases.CreateDB.LOCATIONFILEPATH, locationFilePath);
        values.put(Databases.CreateDB.MEASUREMENTFILEPATH, measureFilePath);
        values.put(Databases.CreateDB.NAVIGATIONFILEPATH, navigationFilePath);
        values.put(Databases.CreateDB.GPSSTATUSFILEPATH, gpsStatusFilePath);
        values.put(Databases.CreateDB.NMEAFILEPATH, nmeaFilePath);

        return mDB.insert(Databases.CreateDB._TABLENAME0, null, values);
    }

    public Cursor selectColumns() {
        return mDB.query(Databases.CreateDB._TABLENAME0, null, null, null, null, null, null, null);
    }

    public boolean deleteColumn(long id) {
        return mDB.delete(Databases.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }

    public Cursor sortColumns(String sort) {
        Cursor c = mDB.rawQuery("SELECT * FROM " + Databases.CreateDB._TABLENAME0
                + " ORDER BY " + sort + " desc;", null);

        return c;
    }


}

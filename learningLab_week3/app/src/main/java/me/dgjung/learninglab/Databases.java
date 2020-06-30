package me.dgjung.learninglab;

import android.provider.BaseColumns;

public final class Databases {

    private String id;  /* Auto Increment */
    private int stepCount;
    private int spendTime;
    private Long measureDate;

    private String locationFilePath;
    private String measurementFilePath;
    private String navigationFilePath;
    private String gpsStatusFilePath;
    private String nmeaFilePath;


    public static final class CreateDB implements BaseColumns {
        public static final String STEPCOUNT = "stepCount";
        public static final String SPENDTIME = "spendTime";
        public static final String MESUREDATE = "measureDate";

        public static final String LOCATIONFILEPATH = "locationFilePath";
        public static final String MEASUREMENTFILEPATH = "measurementFilePath";
        public static final String NAVIGATIONFILEPATH = "navigationFilePath";
        public static final String GPSSTATUSFILEPATH = "gpsStatusFilePath";
        public static final String NMEAFILEPATH = "nmeaFilePath";

        public static final String _TABLENAME0 = "measureInfo";
        public static final String _CREATE0 = "create table if not exists " + _TABLENAME0+"("

                + _ID + " integer primary key autoincrement, "
                + STEPCOUNT + " integer not null , "
                + SPENDTIME + " integer not null , "
                + MESUREDATE + " text not null , "
                + LOCATIONFILEPATH + " text not null , "
                + MEASUREMENTFILEPATH + " text not null , "
                + NAVIGATIONFILEPATH + " text not null , "
                + GPSSTATUSFILEPATH + " text not null ,"
                + NMEAFILEPATH + " text not null );";
    }
}
package me.dgjung.gpsmeasure.db;

import android.provider.BaseColumns;

public class DatabaseContract {

    private DatabaseContract() {

    }

    public static class MeasurementsDatabase implements BaseColumns {

        public static final String TABLE_NAME = "Measurements";
        public static final String COL_NAME_1 = "stepcount";
        public static final String COL_NAME_2 = "spendtime";
        public static final String COL_NAME_3 = "measuredate";
        public static final String COL_NAME_4 = "locationfilepath";
        public static final String COL_NAME_5 = "measurementfilepath";
        public static final String COL_NAME_6 = "navigationfilepath";
        public static final String COL_NAME_7 = "gpsstatusfilepath";
        public static final String COL_NAME_8 = "nmeafilepath";
    }
}

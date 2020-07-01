package me.dgjung.dbadapter_template;

import android.provider.BaseColumns;

public final class DatabaseContract {

    private DatabaseContract() {

    }

    public static class UserDatabase implements BaseColumns {

        public static final String TABLE_NAME = "user_details";
        public static final String COLUMN_NAME_COLS1 = "name";
        public static final String COLUMN_NAME_COLS2 = "address";
        public static final String COLUMN_NAME_COLS3 = "phone_no";
        public static final String COLUMN_NAME_COLS4 = "profession";
    }


}

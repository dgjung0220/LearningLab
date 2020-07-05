package me.dgjung.gpsmeasure.service;

import android.Manifest;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static class permissionUtils {

        // Permission check
        public static final String[] REQUIRED_PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        public static final int PERMISSION_REQUEST_CODE = 1;
    }


    public static final String getCurrentTimes() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formatDate = sdfNow.format(date);

        return formatDate;
    }

    public static final String getFilename(String fullPath) {

        String[] filenames = fullPath.split("/");

        return filenames[filenames.length-1];
    }
}

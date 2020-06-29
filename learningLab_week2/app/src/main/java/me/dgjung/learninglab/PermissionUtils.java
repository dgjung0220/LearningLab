package me.dgjung.learninglab;

import android.Manifest;

public class PermissionUtils {

    // Permission check
    public static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static final int PERMISSION_REQUEST_CODE = 1;


}

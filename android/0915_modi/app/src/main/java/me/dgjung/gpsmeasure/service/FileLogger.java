package me.dgjung.gpsmeasure.service;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogger {

    private final String TAG = "FileLogger";
    private final String FILE_PREFIX = "gnss_log";

    /* FILE PREFIX */
    public static final String LOCATION_PREFIX = "location";
    public static final String ACCELERATION_PREFIX= "acceleration";
    public static final String GYROSCOPE_PREFIX = "gyroscope";
    public static final String ORIENTATION_PREFIX = "orientation";

    /* CSV Header PREFIX */
    private final String LOCATION_HEADER = "Provider, UserTime, SysTime, Latitude, Longitude, Altitude, Bearing, BearingAccDegress, Acc, Speed, SpeedAccPerSecond";
    private final String ACCEL_HEADER = "SysTime, ACCEL_X, ACCEL_Y, ACCEL_Z";
    private final String GYRO_HEADER = "SysTime, GYRO_X, GYRO_Y, GYRO_Z";
    private final String ORIENTATION_HEADER = "SysTime, Azimuth(yaw), pitch, roll";

    private final Object mFileLock = new Object();
    private final Context mContext;
    private BufferedWriter mFileWriter;
    private File mFile;

    public FileLogger(Context context) {
        this.mContext = context;
    }

    public void startNewLog(String PREFIX) {

        Log.d(TAG, "start new log");

        synchronized (mFileLock) {
            File baseDirectory;
            String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state)) {
                baseDirectory = new File(Environment.getExternalStorageDirectory(), FILE_PREFIX);
                baseDirectory.mkdirs();
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                Log.d(TAG, "Cannot write to external storage");
                return;
            } else {
                Log.d(TAG, "Cannot read external storage");
                return;
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyy_MM_dd_HH_mm_ss");
            Date now = new Date();
            String fileName = String.format("%s_%s.csv", PREFIX, formatter.format(now));
            File currentFile = new File(baseDirectory, fileName);
            String currentFilePath = currentFile.getAbsolutePath();
            BufferedWriter currentFileWriter;
            try {
                currentFileWriter = new BufferedWriter(new FileWriter(currentFile));
            } catch (IOException e) {
                Log.d(TAG,"Could not open file: " + currentFilePath, e);
                return;
            }

            try {

                switch(PREFIX) {
                    case LOCATION_PREFIX :
                        currentFileWriter.write(LOCATION_HEADER);
                        break;
                    case ACCELERATION_PREFIX :
                        currentFileWriter.write(ACCEL_HEADER);
                        break;
                    case GYROSCOPE_PREFIX:
                        currentFileWriter.write(GYRO_HEADER);
                        break;
                    case ORIENTATION_PREFIX:
                        currentFileWriter.write(ORIENTATION_HEADER);
                    default:
                        break;
                }
                currentFileWriter.newLine();

            } catch(IOException e){
                Log.d(TAG, "Unable to close all file streams.");
                e.printStackTrace();
                return;
            }

            if (mFileWriter != null) {
                try {
                    mFileWriter.close();
                } catch(IOException e) {
                    Log.d(TAG, "Unable to close all file streams.");
                    e.printStackTrace();
                }
            }

            mFile = currentFile;
            mFileWriter = currentFileWriter;
            Toast.makeText(mContext, "File opend: " + currentFilePath, Toast.LENGTH_SHORT).show();
        }
    }

    public void writeLogs(String log) throws IOException {
        if (mFileWriter != null) {
            mFileWriter.write(log);
            mFileWriter.newLine();
        }
    }

    public void send() {

        if (mFile == null) {
            return;
        }

        if (mFileWriter != null) {
            try {
                mFileWriter.flush();
                mFileWriter.close();
                mFileWriter = null;
            } catch (IOException e) {
                Log.d(TAG,"Unable to close all file streams.");
                return;
            }
        }
    }

    public File getMfile() {

        if (mFile == null) {
            return null;
        } else {
            return mFile;
        }
    }
}

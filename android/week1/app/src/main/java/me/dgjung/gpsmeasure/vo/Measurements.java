package me.dgjung.gpsmeasure.vo;

public class Measurements {

    private int _id; /* auto increment */
    private int stepCount;
    private int spendTime;
    private String measureDate;

    private String locationFilePath;
    private String measurementFilePath;
    private String navigationFilePath;
    private String gpsStatusFilePath;
    private String nmeaFilePath;

    public Measurements() {

    }

    public Measurements(int _id, int stepCount, int spendTime, String measureDate, String locationFilePath, String measurementFilePath, String navigationFilePath, String gpsStatusFilePath, String nmeaFilePath) {
        this._id = _id;
        this.stepCount = stepCount;
        this.spendTime = spendTime;
        this.measureDate = measureDate;
        this.locationFilePath = locationFilePath;
        this.measurementFilePath = measurementFilePath;
        this.navigationFilePath = navigationFilePath;
        this.gpsStatusFilePath = gpsStatusFilePath;
        this.nmeaFilePath = nmeaFilePath;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public int getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(int spendTime) {
        this.spendTime = spendTime;
    }

    public String getMeasureDate() {
        return measureDate;
    }

    public void setMeasureDate(String measureDate) {
        this.measureDate = measureDate;
    }

    public String getLocationFilePath() {
        return locationFilePath;
    }

    public void setLocationFilePath(String locationFilePath) {
        this.locationFilePath = locationFilePath;
    }

    public String getMeasurementFilePath() {
        return measurementFilePath;
    }

    public void setMeasurementFilePath(String measurementFilePath) {
        this.measurementFilePath = measurementFilePath;
    }

    public String getNavigationFilePath() {
        return navigationFilePath;
    }

    public void setNavigationFilePath(String navigationFilePath) {
        this.navigationFilePath = navigationFilePath;
    }

    public String getGpsStatusFilePath() {
        return gpsStatusFilePath;
    }

    public void setGpsStatusFilePath(String gpsStatusFilePath) {
        this.gpsStatusFilePath = gpsStatusFilePath;
    }

    public String getNmeaFilePath() {
        return nmeaFilePath;
    }

    public void setNmeaFilePath(String nmeaFilePath) {
        this.nmeaFilePath = nmeaFilePath;
    }
}

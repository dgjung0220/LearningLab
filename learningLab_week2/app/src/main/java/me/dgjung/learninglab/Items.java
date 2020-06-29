package me.dgjung.learninglab;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* POJO */
public class Items {

    private String id;  /* Auto Increment */
    private String day;
    private int stepCount;
    private int spendTime;
    private Long measureDate;

    public Items() {};
    public Items(String id, String day, int stepCount, int spendTime, Long measureDate) {
        this.id = id;
        this.day = day;
        this.stepCount = stepCount;
        this.spendTime = spendTime;
        this.measureDate = measureDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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

    public Long getMeasureDate() {
        return measureDate;
    }

    public void setMeasureDate(Long measureDate) {
        this.measureDate = measureDate;
    }
}

package com.aman.android.rxandroid.model;


public class DateModel implements Comparable<DateModel> {
    private Integer date;
    private Integer value;

    public DateModel(int date, int value) {
        this.date = date;
        this.value = value;
    }

    @Override
    public int compareTo(DateModel o) {
        return value.compareTo(o.value);
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }
}

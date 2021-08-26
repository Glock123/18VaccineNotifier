package com.example.a18vaccinenotifier.datatype;

public class session {
    private String date;
    private int dose1;
    private int dose2;
    private int minAgeLimit;

    private int maxAgeLimit;



    private boolean allowAllAge;

    private String vaccine;
    public session(String date, int dose1, int dose2, boolean allowAllAge, int minAgeLimit, int maxAgeLimit , String vaccine) {
        this.date = date;
        this.dose1 = dose1;
        this.dose2 = dose2;
        this.minAgeLimit = minAgeLimit;
        this.maxAgeLimit = maxAgeLimit;
        this.allowAllAge = allowAllAge;
        this.vaccine = vaccine;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDose1() {
        return dose1;
    }

    public void setDose1(int dose1) {
        this.dose1 = dose1;
    }

    public int getDose2() {
        return dose2;
    }

    public void setDose2(int dose2) {
        this.dose2 = dose2;
    }

    public int getMinAgeLimit() {
        return minAgeLimit;
    }

    public int getMaxAgeLimit() {
        return maxAgeLimit;
    }
    public void setMinAgeLimit(int minAgeLimit) {
        this.minAgeLimit = minAgeLimit;
    }

    public String getVaccine() {
        return vaccine;
    }

    public void setVaccine(String vaccine) {
        this.vaccine = vaccine;
    }

    public boolean isAllowAllAge() {
        return allowAllAge;
    }
}

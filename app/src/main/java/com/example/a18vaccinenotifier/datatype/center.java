package com.example.a18vaccinenotifier.datatype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class center {

    int centerID;
    String name, address, pincode, feeType;
    ArrayList<session> sessions = new ArrayList<>();
    Map<String, String> price = new HashMap<>();

    public center(int centerID, String name, String address, String pincode, String feeType, ArrayList<session> sessions, Map<String, String> price) {
        this.centerID = centerID;
        this.name = name;
        this.address = address;
        this.pincode = pincode;
        this.feeType = feeType;
        this.sessions = sessions;
        this.price = price;
    }

    public int getCenterID() {
        return centerID;
    }

    public void setCenterID(int centerID) {
        this.centerID = centerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public ArrayList<session> getSessions() {
        return sessions;
    }

    public void setSessions(ArrayList<session> sessions) {
        this.sessions = sessions;
    }

    public Map<String, String> getPrice() {
        return price;
    }

    public void setPrice(Map<String, String> price) {
        this.price = price;
    }
}

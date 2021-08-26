package com.example.a18vaccinenotifier.sharedpreference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class SessionManager {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    public static final String SHARED_PREF_NAME = "com.example.18plusvaccinenotifier";
    private static final String HOSPITAL_MAP = "HOSPITAL_LIST_BY_PINCODE2";
    private static final String HOSPITAL_NAME = "HOSPITAL_LIST_BY_HOSPITALID";
    private static java.lang.reflect.Type type, type1;

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        type = new TypeToken<HashMap<String, ArrayList<String>>>(){}.getType();
        type1 = new TypeToken<HashMap<String, String>>(){}.getType();
//        typeHistory = new TypeToken<HashMap<String, ArrayList<>>>(){}.getType();

    }

    public boolean putHospitalInfo(String pincode, String hospID, String hospName) {
        String res = sharedPreferences.getString(HOSPITAL_MAP, null);
        String res1 = sharedPreferences.getString(HOSPITAL_NAME, null);
        Gson gson = new Gson();
        if(res==null) {
            HashMap<String, ArrayList<String>> map = new HashMap<>();
            HashMap<String, String> mapName = new HashMap<>();
            map.put(pincode, new ArrayList<String>(Collections.singletonList(hospID)));
            mapName.put(hospID, hospName);
            String json = gson.toJson(map);
            String jsonName = gson.toJson(mapName);
            editor.putString(HOSPITAL_MAP, json);
            editor.putString(HOSPITAL_NAME, jsonName);
            editor.commit();
            printCurrentHospitals();
            return false;
        }
        HashMap<String, ArrayList<String>> map = gson.fromJson(res, type);
        HashMap<String, String> mapName = gson.fromJson(res1, type1);
        boolean ret;
        mapName.put(hospID, hospName);
        if(map.containsKey(pincode)) {
            Objects.requireNonNull(map.get(pincode)).add(hospID);
            ret=true;
        }
        else {
            map.put(pincode, new ArrayList<>(Collections.singletonList(hospID)));
            ret=false;
        }

        // Removing previous data so that new data can be appended
        editor.remove(HOSPITAL_MAP);
        editor.remove(HOSPITAL_NAME);

        // Creating json strings for new maps
        String json = gson.toJson(map);
        String jsonName = gson.toJson(mapName);

        // Adding them to shared preferences
        editor.putString(HOSPITAL_NAME, jsonName);
        editor.putString(HOSPITAL_MAP, json);

        // Committing the changes
        editor.commit();

        printCurrentHospitals();
        return ret;
    }

    public boolean ifHospitalPresent(String pincode, String hospID) {
        String res = sharedPreferences.getString(HOSPITAL_MAP, null);
        Gson gson = new Gson();
        if(res!=null) {
            HashMap<String, ArrayList<String>> map = gson.fromJson(res, type);
            return map.containsKey(pincode) && Objects.requireNonNull(map.get(pincode)).contains(hospID);
        }
        return false;
    }

    public boolean removeHospitalInfo(String pincode, String hospID) {
        String res = sharedPreferences.getString(HOSPITAL_MAP, null);
        String res1 = sharedPreferences.getString(HOSPITAL_NAME, null);
        if(res!=null) {
            Gson gson = new Gson();
            HashMap<String, ArrayList<String>> map = gson.fromJson(res, type);
            HashMap<String, String> mapName = gson.fromJson(res1, type1);
            if(!map.containsKey(pincode)) return false;
            boolean ifRemoved = Objects.requireNonNull(map.get(pincode)).remove(hospID) & (mapName.remove(hospID)!=null);
            editor.remove(HOSPITAL_MAP);
            editor.remove(HOSPITAL_NAME);

            String json = gson.toJson(map);
            String jsonName = gson.toJson(mapName);

            editor.putString(HOSPITAL_NAME, jsonName);
            editor.putString(HOSPITAL_MAP, json);

            editor.commit();
            printCurrentHospitals();
            return (ifRemoved);
        }
        return false;
    }

    private void printCurrentHospitals() {
        String res1 = sharedPreferences.getString(HOSPITAL_NAME, null);
        Gson gson = new Gson();
        if(res1!=null) {
            HashMap<String, String> mapName = gson.fromJson(res1, type1);
            Iterator it = mapName.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Log.e(String.valueOf(pair.getKey()), String.valueOf(pair.getValue()));
            }
        }
    }

    public HashMap<String, ArrayList<String>> getHospitalByPincode() {
        String res = sharedPreferences.getString(HOSPITAL_MAP, null);
        if(res!=null) {
            Gson gson = new Gson();
            return gson.fromJson(res, type);
        }
        return new HashMap<String, ArrayList<String>>();
    }

    public HashMap<String, String> getHospitalByHospID() {
        String res1 = sharedPreferences.getString(HOSPITAL_NAME, null);
        if(res1!=null) {
            Gson gson = new Gson();
            return gson.fromJson(res1, type1);
        }
        return new HashMap<String, String>();
    }



}

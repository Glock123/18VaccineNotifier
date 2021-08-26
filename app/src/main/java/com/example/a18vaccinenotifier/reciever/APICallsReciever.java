package com.example.a18vaccinenotifier.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a18vaccinenotifier.sharedpreference.SessionManager;
import com.example.a18vaccinenotifier.utils.NotificationHelper;
import com.example.a18vaccinenotifier.utils.VolleySingleton;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class APICallsReciever extends BroadcastReceiver {
    private static long time = System.currentTimeMillis();

    @Override
    public void onReceive(Context context, Intent intent) {

        // Logging data to see burst time
        long temp = (System.currentTimeMillis() - time) / 1000;
        Log.e("Broadcast recieved at", String.valueOf(temp));
        time = System.currentTimeMillis();

        callAPIForFavouriteHospitals(context);
    }

    private void callAPIForFavouriteHospitals(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        HashMap<String, ArrayList<String>> mapByPin = sessionManager.getHospitalByPincode();
        HashMap<String, String> mapByConfig = sessionManager.getHospitalByHospID();

        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);

        if(mapByConfig!=null && mapByPin!=null) {
            Iterator it = mapByPin.entrySet().iterator();
            final int[] count = {0};
            while(it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String pincode = String.valueOf(pair.getKey());
                ArrayList<String> hospList = (ArrayList<String>) pair.getValue();
                Map<String, ArrayList<String>> centerMap = new HashMap<>(); // Stores centerID: config1, config2, ...
                for(String hosp: hospList) {
                    String[] temp = hosp.split("/", 0);
                    if(centerMap.containsKey(temp[0])) {
                        centerMap.get(temp[0]).add(temp[1]);
                    }
                    else {
                        centerMap.put(temp[0], new ArrayList<>(Collections.singletonList(temp[1])));
                    }
                }

                String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=" + pincode + "&date=" + date + "-" + month + "-" + year;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    JSONArray centers = response.getJSONArray("centers");
                                    for(int i=0; i<centers.length(); ++i) {
                                        JSONObject curCenter = centers.getJSONObject(i);
                                        String curCenterID = String.valueOf(curCenter.getInt("center_id"));
                                        if(centerMap.containsKey(curCenterID)) {

                                            /**
                                             * This center is present in centerMap, we can traverse through different sessions
                                             * of this center
                                             */
                                            JSONArray sessions = curCenter.getJSONArray("sessions");
                                            for(int j=0; j<sessions.length(); ++j) {
                                                JSONObject curSession = sessions.getJSONObject(j);
                                                // TODO
                                                /**
                                                 * Now for various configurations for this center specified in centerMap
                                                 * We check if current session matches the criteria i.e.
                                                 * Min_Age_Limit, Dose Number, Vaccine Type
                                                 */

                                                for(String config: centerMap.get(curCenterID)) {
                                                    if(correctAgeGroup(config.charAt(0), curSession) &&
                                                        correctDosePresent(config.charAt(1), curSession.getInt("available_capacity_dose1"), curSession.getInt("available_capacity_dose2")) &&
                                                            correctVaccine(config.charAt(2), curSession.getString("vaccine"))
                                                    ) {
                                                        String title = curCenter.getString("name");
                                                        String ageString = getAgeString(config.charAt(0), curSession);
                                                        String body =
                                                                pincode +
                                                                "\nDate: " + curSession.getString("date") +
                                                                "\nAge Group: " + ageString +
                                                                "\nVaccine Type: " + curSession.getString("vaccine") +
                                                                "\nDose 1 capacity: " + curSession.getInt("available_capacity_dose1") +
                                                                "\nDose 2 capacity: " + curSession.getInt("available_capacity_dose2");
                                                        notificationBuilder(title, body, count[0]++, context);
                                                        /**
                                                         * Add logic to add current vaccine info in memory, can be shown in favorites later
                                                         */
                                                    }
                                                }

                                            }

                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Toast.makeText(context, "Error occurred: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                VolleySingleton.getInstance(context).addToRequestQue(jsonObjectRequest);

            }
        }


    }

    private String getAgeString(char forCheck, JSONObject curSession) throws JSONException {
        if(forCheck=='0') {
            if(curSession.getBoolean("allow_all_age")) {
                return curSession.getInt("min_age_limit") + " & above";
            }
            else if(curSession.getInt("min_age_limit")==18) {
                return "18-" + curSession.getInt("max_age_limit") + " only";
            }
            else return curSession.getInt("min_age_limit") + "+ only";
        }
        else if(forCheck=='1') return "18-44 only";
        else if(forCheck=='2') return "45+ only";
        else return "18 & above";
    }

    private boolean correctAgeGroup(char forCheck, JSONObject curSession) throws JSONException {
        /**
         * forCheck values
         * 0: No config selected, notify for all availability
         * 1: 18-44 age group only
         * 2: 45+ only
         * 3: 18 & above
         */
        if(forCheck=='0') return true;
        // If all age groups are allowed
        if(curSession.getBoolean("allow_all_age")) {
            if(forCheck!='3') return false; // Requirement is not 18 and above
            // Checking if minimum age is 18 (additional check)
            return curSession.getInt("min_age_limit") == 18;
        }

        // Only specific age group is allowed
        return (forCheck == '1' && curSession.getInt("min_age_limit") == 18 && curSession.getInt("max_age_limit") == 44) || (forCheck == '2' && curSession.getInt("min_age_limit") == 45);
    }

    private boolean correctDosePresent(char forCheck, int dose1Amount, int dose2Amount) {
        /**
         * forCheck values
         * 0: No config selected, notify for all availability
         * 1: Dose 1 only
         * 2: Dose 2 only
         */
        return (forCheck == '0' && (dose1Amount > 0 || dose2Amount > 0)) || (forCheck == '1' && dose1Amount > 0) || (forCheck == '2' && dose2Amount > 0);
    }

    private boolean correctVaccine(char forCheck, String vaccine) {
        /**
         * forCheck values
         * 0: No config selected, notify for all availability
         * 1: COVISHIELD only
         * 2: COVAXIN only
         * 3: SPUTNIK V only
         */
        return forCheck=='0' || (forCheck=='1' && (vaccine.compareTo("COVISHIELD")==0)) || (forCheck=='2' && (vaccine.compareTo("COVAXIN")==0)) || (forCheck=='3' && (vaccine.compareTo("SPUTNIK V")==0));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notificationBuilder(String title, String body, int idx, Context context) {
        NotificationHelper notificationHelper = new NotificationHelper(context, title, body, String.valueOf(idx));
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(idx, nb.build());
    }

}

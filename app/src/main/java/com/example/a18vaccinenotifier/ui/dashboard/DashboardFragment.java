package com.example.a18vaccinenotifier.ui.dashboard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a18vaccinenotifier.R;
import com.example.a18vaccinenotifier.adapter.CenterAdapter;
import com.example.a18vaccinenotifier.adapter.HospitalNameAdapter;
//import com.example.a18vaccinenotifier.databinding.FragmentDashboardBinding;
import com.example.a18vaccinenotifier.datatype.center;
import com.example.a18vaccinenotifier.datatype.session;
import com.example.a18vaccinenotifier.reciever.APICallsReciever;
import com.example.a18vaccinenotifier.utils.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DashboardFragment extends Fragment {
    private TextInputEditText pincode;
    private TextInputLayout pincodeLayout;
    private RadioGroup age, vaccine, dose;
    private Button clearAge, clearVaccine, clearDose;
    private RecyclerView centerRV, hospitalNameRV;
    private ArrayList<center> availableCenters;
    private CenterAdapter adapter;
    private HospitalNameAdapter hospitalNameAdapter;
    private JSONArray centers;

    public static String ageGroup, doseNumber, vaccineType, pincodeString;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pincode = view.findViewById(R.id.pincode);
        pincodeLayout = view.findViewById(R.id.pincode_layout);
        age = view.findViewById(R.id.age_group);
        vaccine = view.findViewById(R.id.vaccine_type);
        dose = view.findViewById(R.id.dose_number);
        clearAge = view.findViewById(R.id.age_clear);
        clearVaccine = view.findViewById(R.id.vaccine_clear);
        clearDose = view.findViewById(R.id.dose_clear);
        hospitalNameRV = view.findViewById(R.id.rv_hospital_names);
        centerRV = view.findViewById(R.id.rv_centers);

        availableCenters = new ArrayList<>();
        ageGroup = doseNumber = vaccineType = "0";

        // Initialising the center recycler view
        fillCenterRecyclerView();

        // Initialising the hospital name recycler view
        fillFilterRecyclerView();

        // Start the pending intent for automatic calls to COWIN API
        runAlarmManagerForAutomatingCalls();

        // Setting color of endIcon in pincode layout
        setEndIconColor();

        age.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.above_fourty_five:
                        ageGroup = "2";
                        break;
                    case R.id.eighteen_to_fourty_four:
                        ageGroup="1";
                        break;
                    case R.id.eighteen_and_above:
                        ageGroup="3";
                        break;
                }
                recreateHospitalNameRecyclerView();
            }
        });

        vaccine.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.covaxin:
                        vaccineType = "2";
                        break;
                    case R.id.covishield:
                        vaccineType="1";
                        break;
                    case R.id.sputnik:
                        vaccineType="3";
                        break;
                }
                recreateHospitalNameRecyclerView();
            }
        });

        dose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.dose1:
                        doseNumber = "1";
                        break;
                    case R.id.dose2:
                        doseNumber="2";
                        break;
                }
                recreateHospitalNameRecyclerView();
            }
        });

        clearAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                age.clearCheck();
                ageGroup = "0";
                recreateHospitalNameRecyclerView();
            }
        });

        clearVaccine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaccine.clearCheck();
                vaccineType = "0";
                recreateHospitalNameRecyclerView();
            }
        });

        clearDose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dose.clearCheck();
                doseNumber = "0";
                recreateHospitalNameRecyclerView();
            }
        });

        pincodeLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Closing keyboard upon searching
                closeKeyboard();

                // CLear any previous error in pincode layout
                pincodeLayout.setErrorEnabled(false);

                // Clear out the Center recycler view
                availableCenters.clear();
                adapter.notifyDataSetChanged();

                Calendar calendar = Calendar.getInstance();
                int date = calendar.get(Calendar.DATE);
                int month = calendar.get(Calendar.MONTH)+1;
                int year = calendar.get(Calendar.YEAR);

                //ageGroup = doseNumber = vaccineType = "0";
                availableCenters.clear();
                recreateHospitalNameRecyclerView();

                String pin = Objects.requireNonNull(pincode.getText()).toString();
                if(pin.length()!=6 || (pin.length()>0&&pin.charAt(0)=='0')) {
                    pincodeLayout.setError("Incorrect Pincode");
                    pincodeString="";
                    return;
                }

                pincodeString = pin;
                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Fetching data...Please wait");
                progressDialog.show();

                String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=" + pin + "&date=" + date + "-" + month + "-" + year;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                try {
                                    centers = response.getJSONArray("centers");

                                    if(centers.length()==0) {
                                        Toast.makeText(getContext(), "No centers available currently", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    /**
                                     * Creating list of centers
                                     */
                                    for(int i=0; i<centers.length(); ++i) {
                                        JSONObject curCenter = centers.getJSONObject(i);
                                        ArrayList<session> curSession = new ArrayList<>();
                                        for(int j=0; j<curCenter.getJSONArray("sessions").length(); ++j) {
                                            JSONObject tempSession = curCenter.getJSONArray("sessions").getJSONObject(j);
                                            boolean allowAllAge = tempSession.getBoolean("allow_all_age");
                                            int minAge = tempSession.getInt("min_age_limit");
                                            int maxAge = 200;
                                            if(!allowAllAge && minAge==18) maxAge = tempSession.getInt("max_age_limit");
                                            session temp = new session(tempSession.getString("date"),
                                                    tempSession.getInt("available_capacity_dose1"),
                                                    tempSession.getInt("available_capacity_dose2"),
                                                    allowAllAge,
                                                    minAge,
                                                    maxAge,
                                                    tempSession.getString("vaccine"));
                                            curSession.add(temp);
                                        }
                                        Map<String, String> priceMap = new HashMap<>();
                                        if(curCenter.getString("fee_type").equals("Paid")) {
                                            for(int j=0; j<curCenter.getJSONArray("vaccine_fees").length(); ++j) {
                                                priceMap.put(curCenter.getJSONArray("vaccine_fees").getJSONObject(j).getString("vaccine"),
                                                        curCenter.getJSONArray("vaccine_fees").getJSONObject(j).getString("fee"));
                                            }
                                        }
                                        center tempCenter = new center(
                                                curCenter.getInt("center_id"),
                                                curCenter.getString("name"),
                                                curCenter.getString("address"),
                                                pin,
                                                curCenter.getString("fee_type"),
                                                curSession,
                                                priceMap);

                                        availableCenters.add(tempCenter);
                                    }
                                    // Inflating the recycler view
                                    adapter.notifyDataSetChanged();

                                    // Filling the hospital names for filter
                                    recreateHospitalNameRecyclerView();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.e("VolleyError", error.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Request not completed, try again ", Toast.LENGTH_LONG).show();
                    }
                });
                VolleySingleton.getInstance(getContext()).addToRequestQue(jsonObjectRequest);
            }
        });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void fillFilterRecyclerView() {

        hospitalNameAdapter = new HospitalNameAdapter(getContext(), availableCenters);
        hospitalNameRV.setAdapter(hospitalNameAdapter);
        hospitalNameRV.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void fillCenterRecyclerView() {
        adapter = new CenterAdapter(getContext(), availableCenters);
        centerRV.setAdapter(adapter);
        centerRV.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void recreateHospitalNameRecyclerView() {
        hospitalNameRV.setAdapter(null);
        hospitalNameRV.setLayoutManager(null);
        hospitalNameRV.setAdapter(hospitalNameAdapter);
        hospitalNameRV.setLayoutManager(new LinearLayoutManager(getContext()));
        hospitalNameAdapter.notifyDataSetChanged();
    }

    private void closeKeyboard() {
        View myView = requireActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myView.getWindowToken(), 0);
    }

    private void runAlarmManagerForAutomatingCalls() {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent in = new Intent(requireContext(), APICallsReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(requireContext(), 0, in, 0);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() , 60000, alarmIntent);
    }

    private void setEndIconColor() {
        int nightModeFlags =
                requireContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                pincodeLayout.setEndIconDrawable(R.drawable.ic_baseline_search_night_24);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                pincodeLayout.setEndIconDrawable(R.drawable.ic_baseline_search_24);
                break;
        }
    }
}
package com.example.a18vaccinenotifier.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a18vaccinenotifier.R;
import com.example.a18vaccinenotifier.sharedpreference.SessionManager;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    Context context;
    ArrayList<String> list;
    public FavouriteAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View view = inflator.inflate(R.layout.card_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FavouriteAdapter.ViewHolder holder, int position) {
        String[] now = list.get(position).split("/", 0);
        String pincode = now[0], centerID = now[1], config = now[2], name = now[3];

        String age, dose, vaccine;

        // Getting AGE
        if(config.charAt(0)=='0') {
            age = "All age group";
        }
        else if(config.charAt(0)=='1') {
            age = "Age 18-44 only";
        }
        else if(config.charAt(0)=='2'){
            age = "Age 45+ only";
        }

        else{
            age = "Age 18 & above";
        }


        // Getting DOSE
        if(config.charAt(1)=='0') {
            dose = "Both dose";
        }
        else if(config.charAt(1)=='1') {
            dose = "Dose 1";
        }
        else dose = "Dose 2";

        // Getting VACCINE
        if(config.charAt(2)=='0') {
            vaccine = "All types of vaccines";
        }
        else if(config.charAt(2)=='1') {
            vaccine = "Covishield";
        }
        else if(config.charAt(2)=='2') {
            vaccine = "Covaxin";
        }
        else vaccine = "Sputnik V";

        holder.nameTV.setText(name); // Setting hospital name
        holder.pincodeTV.setText(pincode); // Setting pincode of hospital
        holder.ageTV.setText(age);  // Setting age config
        holder.doseTV.setText(dose); // Setting dose config
        holder.vaccineTV.setText(vaccine); // Setting vaccine config

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Want to delete?");
                alertDialog.setMessage("Can be added anytime again, but availability data is lost");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SessionManager mManager = new SessionManager(context);
                        mManager.removeHospitalInfo(pincode, centerID+"/"+config);
                        notifyItemRemoved(position);
                        list.remove(position);
                        notifyItemRangeChanged(position, list.size());
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV, pincodeTV, doseTV, ageTV, vaccineTV;
        public MaterialButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.fav_hospital_name);
            pincodeTV = itemView.findViewById(R.id.fav_pincode);
            doseTV = itemView.findViewById(R.id.fav_dose_info);
            ageTV = itemView.findViewById(R.id.fav_age_info);
            vaccineTV = itemView.findViewById(R.id.fav_vaccine_info);
            delete = itemView.findViewById(R.id.fav_delete);
        }

    }

}

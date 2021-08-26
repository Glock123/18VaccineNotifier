package com.example.a18vaccinenotifier.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a18vaccinenotifier.R;
import com.example.a18vaccinenotifier.datatype.center;
import com.example.a18vaccinenotifier.sharedpreference.SessionManager;
import com.example.a18vaccinenotifier.ui.dashboard.DashboardFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HospitalNameAdapter extends RecyclerView.Adapter<HospitalNameAdapter.ViewHolder> {

    Context context;
    ArrayList<center> centers;
    public HospitalNameAdapter(Context context, ArrayList<center> centers) {
        this.context = context;
        this.centers = centers;
    }

    @NonNull
    @NotNull
    @Override
    public HospitalNameAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View view =inflator.inflate(R.layout.card_hospital_name, parent, false);
        return new HospitalNameAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HospitalNameAdapter.ViewHolder holder, int position) {
        holder.hospitalName.setText(centers.get(position).getName());

        holder.fav.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_off));
        if(checkIfFavourite(position)) {
            holder.fav.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_on));
            holder.fav.setChecked(true);
        }

        holder.fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    holder.fav.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_on));
                    addToFavourite(position);
                }
                else {
                    holder.fav.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_off));
                    removeFromFavourite(position);
                }
            }
        });

    }

    private boolean checkIfFavourite(int pos) {
        // TODO
        SessionManager mManager = new SessionManager(context);
        String hosp = centers.get(pos).getCenterID() + "/" + DashboardFragment.ageGroup + DashboardFragment.doseNumber + DashboardFragment.vaccineType;
        return mManager.ifHospitalPresent(DashboardFragment.pincodeString, hosp);
    }

    private void addToFavourite(int pos) {
        // TODO
        SessionManager mManager = new SessionManager(context);
        String hosp = centers.get(pos).getCenterID() + "/" + DashboardFragment.ageGroup + DashboardFragment.doseNumber + DashboardFragment.vaccineType;
        boolean recieved = mManager.putHospitalInfo(DashboardFragment.pincodeString, hosp, centers.get(pos).getName());

        Toast.makeText(context, "Added Successfully", Toast.LENGTH_LONG).show();

    }

    private  void removeFromFavourite(int pos) {
        // TODO
        SessionManager mManager = new SessionManager(context);
        String hosp = centers.get(pos).getCenterID() + "/" + DashboardFragment.ageGroup + DashboardFragment.doseNumber + DashboardFragment.vaccineType;
        boolean recieved = mManager.removeHospitalInfo(DashboardFragment.pincodeString, hosp);
        Toast.makeText(context, "Removed Successfully", Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return centers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView hospitalName;
        ToggleButton fav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.filter_hospital_name);
            fav = itemView.findViewById(R.id.filter_toggle);
        }

    }

}

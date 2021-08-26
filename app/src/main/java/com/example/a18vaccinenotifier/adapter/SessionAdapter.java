package com.example.a18vaccinenotifier.adapter;

import android.content.Context;
import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a18vaccinenotifier.R;
import com.example.a18vaccinenotifier.datatype.session;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    Context context;
    ArrayList<session> sessions;
    public SessionAdapter(Context context, ArrayList<session> sessions) {
        this.context = context;
        this.sessions = sessions;
    }

    @NotNull
    @Override
    public SessionAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View view =inflator.inflate(R.layout.card_session, parent, false);
        return new SessionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        holder.date.setText(sessions.get(position).getDate());
        holder.dose1Capacity.setText("Dose 1: " + sessions.get(position).getDose1());
        holder.dose2Capacity.setText("Dose 2: " + sessions.get(position).getDose2());
        holder.vaccineType.setText(sessions.get(position).getVaccine());
        String ageString;
        if(sessions.get(position).isAllowAllAge()) {
            ageString = sessions.get(position).getMinAgeLimit() + " & above";
        }
        else {
            if(sessions.get(position).getMinAgeLimit()==18) ageString = "18-" + sessions.get(position).getMaxAgeLimit() + " only";
            else ageString = sessions.get(position).getMinAgeLimit() + "+ only";
        }

        holder.ageLimit.setText("Age Limit: " + ageString);
        if(sessions.get(position).getDose1() + sessions.get(position).getDose2() > 0)
            holder.session.setCardBackgroundColor(context.getResources().getColor(R.color.light_green));
        else
            holder.session.setCardBackgroundColor(context.getResources().getColor(R.color.light_red));
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView date, dose1Capacity, dose2Capacity, vaccineType, ageLimit;
        CardView session;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.session_date);
            dose1Capacity = itemView.findViewById(R.id.dose_1_capacity);
            dose2Capacity = itemView.findViewById(R.id.dose_2_capacity);
            vaccineType = itemView.findViewById(R.id.vaccine_type);
            ageLimit = itemView.findViewById(R.id.age_limit);
            session = itemView.findViewById(R.id.card_session);
        }

    }
}

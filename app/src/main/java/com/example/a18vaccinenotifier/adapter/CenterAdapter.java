package com.example.a18vaccinenotifier.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a18vaccinenotifier.R;
import com.example.a18vaccinenotifier.datatype.center;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class CenterAdapter extends RecyclerView.Adapter<CenterAdapter.ViewHolder> {

    Context context;
    ArrayList<center> centers;
    public CenterAdapter(Context context, ArrayList<center> centers) {
        this.context = context;
        this.centers = centers;
    }

    @NotNull
    @Override
    public CenterAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View view = inflator.inflate(R.layout.card_center, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CenterAdapter.ViewHolder holder, int position) {
        holder.name.setText(centers.get(position).getName());
        holder.address.setText(centers.get(position).getAddress());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        holder.rvSessions.setLayoutParams(lp);

        // Setting fee type as Paid/Free
        if(centers.get(position).getFeeType().compareTo("Paid")==0) {
            holder.feeType.setText("PAID");
            holder.feeType.setTextColor(context.getResources().getColor(R.color.red));
            StringBuilder feeInfo = new StringBuilder();
            for (Map.Entry<String, String> entry : centers.get(position).getPrice().entrySet()) {
                Map.Entry pair = (Map.Entry) entry;
                String vaccine = String.valueOf(pair.getKey());
                String price = String.valueOf(pair.getValue());
                feeInfo.append(feeInfo.length() == 0 ? "" : "\n").append(vaccine).append(": Rs ").append(price);
            }
            holder.feeInfo.setText(feeInfo.toString());
        } else {
            holder.feeType.setText("FREE");
            holder.feeType.setTextColor(context.getResources().getColor(R.color.green));
            holder.feeInfo.setLayoutParams(lp);
        }

        final int[] isChecked = {0};
        holder.viewHideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked[0] ^= 1;
                if(isChecked[0] == 1) {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    holder.rvSessions.setLayoutParams(lp);
                    holder.viewHideButton.setText("HIDE SESSIONS");
                    holder.viewHideButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                    holder.viewHideButton.setTextColor(ContextCompat.getColor(context, R.color.black));
                    holder.viewHideButton.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_up_24));
                }
                else {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    holder.rvSessions.setLayoutParams(lp);
                    holder.viewHideButton.setText("VIEW SESSIONS");
                    holder.viewHideButton.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                    holder.viewHideButton.setTextColor(ContextCompat.getColor(context, R.color.white));
                    holder.viewHideButton.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_down_24));
                }

            }
        });

        SessionAdapter mAdapter = new SessionAdapter(context, centers.get(position).getSessions());
        holder.rvSessions.setAdapter(mAdapter);
        holder.rvSessions.setLayoutManager(new LinearLayoutManager(context));
        holder.rvSessions.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {
        return centers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, address, feeType, feeInfo;
        MaterialButton viewHideButton;
        RecyclerView rvSessions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.hospital_name);
            address = itemView.findViewById(R.id.hospital_address);
            rvSessions = itemView.findViewById(R.id.rv_sessions);
            viewHideButton = itemView.findViewById(R.id.view_hide_sessions);
            feeType = itemView.findViewById(R.id.fee_type);
            feeInfo = itemView.findViewById(R.id.fee_info);
        }

    }
}

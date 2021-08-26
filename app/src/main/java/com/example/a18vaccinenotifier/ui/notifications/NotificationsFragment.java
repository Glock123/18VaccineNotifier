package com.example.a18vaccinenotifier.ui.notifications;

import androidx.fragment.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a18vaccinenotifier.R;
import com.example.a18vaccinenotifier.adapter.FavouriteAdapter;
import com.example.a18vaccinenotifier.databinding.FragmentNotificationsBinding;
import com.example.a18vaccinenotifier.sharedpreference.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class NotificationsFragment extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv = view.findViewById(R.id.rv_favourite);
        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, ArrayList<String>> map = sessionManager.getHospitalByPincode();
        HashMap<String, String> mapName = sessionManager.getHospitalByHospID();
        ArrayList<String> newList = new ArrayList<>();

        for (Map.Entry<String, ArrayList<String>> stringArrayListEntry : map.entrySet()) {
            Map.Entry pair = (Map.Entry) stringArrayListEntry;
            ArrayList<String> temp = (ArrayList<String>) pair.getValue();
            String pincode = String.valueOf(pair.getKey());
            for (String j : temp) {
                String now = pincode + "/" + j + "/" + mapName.get(j);
                newList.add(now);
            }
        }

        FavouriteAdapter adapter = new FavouriteAdapter(getContext(), newList);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
package com.example.rpac_sports_events.Fragment;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.example.rpac_sports_events.Event.RecSportEvents;
import com.example.rpac_sports_events.Event.RecSportEventsAdapter;
import com.example.rpac_sports_events.Interface.AppBarText;
import com.example.rpac_sports_events.Model.EventViewModel;
import com.example.rpac_sports_events.Interface.MyItemClickListener;
import com.example.rpac_sports_events.R;

/*
* Home fragment displays events scraped from recreational website
*
* Created by Haowen Liu on 02/01/2020
*
* */
public class Home extends Fragment implements AppBarText {
    private static final String TAG = "Checkpoint";
    private RecyclerView sportEvents;
    private RecSportEventsAdapter eventAdapter;
    private ProgressBar pb;
    public static final String EVENT_DETAIL_KEY = "event";
    private EventViewModel em;
    private TextView noEvent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View home = inflater.inflate(R.layout.fragment_home, container, false);
        noEvent = home.findViewById(R.id.home_text);
        Log.d(TAG, "onCreateView() - Home Fragment");
        pb = home.findViewById(R.id.progressbar);
        TextView tv = getActivity().findViewById(R.id.appbar_text);
        setBarText(tv);

        if (isNetworkAvailable()) {
            sportEvents = home.findViewById(R.id.sportEvents);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            sportEvents.setLayoutManager(layoutManager);
            getEvents();
        }else{
            pb.setVisibility(View.GONE);
            noEvent.setVisibility(View.VISIBLE);
            noEvent.setText("No internet connection");
        }


        return home;

    }

    @Override
    public void setBarText(TextView tv){
        tv.setText("Events");
    }

    // adopted from stackoverflow
    // https://stackoverflow.com/questions/57277759/getactivenetworkinfo-is-deprecated-in-api-29
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {
                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_status", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_status", "" + e.getMessage());
                }
            }
        }
        Log.i("update_status", "Network is available : FALSE ");
        return false;
    }

    private void getEvents(){
        em = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        Log.d(TAG, "onCreateView() - Getting events from website");
        em.getEvents().observe(getActivity(), new Observer<ArrayList<RecSportEvents>>() {
            @Override
            public void onChanged(final ArrayList<RecSportEvents> recSportEvents) {
                if (recSportEvents.size() != 0) {
                    eventAdapter = new RecSportEventsAdapter(recSportEvents, new MyItemClickListener() {
                        @Override
                        public void onItemClick(RecSportEvents event) {
                            Bundle bld = new Bundle();
                            String title = event.getTitle();
                            String time = event.getTime();
                            String location = event.getLocation();
                            String des = event.getDescription();
                            String date = event.getDate();
                            String url = event.getUrlDate();
                            bld.putString(EVENT_DETAIL_KEY, title + ">" + time + ">" + location + ">" + des + ">" + date + ">" + url);
                            NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                            navController.navigate(R.id.action_home_to_event_detail, bld);
                        }
                    });
                    Log.d(TAG, "onCreateView() - Events created");
                    sportEvents.setAdapter(eventAdapter);
                    eventAdapter.notifyDataSetChanged();
                } else {
                    noEvent.setVisibility(View.VISIBLE);
                }
                Log.d(TAG, "onCreateView() - Events adapter notified");
                pb.setVisibility(View.GONE);
            }
        });
    }



}


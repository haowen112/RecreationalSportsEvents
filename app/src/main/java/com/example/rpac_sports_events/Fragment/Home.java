package com.example.rpac_sports_events.Fragment;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View home = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d(TAG, "onCreateView() - Home Fragment");
        pb = (ProgressBar)home.findViewById(R.id.progressbar);
        TextView tv = getActivity().findViewById(R.id.appbar_text);
        setBarText(tv);

        if (isNetworkAvailable()) {
            sportEvents = (RecyclerView)home.findViewById(R.id.sportEvents);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            sportEvents.setLayoutManager(layoutManager);
            getEvents();
        }else{
            pb.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "No network connection",
                    Toast.LENGTH_LONG).show();
        }


        return home;

    }

    @Override
    public void setBarText(TextView tv){
        tv.setText("Events");
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }

    private void getEvents(){
        em = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        Log.d(TAG, "onCreateView() - Getting events from website");
        em.getEvents().observe(getActivity(), new Observer<ArrayList<RecSportEvents>>() {
            @Override
            public void onChanged(final ArrayList<RecSportEvents> recSportEvents) {
                if (recSportEvents != null) {
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
                }
                eventAdapter.notifyDataSetChanged();
                Log.d(TAG, "onCreateView() - Events adapter notified");
                pb.setVisibility(View.GONE);
            }
        });
    }



}


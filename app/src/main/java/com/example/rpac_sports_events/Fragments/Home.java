package com.example.rpac_sports_events.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.example.rpac_sports_events.Models.EventViewModel;
import com.example.rpac_sports_events.MyItemClickListener;
import com.example.rpac_sports_events.R;

/*
* Home fragment displays events scraped from recreational website
*
* Created by Haowen Liu on 02/01/2020
*
* */
public class Home extends Fragment {
    private RecyclerView sportEvents;
    private RecSportEventsAdapter eventAdapter;
    private ProgressBar pb;
    public static final String EVENT_DETAIL_KEY = "event";
    private EventViewModel em;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View home = inflater.inflate(R.layout.fragment_home, container, false);
        pb = (ProgressBar)home.findViewById(R.id.progressbar);

        sportEvents = (RecyclerView)home.findViewById(R.id.sportEvents);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        sportEvents.setLayoutManager(layoutManager);
        em = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        em.getEvents().observe(getActivity(), new Observer<ArrayList<RecSportEvents>>() {
            @Override
            public void onChanged(final ArrayList<RecSportEvents> recSportEvents) {
                if(recSportEvents!=null){
                    eventAdapter = new RecSportEventsAdapter(recSportEvents, new MyItemClickListener(){
                        @Override
                        public void onItemClick(RecSportEvents event){
                            Bundle bld = new Bundle();
                            String title = event.getTitle();
                            String time = event.getTime();
                            String location = event.getLocation();
                            String des = event.getDescription();
                            String date = event.getDate();
                            String url = event.getUrlDate();
                            bld.putString(EVENT_DETAIL_KEY, title + ">" + time + ">" + location + ">" + des + ">" + date+">"+url);
                            NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                            navController.navigate(R.id.action_home_to_event_detail,bld);
                        }
                    });
                    sportEvents.setAdapter(eventAdapter);
                }
                eventAdapter.notifyDataSetChanged();
                pb.setVisibility(View.GONE);
            }
        });


        return home;

    }






}


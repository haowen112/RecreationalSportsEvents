package com.example.rpac_sports_events.Fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rpac_sports_events.AppBarText;
import com.example.rpac_sports_events.R;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class Feed extends Fragment implements AppBarText {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_feed, container, false);
        TextView tv = getActivity().findViewById(R.id.appbar_text);
        setBarText(tv);
        return v;
    }

    @Override
    public void setBarText(TextView tv){
        tv.setText("Feeds");
    }


}
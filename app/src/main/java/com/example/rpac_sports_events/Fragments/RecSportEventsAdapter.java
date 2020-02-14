package com.example.rpac_sports_events.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.example.rpac_sports_events.MyItemClickListener;
import com.example.rpac_sports_events.R;

import static java.time.temporal.ChronoUnit.DAYS;

public class RecSportEventsAdapter extends RecyclerView.Adapter<RecSportEventsAdapter.MyViewHolder> {

    ArrayList<RecSportEvents> events;
    MyItemClickListener listener;

    public RecSportEventsAdapter(ArrayList<RecSportEvents> events, MyItemClickListener listener){
        this.events = events;
        this.listener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView eventTitle;
        TextView eventTime;
        TextView eventLocation;
        TextView eventDescription;
        CardView card;

        public MyViewHolder(View itemView){
            super(itemView);
            card = itemView.findViewById(R.id.cardView);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventTime = itemView.findViewById(R.id.eventTime);
            eventLocation = itemView.findViewById(R.id.eventLocation);
            eventDescription = itemView.findViewById(R.id.eventDescription);
        }

        public void bind(final RecSportEvents data, final MyItemClickListener listener){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/M/d");
            LocalDate localDate = LocalDate.now();
            LocalDate url = LocalDate.parse(data.getUrlDate(), dtf);
            if(DAYS.between(localDate, url) == 0){
                card.setCardBackgroundColor(Color.parseColor("#ebebff"));
            }else if(DAYS.between(localDate, url) == 1){
                card.setCardBackgroundColor(Color.parseColor("#c4c4ff"));
            }else{
                card.setCardBackgroundColor(Color.parseColor("#9d9dff"));
            }

            eventTitle.setText(data.getTitle());
            eventTime.setText(data.getTime());
            eventLocation.setText(data.getLocation());
            eventDescription.setText(data.getDescription());
            itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     listener.onItemClick(data);
                 }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_rec_sports, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {

        viewHolder.bind(events.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }


}

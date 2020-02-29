package com.example.rpac_sports_events.Favorite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rpac_sports_events.Interface.FavoriteItemClickListener;
import com.example.rpac_sports_events.R;

import java.util.ArrayList;

public class FavoriteEventsAdapter extends RecyclerView.Adapter<FavoriteEventsAdapter.MyViewHolder> {
    ArrayList<FavoriteEvents> events;
    FavoriteItemClickListener mListener;

    public FavoriteEventsAdapter(ArrayList<FavoriteEvents> events, FavoriteItemClickListener listener){
        this.events = events;
        this.mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView title;

        public MyViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.favoriteEventTitle);
        }

        public void bind(final FavoriteEvents event, final FavoriteItemClickListener listener){
            title.setText(event.getTitle());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(event);
                }
            });
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_event, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {

        viewHolder.bind(events.get(i), mListener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}

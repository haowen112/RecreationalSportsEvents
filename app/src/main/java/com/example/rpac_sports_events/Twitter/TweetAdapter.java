package com.example.rpac_sports_events.Twitter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rpac_sports_events.R;
import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.MyViewHolder>{

    List<Tweet> tweets;

    public TweetAdapter(List<Tweet> tweets){
        this.tweets = tweets;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tweetTime;
        TextView tweetText;
        CardView card;

        public MyViewHolder(View itemView){
            super(itemView);
            card = itemView.findViewById(R.id.feed_cardView);
            tweetTime = itemView.findViewById(R.id.tweetTime);
            tweetText = itemView.findViewById(R.id.tweetText);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tweet, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        viewHolder.tweetTime.setText(tweets.get(i).getTime());
        viewHolder.tweetText.setText(tweets.get(i).getText());
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

}

package com.example.rpac_sports_events.Twitter;

import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpac_sports_events.Event.RecSportEvents;
import com.example.rpac_sports_events.Favorite.FavoriteEvents;
import com.example.rpac_sports_events.Interface.FavoriteItemClickListener;
import com.example.rpac_sports_events.Interface.FeedItemClickListener;
import com.example.rpac_sports_events.Interface.MyItemClickListener;
import com.example.rpac_sports_events.R;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.MyViewHolder>{

    List<Tweet> tweets;
    FeedItemClickListener listener;
    Spanned TEXT = Html.fromHtml("<a href='https://twitter.com/OSURec' style=\"text-decoration: none\" >OhioStateRecSports</a>");

    public TweetAdapter(List<Tweet> tweets, FeedItemClickListener listener) {
        this.tweets = tweets;
        this.listener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tweetTime;
        TextView tweetText;
        TextView tweetName;
        CardView card;

        public MyViewHolder(View itemView){
            super(itemView);
            card = itemView.findViewById(R.id.feed_cardView);
            tweetName = itemView.findViewById(R.id.tweetName);
            tweetTime = itemView.findViewById(R.id.tweetTime);
            tweetText = itemView.findViewById(R.id.tweetText);
        }

        public void bind(final Tweet data, final FeedItemClickListener listener) {
            tweetTime.setText(data.getTime());
            tweetText.setText(data.getText());
            tweetName.setText(TEXT);
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
                .inflate(R.layout.tweet, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
//        viewHolder.tweetName.setMovementMethod(LinkMovementMethod.getInstance());
//        viewHolder.tweetName.setText(text);
//        viewHolder.tweetTime.setText(tweets.get(i).getTime());
//        viewHolder.tweetText.setText(tweets.get(i).getText());
        viewHolder.bind(tweets.get(i), listener);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public String parseText(String str){
        String link;
        String pattern = "https.*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        if(m.find()){
            link = m.group(0);
            str.replace(link,"");
        }else{
            link="";
        }

        return str+link;

    }

}

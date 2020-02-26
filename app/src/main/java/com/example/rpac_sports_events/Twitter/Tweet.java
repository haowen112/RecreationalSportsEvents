package com.example.rpac_sports_events.Twitter;

import com.google.gson.annotations.SerializedName;

public class Tweet {
    @SerializedName("created_at")
    private String time;

    @SerializedName("text")
    private String text;

    public Tweet(String time, String text){
        this.time = time;
        this.text = text;
    }

    public String getTime(){
        return time;
    }

    public String getText(){
        return text;
    }
}

package com.example.rpac_sports_events.Twitter;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;

public class Tweet {
    @SerializedName("created_at")
    private String time;

    @SerializedName("text")
    private String text;

    @SerializedName("id")
    private String id;


    public Tweet(String time, String text, String id) {
        this.time = time;
        this.text = text;
        this.id = id;
    }

    public String getTime(){
        String[] timegroup = time.split(" ");

        return timegroup[0]+" "+timegroup[1]+" "+timegroup[2]+" "+timegroup[3];
    }

    public String getText(){
        return text;
    }

    public String getId() {
        return id;
    }
}

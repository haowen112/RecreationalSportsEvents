package com.example.rpac_sports_events.Interface;

import com.example.rpac_sports_events.Twitter.Tweet;
import com.example.rpac_sports_events.Twitter.TwitterToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GetTweet {

    //get twitter timeline
    @GET("1.1/statuses/user_timeline.json?screen_name=osurec&count=20")
    Call<List<Tweet>> getTimeLine();

    //get auth token
    @POST("oauth2/token")
    Call<TwitterToken> getToken();
}

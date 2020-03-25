package com.example.rpac_sports_events.Model;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rpac_sports_events.Interface.GetTweet;
import com.example.rpac_sports_events.Twitter.Tweet;
import com.example.rpac_sports_events.Twitter.TwitterApiClient;
import com.example.rpac_sports_events.Twitter.TwitterToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TwitterViewModel extends AndroidViewModel {
    private MutableLiveData<List<Tweet>> tweet = new MutableLiveData<>();
    private GetTweet api_call;
    private List<Tweet> timelineList;
    private GetTweet token_service;
    private TwitterToken token;

    // Constructor
    public TwitterViewModel(Application app) {
        super(app);
        FetchFeeds();
    }

    public LiveData<List<Tweet>> getTweets() {
        return tweet;
    }

    private void FetchFeeds() {
        token_service = TwitterApiClient.getTwitterApiToken().create(GetTweet.class);
        Call<TwitterToken> call = token_service.getToken();
        call.enqueue(new Callback<TwitterToken>() {
            @Override
            public void onResponse(Call<TwitterToken> call, Response<TwitterToken> response) {
                if (response.isSuccessful()) {
                    Log.d("Test", "Get token successful ");
                    token = response.body();
                    getTimeLine();
                } else {
                    Log.d("Test", "Get token failed ");
                }
            }

            @Override
            public void onFailure(Call<TwitterToken> call, Throwable t) {
                Log.d("Test", "Get token failed");
            }
        });
        Log.d(TAG, "OnCreate() - get twitter api token");
    }

    public void getTimeLine() {
        api_call = TwitterApiClient.getClient(token.getToken()).create(GetTweet.class);
        Call<List<Tweet>> call = api_call.getTimeLine();

        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                if (response.isSuccessful()) {
                    timelineList = response.body();
                    tweet.setValue(timelineList);

                } else {
                    Log.d("Test", response.code() + " ");
                }
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                return;
            }
        });
        Log.d(TAG, "OnCreateView() - get time line successful");
    }

}

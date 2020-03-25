package com.example.rpac_sports_events.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.GetChars;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rpac_sports_events.Interface.AppBarText;
import com.example.rpac_sports_events.Interface.FavoriteItemClickListener;
import com.example.rpac_sports_events.Interface.FeedItemClickListener;
import com.example.rpac_sports_events.Interface.GetTweet;
import com.example.rpac_sports_events.MainActivity;
import com.example.rpac_sports_events.Model.TwitterViewModel;
import com.example.rpac_sports_events.R;
import com.example.rpac_sports_events.Twitter.Tweet;
import com.example.rpac_sports_events.Twitter.TweetAdapter;
import com.example.rpac_sports_events.Twitter.TwitterApiClient;
import com.example.rpac_sports_events.Twitter.TwitterToken;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Feed extends Fragment implements AppBarText {
    private static final String TAG = "Check";
    private RecyclerView tweetsView;
    private TweetAdapter tweetAdapter;
    private TwitterViewModel tweets;
    private TextView noEvent;


    @Override
    public void onCreate(Bundle instance) {
        super.onCreate(instance);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "OnCreateView() - Feed fragment view created");
        View v =  inflater.inflate(R.layout.fragment_feed, container, false);
        TextView tv = getActivity().findViewById(R.id.appbar_text);
        noEvent = v.findViewById(R.id.feed_text);

        setBarText(tv);
        tweetsView = v.findViewById(R.id.tweets);

        if(isNetworkAvailable()){
//            token_service = TwitterApiClient.getTwitterApiToken().create(GetTweet.class);
            getToken();
        }else{
            noEvent.setVisibility(View.VISIBLE);
            noEvent.setText("No internet connection");
        }
        return v;
    }

    @Override
    public void setBarText(TextView tv){
        tv.setText("Feeds");
    }


//    public void getTimeLine(){
//
//        MainActivity activity = (MainActivity) getActivity();
//        api_call = TwitterApiClient.getClient(token.getToken()).create(GetTweet.class);
//        Call<List<Tweet>> call = api_call.getTimeLine();
//
//        call.enqueue(new Callback<List<Tweet>>() {
//            @Override
//            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
//                if(response.isSuccessful()){
//                    timelineList = response.body();
//                    tweetAdapter = new TweetAdapter(timelineList, new FeedItemClickListener() {
//                        @Override
//                        public void onItemClick(Tweet tweet) {
//                            Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.twitter.com/osurec/status/" + tweet.getId()));
//                            startActivity(intent);
//                        }
//                    });
//                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//                    tweetsView.setLayoutManager(layoutManager);
//                    tweetsView.setAdapter(tweetAdapter);
//
//                }else{
//                    Log.d("Test", response.code()+" ");
//                    Toast.makeText(getActivity(), "get timeline failed",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Tweet>> call, Throwable t) {
//                Toast.makeText(getActivity(), "Something went wrong: "+t.getCause(),
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//        Log.d(TAG, "OnCreateView() - get time line successful");
//    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {
                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_status", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_status", "" + e.getMessage());
                }
            }
        }
        Log.i("update_status", "Network is available : FALSE ");
        return false;
    }

    public void getToken() {
//        Call<TwitterToken> call = token_service.getToken();
//        call.enqueue(new Callback<TwitterToken>() {
//            @Override
//            public void onResponse(Call<TwitterToken> call, Response<TwitterToken> response) {
//                if (response.isSuccessful()) {
//                    Log.d("Test", "Get token successful ");
//                    token = response.body();
//                    getTimeLine();
//
//                } else {
//                    Log.d("Test", "Get token failed ");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<TwitterToken> call, Throwable t) {
//                Log.d("Test", "Get token failed");
//            }
//        });
//        Log.d(TAG, "OnCreate() - get twitter api token");

        tweets = new ViewModelProvider(requireActivity()).get(TwitterViewModel.class);
        tweets.getTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                if (tweets.size() != 0) {
                    tweetAdapter = new TweetAdapter(tweets, new FeedItemClickListener() {
                        @Override
                        public void onItemClick(Tweet tweet) {
                            Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.twitter.com/osurec/status/" + tweet.getId()));
                            startActivity(intent);
                        }
                    });
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    tweetsView.setLayoutManager(layoutManager);
                    tweetsView.setAdapter(tweetAdapter);
                } else {
                    noEvent.setVisibility(View.VISIBLE);
                }
            }
        });


    }
}
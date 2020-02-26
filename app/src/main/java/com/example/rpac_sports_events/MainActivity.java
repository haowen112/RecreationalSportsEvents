package com.example.rpac_sports_events;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rpac_sports_events.Interface.AppBarText;
import com.example.rpac_sports_events.Interface.GetTweet;
import com.example.rpac_sports_events.Twitter.TwitterApiClient;
import com.example.rpac_sports_events.Twitter.TwitterToken;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
* Main activity class
*
* Created by Haowen Liu on 02/01/2020
* */
public class MainActivity extends AppCompatActivity implements AppBarText {
    private static final String TAG = "Checkpoint";
    BottomNavigationView bottomNavigationView;
    private static GetTweet token_service;
    private TwitterToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate() - main activity created");
        setUpNavigation();
        token_service = TwitterApiClient.getTwitterApiToken().create(GetTweet.class);
        getToken();

    }

    // Setting up navigation architecture on bottom navigation bar
    public void setUpNavigation(){
        bottomNavigationView =findViewById(R.id.bottom_navigation_view);
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager()
                .findFragmentById(R.id.navigation_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,
                 navHostFragment.getNavController());
        Log.d(TAG, "OnCreate() - setUpNavigation");
    }

    @Override
    public void setBarText(TextView tv){
        tv.setText("Events");
    }

    public void getToken() {

        Call<TwitterToken> call = token_service.getToken();
        call.enqueue(new Callback<TwitterToken>() {
            @Override
            public void onResponse(Call<TwitterToken> call, Response<TwitterToken> response) {
                if(response.isSuccessful()){
                    Log.d("Test", "Get token successful ");
                    token = response.body();

                }else{
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

    public String returnToken(){
        return token.getToken();
    }


}

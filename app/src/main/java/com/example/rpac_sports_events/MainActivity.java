package com.example.rpac_sports_events;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

/*
* Main activity class
*
* Created by Haowen Liu on 02/01/2020
* */
public class MainActivity extends AppCompatActivity implements AppBarText {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpNavigation();

    }

    // Setting up navigation architecture on bottom navigation bar
    public void setUpNavigation(){
        bottomNavigationView =findViewById(R.id.bottom_navigation_view);
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager()
                .findFragmentById(R.id.navigation_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,
                 navHostFragment.getNavController());
    }

    @Override
    public void setBarText(TextView tv){
        tv.setText("Events");
    }



}

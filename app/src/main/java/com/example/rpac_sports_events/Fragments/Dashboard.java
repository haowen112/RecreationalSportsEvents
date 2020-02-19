package com.example.rpac_sports_events.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.rpac_sports_events.AppBarText;
import com.example.rpac_sports_events.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends Fragment implements AppBarText {

    private FirebaseUser user;
    private TextView dashboard_email;
    private TextView dashboard_username;
    private TextView dashboard_name;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                navController.navigate(R.id.action_dashboard_to_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dashboard_page =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        Button edit_profile = (Button) dashboard_page.findViewById(R.id.dashboard_edit_profile_button);
        Button edit_password = (Button) dashboard_page.findViewById(R.id.dashboard_edit_password_button);
        Button sign_out = (Button) dashboard_page.findViewById(R.id.dashboard_sign_out_button);

        TextView tv = getActivity().findViewById(R.id.appbar_text);
        setBarText(tv);

        edit_profile.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.edit_profile, null));
        edit_password.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.change_password, null));
        sign_out.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                        navController.navigate(R.id.action_dashboard_to_login);
                    }
                }
        );

        dashboard_email = dashboard_page.findViewById(R.id.dashboard_email);
        dashboard_username = dashboard_page.findViewById(R.id.dashboard_username);
        dashboard_name = dashboard_page.findViewById(R.id.dashboard_display_username);



        if(user!=null){
            user.reload();
            String name = user.getDisplayName();
            String email = user.getEmail();

            String temp = "Please setup your username";
            if(name==null){
                dashboard_username.setText(temp);
            }else {
                dashboard_name.setText(name);
                dashboard_username.setText(name);
            }
            dashboard_email.setText(email);
        }



        return dashboard_page;
    }


    @Override
    public void setBarText(TextView tv){
        tv.setText("For you");
    }

}
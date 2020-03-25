package com.example.rpac_sports_events.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.rpac_sports_events.Interface.AppBarText;
import com.example.rpac_sports_events.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class EditProfile extends Fragment implements AppBarText {

    private FirebaseUser user;
    private TextInputEditText edit_username;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                navController.navigate(R.id.action_edit_profile_to_dashboard);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View edit_profile_page =  inflater.inflate(R.layout.fragment_edit_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Button submit = edit_profile_page.findViewById(R.id.profile_submit_button);
        TextView tv = getActivity().findViewById(R.id.appbar_text);
        setBarText(tv);

        edit_username = edit_profile_page.findViewById(R.id.edit_profile_username);

        submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edit_username.length() > 0) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(edit_username.getText().toString().trim())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                                Toast.makeText(getActivity(), "Username updated. You may need to sign-in again to " +
                                                                "see the change",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                            navController.navigate(R.id.action_edit_profile_to_dashboard);

                        }else{
                            Toast.makeText(getActivity(), "Invalid username",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return edit_profile_page;

    }

    @Override
    public void setBarText(TextView tv) {
        tv.setText("Edit Username");
    }
}
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

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ChangePassword extends Fragment implements AppBarText {

    private FirebaseUser user;
    private TextInputEditText pass;
    private TextInputEditText cpass;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                navController.navigate(R.id.action_change_password_to_dashboard);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View change_password_page =  inflater.inflate(R.layout.fragment_change_password, container, false);

        Button submit = change_password_page.findViewById(R.id.password_submit_button);

        pass = change_password_page.findViewById(R.id.change_password_text);
        cpass = change_password_page.findViewById(R.id.change_password2_text);
        TextView tv = getActivity().findViewById(R.id.appbar_text);
        setBarText(tv);
        user = FirebaseAuth.getInstance().getCurrentUser();

        submit.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        if(pass.length() >= 6 && cpass.length() >= 0){
                            String password = pass.getText().toString();
                            String cpassword = cpass.getText().toString();
                            if(password.equals(cpassword)){
                                user.updatePassword(password)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User password updated.");
                                                    Toast.makeText(getActivity(), R.string.pass_updated,
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                FirebaseAuth.getInstance().signOut();
                                NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                                navController.navigate(R.id.action_change_password_to_login);
                            }else{
                                Toast.makeText(getActivity(), R.string.pass_no_match,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getActivity(), R.string.choose_pass,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );
        return change_password_page;
    }

    @Override
    public void setBarText(TextView tv) {
        tv.setText(R.string.change_password);
    }
}
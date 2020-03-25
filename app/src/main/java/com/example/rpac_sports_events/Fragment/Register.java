package com.example.rpac_sports_events.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Register extends Fragment implements AppBarText {

    private TextInputEditText register_email;
    private TextInputEditText register_password;
    private TextInputEditText register_password2;

    private FirebaseAuth mAuth;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                navController.navigate(R.id.action_register_to_login2);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View register_page = inflater.inflate(R.layout.fragment_register, container, false);

        Button register = register_page.findViewById(R.id.register_submit_button);
        TextView tv = getActivity().findViewById(R.id.appbar_text);
        setBarText(tv);
        register_email = register_page.findViewById(R.id.register_email);
        register_password = register_page.findViewById(R.id.register_password);
        register_password2 = register_page.findViewById(R.id.register_password2);


        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isNetworkAvailable()) {
                            if (register_email.length() <= 0 || register_password.length() <= 0 || register_password2.length() <= 0) {
                                Toast.makeText(getActivity(), "Please enter your credentials",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                String email = register_email.getText().toString().trim();
                                String pass = register_password.getText().toString();
                                String cpass = register_password2.getText().toString();

                                if (email.matches(emailPattern)) {
                                    if (pass.equals(cpass)) {
                                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
                                                new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getActivity(), "Register Successfully", Toast.LENGTH_SHORT).show();
                                                            Log.d(TAG, "createUserWithEmail:success");
                                                            NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                                                            navController.navigate(R.id.action_register_to_login2);
                                                        } else {
                                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                        );
                                    } else {
                                        Toast.makeText(getActivity(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Email entered is wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else{
                            Toast.makeText(getActivity(), "No network connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        return register_page;
    }

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

    @Override
    public void setBarText(TextView tv) {
        tv.setText("Register");
    }
}
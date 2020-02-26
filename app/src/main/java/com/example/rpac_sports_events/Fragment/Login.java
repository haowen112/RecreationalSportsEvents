package com.example.rpac_sports_events.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rpac_sports_events.Interface.AppBarText;
import com.example.rpac_sports_events.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class Login extends Fragment implements AppBarText {
    private static final String TAG = "Checkpoint";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextInputEditText login_email;
    private TextInputEditText login_password;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Login fragment created");

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "Getting current user");


        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                navController.navigate(R.id.action_login_to_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        if(user!=null){
            Log.d(TAG, "user signed in");
            NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
            navController.navigate(R.id.action_login_to_dashboard);
        }



    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View login_page =  inflater.inflate(R.layout.fragment_login, container, false);
        Log.d(TAG, "Login fragment view created");

        Button login = (Button) login_page.findViewById(R.id.login_button);
        Button register = (Button) login_page.findViewById(R.id.register_button);
        Button forget_password = (Button)login_page.findViewById(R.id.forget_password_button);

        login_email = login_page.findViewById(R.id.login_email);
        login_password = login_page.findViewById(R.id.login_password);

        TextView tv = getActivity().findViewById(R.id.appbar_text);
        setBarText(tv);

        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isNetworkAvailable()) {
                            if (login_email.length() <= 0 || login_password.length() <= 0) {
                                Toast.makeText(getActivity(), "Please enter your credentials",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                String email = login_email.getText().toString().trim();
                                String pass = login_password.getText().toString().trim();
                                mAuth.signInWithEmailAndPassword(email, pass)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    Log.d(TAG, "signInWithEmail:success");
                                                    Toast.makeText(getActivity(), "Welcome Back!",
                                                            Toast.LENGTH_LONG).show();
                                                    NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                                                    navController.navigate(R.id.action_login_to_dashboard);
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                    Toast.makeText(getActivity(), "Email or password wrong",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        }else{
                            Toast.makeText(getActivity(), "No network connection",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
        });

        register.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.register, null));
        forget_password.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.forget_password, null));
        return login_page;
    }

    @Override
    public void setBarText(TextView tv){
        tv.setText("Login");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }
}
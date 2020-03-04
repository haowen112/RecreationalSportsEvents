package com.example.rpac_sports_events.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.rpac_sports_events.Interface.AppBarText;
import com.example.rpac_sports_events.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends Fragment implements AppBarText {

    private FirebaseAuth auth;
    private TextInputEditText email;
    private Button submit;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @Override
    public void onCreate(Bundle instance){
        super.onCreate(instance);
        auth = FirebaseAuth.getInstance();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_forget_password,container, false);
        email = v.findViewById(R.id.forget_password_email);
        submit = v.findViewById(R.id.forget_password_submit);
        TextView tv = getActivity().findViewById(R.id.appbar_text);
        setBarText(tv);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.length()<=0){
                    Toast.makeText(getActivity(), "Please enter your email correctly",
                            Toast.LENGTH_SHORT).show();
                }else{
                    String email_entered = email.getText().toString();
                    if(email_entered.matches(emailPattern)){
                        auth.sendPasswordResetEmail(email_entered)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Email sent",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else{
                        Toast.makeText(getActivity(), "Please enter your email correctly",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return v;

    }

    @Override
    public void setBarText(TextView tv) {
        tv.setText("Reset Password");
    }
}
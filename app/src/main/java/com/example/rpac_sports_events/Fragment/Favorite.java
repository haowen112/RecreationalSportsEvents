package com.example.rpac_sports_events.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rpac_sports_events.Favorite.FavoriteEvents;
import com.example.rpac_sports_events.Favorite.FavoriteEventsAdapter;
import com.example.rpac_sports_events.Interface.AppBarText;
import com.example.rpac_sports_events.Interface.FavoriteItemClickListener;
import com.example.rpac_sports_events.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Favorite extends Fragment implements AppBarText {

    private FirebaseUser user;
    private Button signin;
    private DatabaseReference myRef;
    private ArrayList<FavoriteEvents> events;
    private FavoriteEventsAdapter adapter;
    private FavoriteItemClickListener mListener;
    private RecyclerView favorites;
    private TextView noFavorite;
    private Dialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        if(user == null){
            v = inflater.inflate(R.layout.fragment_favorite_not_signed_in, container, false);
            signin = v.findViewById(R.id.favorite_login_button);
            signin.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                            navController.navigate(R.id.action_favorite_to_login);
                        }
                    }
            );
        }else{
            v =  inflater.inflate(R.layout.fragment_favorite, container, false);
            favorites = v.findViewById(R.id.favoriteEvents);
            noFavorite = v.findViewById(R.id.favorite_text);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            favorites.setLayoutManager(layoutManager);
            events = new ArrayList<>();
            myRef.child("user").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        noFavorite.setVisibility(View.GONE);
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            events.add(new FavoriteEvents(data.getValue().toString()));
                        }
                        adapter = new FavoriteEventsAdapter(events, new FavoriteItemClickListener() {
                            @Override
                            public void onItemClick(FavoriteEvents event) {
                                alertBuilder(event.getTitle());
                            }
                        });
                        favorites.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        TextView tv = getActivity().findViewById(R.id.appbar_text);
        setBarText(tv);
        return v;

    }

    @Override
    public void setBarText(TextView tv){
        tv.setText("Favorites");
    }

    public void alertBuilder(String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert");
        builder.setMessage("Do you wish to delete this event?");

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        myRef.child("user").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot data: dataSnapshot.getChildren()){
                                    if(data.getValue().equals(title)){
                                        myRef.child("user").child(user.getUid()).child(data.getKey()).removeValue();
                                        Toast.makeText(getActivity(), "Event deleted",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        builder.setPositiveButton("Yes", dialogClickListener);
        builder.setNegativeButton("No",dialogClickListener);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}

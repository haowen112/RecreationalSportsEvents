package com.example.rpac_sports_events.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rpac_sports_events.Calendar.CalendarPermissionUtil;
import com.example.rpac_sports_events.Calendar.CalendarReminderUtils;
import com.example.rpac_sports_events.Favorite.FavoriteEvents;
import com.example.rpac_sports_events.Favorite.FavoriteEventsAdapter;
import com.example.rpac_sports_events.Favorite.FavoriteEventsScheduleAdapter;
import com.example.rpac_sports_events.Interface.AppBarText;
import com.example.rpac_sports_events.Interface.FavoriteItemClickListener;
import com.example.rpac_sports_events.Model.EventScheduleViewModel;
import com.example.rpac_sports_events.Model.MyViewModelFactory;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Favorite extends Fragment implements AppBarText {

    private FirebaseUser user;
    private Button signin;
    private DatabaseReference myRef;
    private ArrayList<FavoriteEvents> events;
    private FavoriteEventsAdapter adapter;
    private RecyclerView favorites;
    private TextView noFavorite;
    private ProgressBar pb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        TextView tv = getActivity().findViewById(R.id.appbar_text);

        if (user == null) {
            v = inflater.inflate(R.layout.fragment_favorite_not_signed_in, container, false);
            signin = v.findViewById(R.id.favorite_login_button);
            signin.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
//                            navController.navigate(R.id.action_favorite_to_login);
                            Navigation.findNavController(v).navigate(R.id.login);
                        }
                    }
            );
        } else {
            v = inflater.inflate(R.layout.fragment_favorite, container, false);
            pb = v.findViewById(R.id.progressbar2);
            pb.setVisibility(View.GONE);

            if (isNetworkAvailable()) {
                favorites = v.findViewById(R.id.favoriteEvents);
                noFavorite = v.findViewById(R.id.favorite_text);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                favorites.setLayoutManager(layoutManager);
                events = new ArrayList<>();
                myRef.child("user").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            noFavorite.setVisibility(View.GONE);
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                events.add(new FavoriteEvents(data.getValue().toString()));
                            }
                            adapter = new FavoriteEventsAdapter(events, new FavoriteItemClickListener() {
                                @Override
                                public void onItemClick(FavoriteEvents event) {
                                    alertBuilder(event.getTitle());
                                }
                            });
                            favorites.setAdapter(adapter);
                        } else {
                            noFavorite.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                noFavorite = v.findViewById(R.id.favorite_text);
                noFavorite.setText(R.string.no_network);
                noFavorite.setVisibility(View.VISIBLE);

            }
        }

        if (tv != null) {
            setBarText(tv);
        }
        return v;

    }

    @Override
    public void setBarText(TextView tv) {
        tv.setText(R.string.favorites);
    }


    public void alertBuilder(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(R.string.select_option);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        myRef.child("user").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    if (data.getValue().equals(title)) {
                                        myRef.child("user").child(user.getUid()).child(data.getKey()).removeValue();
                                        Toast.makeText(getActivity(), R.string.event_delete,
                                                Toast.LENGTH_SHORT).show();
                                        NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
                                        navController.navigate(R.id.action_favorite_self);
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


                    case DialogInterface.BUTTON_NEUTRAL:
                        switchLayout(title);
                        break;


                }
            }
        };
        builder.setNeutralButton(R.string.get_schedule, dialogClickListener);
        builder.setPositiveButton(R.string.delete, dialogClickListener);
        builder.setNegativeButton(R.string.cancel, dialogClickListener);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void switchLayout(String title) {
        Bundle bld = new Bundle();
        bld.putString("EVENT_TITLE", title);
        NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
        navController.navigate(R.id.action_favorite_to_favorite_schedule, bld);
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
}

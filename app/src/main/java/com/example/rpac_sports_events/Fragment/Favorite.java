package com.example.rpac_sports_events.Fragment;

import android.Manifest;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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
import com.example.rpac_sports_events.Model.EventViewModel;
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
    private FavoriteEventsScheduleAdapter scheduleAdapter;
    private FavoriteItemClickListener mListener;
    private RecyclerView favorites;
    private RecyclerView schedule;
    private TextView noFavorite;
    private Dialog dialog;
    private ProgressBar pb;
    private EventScheduleViewModel em;
    private FavoriteEvents temp;

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
        TextView tv = getActivity().findViewById(R.id.appbar_text);

        if (user == null) {
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
        } else {
            v = inflater.inflate(R.layout.fragment_favorite, container, false);
            pb = v.findViewById(R.id.progressbar2);
            if (getArguments() == null) {
                pb.setVisibility(View.GONE);
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
                schedule = v.findViewById(R.id.favoriteEvents);
                noFavorite = v.findViewById(R.id.favorite_text);
                if (isNetworkAvailable()) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    schedule.setLayoutManager(layoutManager);
                    getSchedule(getArguments().getString("EVENT_TITLE"));
                } else {
                    pb.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "No network connection",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }
        setBarText(tv);
        return v;

    }

    @Override
    public void setBarText(TextView tv){
        tv.setText("Favorites");
    }


    public void alertBuilder(String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage("Please select an option. ");

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
        builder.setNeutralButton("Get Schedule", dialogClickListener);
        builder.setPositiveButton("Delete", dialogClickListener);
        builder.setNegativeButton("Cancel", dialogClickListener);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void switchLayout(String title) {
        Bundle bld = new Bundle();
        bld.putString("EVENT_TITLE", title);
        NavController navController = Navigation.findNavController(getActivity(), R.id.navigation_host_fragment);
        navController.navigate(R.id.action_favorite_self, bld);
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

    public void getSchedule(String title) {
//      em = new ViewModelProvider(requireActivity(), new MyViewModelFactory(getActivity().getApplication(), title)).get(EventScheduleViewModel.class);
        em = new ViewModelProvider(requireActivity(), new MyViewModelFactory(getActivity().getApplication(), "Cardio Barbell")).get(EventScheduleViewModel.class);
        em.getSchedule().observe(getActivity(), new Observer<ArrayList<FavoriteEvents>>() {
            @Override
            public void onChanged(ArrayList<FavoriteEvents> favoriteEvents) {
                if (favoriteEvents != null) {
                    scheduleAdapter = new FavoriteEventsScheduleAdapter(favoriteEvents, new FavoriteItemClickListener() {
                        @Override
                        public void onItemClick(FavoriteEvents event) {
                            temp = event;
                            alertScheduleBuilder(event);
                        }
                    });
                    schedule.setAdapter(scheduleAdapter);
                    pb.setVisibility(View.GONE);
                } else {
                    noFavorite.setVisibility(View.VISIBLE);
                    noFavorite.setText("No events scheduled in 3 days");
                }
            }
        });

    }

    public void alertScheduleBuilder(FavoriteEvents event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(event.getTitle());
        builder.setMessage("Please select an option. ");

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        fetchPermission(99);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        builder.setPositiveButton("Add to calendar", dialogClickListener);
        builder.setNegativeButton("Cancel", dialogClickListener);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void fetchPermission(int requestCode) {
        int selfPermission;

        try {
            selfPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (selfPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR}, requestCode);
        } else {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {

                    if (!CalendarReminderUtils.isNoCursor(getActivity())) {
                        addCalender();
                        e.onNext(true);
                    } else {
                        e.onNext(false);
                    }

                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean saveResult) throws Exception {
                            if (!saveResult) {
                                CalendarPermissionUtil.showWaringDialog(getActivity());
                            }
                        }
                    });
        }
    }

    private void addCalender() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                CalendarReminderUtils.deleteCalendarEvent(getActivity(), temp.getTitle());
                String[] date = temp.getUrlDate().split("/");
                String[] time = temp.getTime().split(" ");
                String[] startT = time[0].split(":");
                String[] endT = time[3].split(":");
                String ampm = time[1];

                long startTime = getMsFromDayTime(
                        Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1,
                        Integer.parseInt(date[2]), Integer.parseInt(startT[0]),
                        Integer.parseInt(startT[1]));
                long endTime = getMsFromDayTime(
                        Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1,
                        Integer.parseInt(date[2]), Integer.parseInt(endT[0]),
                        Integer.parseInt(endT[1]));
                CalendarReminderUtils.addCalendarEvent(
                        getActivity(), temp.getTitle(), temp.getDescription(), startTime, endTime, temp.getLocation(), ampm
                );

                e.onNext(!CalendarReminderUtils.isNoCalendarData(getActivity().getApplicationContext(), temp.getTitle()));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean saveResult) throws Exception {
                        if (saveResult) {
                            Toast.makeText(getActivity().getApplicationContext(), "Add to calendar successful", Toast.LENGTH_SHORT).show();
                        } else {
                            CalendarPermissionUtil.showWaringDialog(getActivity());
                        }
                    }
                });
    }

    public static long getMsFromDayTime(int year, int month, int day, int hour, int minute) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.YEAR, year);
        cal.set(java.util.Calendar.MONTH, month);
        cal.set(java.util.Calendar.DAY_OF_MONTH, day);
        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MINUTE, minute);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 99) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                        if (!CalendarReminderUtils.isNoCursor(getActivity())) {
                            addCalender();
                            e.onNext(true);
                        } else {
                            e.onNext(false);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean saveResult) throws Exception {
                                if (!saveResult) {
                                    CalendarPermissionUtil.showWaringDialog(getActivity());
                                }
                            }
                        });
            } else {
                CalendarPermissionUtil.showWaringDialog(getActivity());
            }
        }
    }



}

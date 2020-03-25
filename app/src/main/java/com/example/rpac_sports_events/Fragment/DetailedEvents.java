package com.example.rpac_sports_events.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.rpac_sports_events.Calendar.CalendarPermissionUtil;
import com.example.rpac_sports_events.Calendar.CalendarReminderUtils;
import com.example.rpac_sports_events.Interface.AppBarText;
import com.example.rpac_sports_events.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DetailedEvents extends Fragment implements AppBarText {
    private TextView eventTitle;
    private TextView eventDate;
    private TextView eventTime;
    private TextView eventLocation;
    private TextView eventDescription;
    private Button add_to_calendar;
    private String[] event;
    private Button add_to_favorite;
    private FirebaseUser user;
    private String uid;
    private DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View detailedEvent = inflater.inflate(R.layout.event_rec_sports_detail, container, false);

        event = getArguments().getString("event").split(">");

        eventTitle = detailedEvent.findViewById(R.id.detailedEventTitle);
        eventDate = detailedEvent.findViewById(R.id.detailedEventDate);
        eventTime = detailedEvent.findViewById(R.id.detailedEventTime);
        eventLocation = detailedEvent.findViewById(R.id.detailedEventLocation);
        eventDescription = detailedEvent.findViewById(R.id.detailedEventDescription);

        add_to_calendar = detailedEvent.findViewById(R.id.calendar_button);
        add_to_favorite = detailedEvent.findViewById(R.id.favorite_button);

        add_to_calendar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fetchPermission(99);
                    }
                }
        );
        add_to_favorite.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(user !=null){
                            uid = user.getUid();
                            myRef.child("user").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean added = false;
                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        if(data.getValue().equals(event[0])){
                                            Toast.makeText(getActivity(), "Event already added",
                                                    Toast.LENGTH_SHORT).show();
                                            added = true;
                                        }
                                    }
                                    if(!added){
                                        myRef.child("user").child(uid).push().setValue(event[0]);
                                        Toast.makeText(getActivity(), "Event added to favorite",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getActivity(), "Database connection error",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else{
                            Toast.makeText(getActivity(), "Please login first",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        loadEvent(event);

        TextView tv = getActivity().findViewById(R.id.appbar_text);
        setBarText(tv);
        return detailedEvent;

    }

    public void loadEvent(String[] event){
        eventTitle.setText(event[0]);
        eventTime.setText(event[1]);
        eventLocation.setText(event[2]);
        eventDescription.setText(event[3]);
        eventDate.setText(event[4]);
    }

    public void fetchPermission(int requestCode){
        int selfPermission;

        try{
            selfPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR);
        }catch(Exception e){
            e.printStackTrace();
            return;
        }

        if(selfPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR}, requestCode);
        }else{
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

    private void addCalender(){
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                CalendarReminderUtils.deleteCalendarEvent(getActivity(), event[0]);
                String[] date = event[5].split("/");
                String[] time = event[1].split(" ");
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
                            getActivity(), event[0], event[3], startTime, endTime, event[2], ampm
                    );

                e.onNext(!CalendarReminderUtils.isNoCalendarData(getActivity().getApplicationContext(), event[0]));
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

    public static long getMsFromDayTime(int year,int month,int day,int hour,int minute){
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.YEAR,year);
        cal.set(java.util.Calendar.MONTH,month);
        cal.set(java.util.Calendar.DAY_OF_MONTH,day);
        cal.set(java.util.Calendar.HOUR_OF_DAY,hour);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MINUTE, minute);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
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
            } else{
                CalendarPermissionUtil.showWaringDialog(getActivity());
            }
        }
    }

    @Override
    public void setBarText(TextView tv) {
        tv.setText("Events");
    }



}

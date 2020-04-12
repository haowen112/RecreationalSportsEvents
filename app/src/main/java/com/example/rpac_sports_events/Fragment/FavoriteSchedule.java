package com.example.rpac_sports_events.Fragment;

import android.Manifest;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpac_sports_events.Calendar.CalendarPermissionUtil;
import com.example.rpac_sports_events.Calendar.CalendarReminderUtils;
import com.example.rpac_sports_events.Favorite.FavoriteEvents;
import com.example.rpac_sports_events.Favorite.FavoriteEventsScheduleAdapter;
import com.example.rpac_sports_events.Interface.AppBarText;
import com.example.rpac_sports_events.Interface.FavoriteItemClickListener;
import com.example.rpac_sports_events.Model.EventScheduleViewModel;
import com.example.rpac_sports_events.Model.MyViewModelFactory;
import com.example.rpac_sports_events.R;

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

public class FavoriteSchedule extends Fragment implements AppBarText {

    private RecyclerView schedule;
    private TextView noFavorite;
    private ProgressBar pb;
    private FavoriteEventsScheduleAdapter scheduleAdapter;
    private FavoriteEvents temp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorite_schedule, container, false);
        ;
        TextView tv = getActivity().findViewById(R.id.appbar_text);
        pb = v.findViewById(R.id.progressbar3);


        schedule = v.findViewById(R.id.favoriteScheduleEvents);
        noFavorite = v.findViewById(R.id.favorite_schedule_text);
        if (isNetworkAvailable()) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            schedule.setLayoutManager(layoutManager);
            FetchSchedule(getArguments().getString("EVENT_TITLE"));
        } else {
            pb.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.no_network,
                    Toast.LENGTH_SHORT).show();
        }
        setBarText(tv);
        return v;
    }

    @Override
    public void setBarText(TextView tv) {
        tv.setText(R.string.event_schedule);
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

//    public void getSchedule(String title) {
//        FetchSchedule(title);
//        EventScheduleViewModel em = new ViewModelProvider(requireActivity(), new MyViewModelFactory(getActivity().getApplication(), title)).get(EventScheduleViewModel.class);
//
////        em = new ViewModelProvider(requireActivity(), new MyViewModelFactory(getActivity().getApplication(), "Cardio Barbell")).get(EventScheduleViewModel.class);
//        em.getSchedule().observe(getActivity(), new Observer<ArrayList<FavoriteEvents>>() {
//            @Override
//            public void onChanged(ArrayList<FavoriteEvents> favoriteEvents) {
//                if (favoriteEvents.size() != 0) {
//                    scheduleAdapter = new FavoriteEventsScheduleAdapter(favoriteEvents, new FavoriteItemClickListener() {
//                        @Override
//                        public void onItemClick(FavoriteEvents event) {
//                            temp = event;
//                            alertScheduleBuilder(event);
//                        }
//                    });
//                    schedule.setAdapter(scheduleAdapter);
//                    pb.setVisibility(View.GONE);
//                } else {
//                    pb.setVisibility(View.GONE);
//                    noFavorite.setVisibility(View.VISIBLE);
//                    noFavorite.setText(R.string.no_event_schedule);
//                }
//            }
//        });
//    }

    private void FetchSchedule(String title) {
        new AsyncTask<Void, Void, ArrayList<FavoriteEvents>>() {
            @Override
            public ArrayList<FavoriteEvents> doInBackground(Void... params) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/M/d");
                LocalDate localDate = LocalDate.now();
                LocalDate tomorrow = localDate.plusDays(1);
                LocalDate dayAfterTomorrow = localDate.plusDays(2);

                // Scrape three days loads of events because of slow website responding time
                String url1 = String.format("https://recsports.osu.edu/events.aspx/%s?d=2&q=%s", dtf.format(localDate), title);
                String url2 = String.format("https://recsports.osu.edu/events.aspx/%s?d=2&q=%s", dtf.format(tomorrow), title);
                String url3 = String.format("https://recsports.osu.edu/events.aspx/%s?d=2&q=%s", dtf.format(dayAfterTomorrow), title);


                //temporary place holder
//                String url1 = String.format("https://recsports.osu.edu/events.aspx/%s?d=2&q=%s", "2020/2/3", title);
//                String url2 = String.format("https://recsports.osu.edu/events.aspx/%s?d=2&q=%s", "2020/2/5", title);
//                String url3 = String.format("https://recsports.osu.edu/events.aspx/%s?d=2&q=%s", "2020/2/6", title);


                Document eventUrls;
                Elements urls;

                ArrayList<FavoriteEvents> events1 = null;
                ArrayList<FavoriteEvents> events2 = null;
                ArrayList<FavoriteEvents> events3 = null;
                ArrayList<Document> eventList1;
                ArrayList<Document> eventList2;
                ArrayList<Document> eventList3;


                try {
                    eventUrls = Jsoup.connect(url1).get();
                    urls = eventUrls.select("li.confirmed a");
                    eventList1 = new ArrayList<Document>();
                    for (Element e : urls) {
                        String event_url = e.attr("href");
                        String detailed_url = String.format("https://recsports.osu.edu%s", event_url);
                        eventList1.add(Jsoup.connect(detailed_url).get());
                    }
                    events1 = FavoriteEvents.fromDocument(eventList1);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    eventUrls = Jsoup.connect(url2).get();
                    urls = eventUrls.select("li.confirmed a");
                    eventList2 = new ArrayList<Document>();
                    for (Element e : urls) {
                        String event_url = e.attr("href");
                        String detailed_url = String.format("https://recsports.osu.edu%s", event_url);
                        eventList2.add(Jsoup.connect(detailed_url).get());
                    }
                    events2 = FavoriteEvents.fromDocument(eventList2);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    eventUrls = Jsoup.connect(url3).get();
                    urls = eventUrls.select("li.confirmed a");
                    eventList3 = new ArrayList<Document>();
                    for (Element e : urls) {
                        String event_url = e.attr("href");
                        String detailed_url = String.format("https://recsports.osu.edu%s", event_url);
                        eventList3.add(Jsoup.connect(detailed_url).get());
                    }
                    events3 = FavoriteEvents.fromDocument(eventList3);

                } catch (IOException e) {
                    e.printStackTrace();
                }


                ArrayList<FavoriteEvents> finalEvents = new ArrayList<>();
                finalEvents.addAll(events1);
                finalEvents.addAll(events2);
                finalEvents.addAll(events3);

                return finalEvents;
            }

            @Override
            protected void onPostExecute(ArrayList<FavoriteEvents> events) {
                if (events.size() != 0) {
                    scheduleAdapter = new FavoriteEventsScheduleAdapter(events, new FavoriteItemClickListener() {
                        @Override
                        public void onItemClick(FavoriteEvents event) {
                            temp = event;
                            alertScheduleBuilder(event);
                        }
                    });
                    schedule.setAdapter(scheduleAdapter);
                    pb.setVisibility(View.GONE);
                } else {
                    pb.setVisibility(View.GONE);
                    noFavorite.setVisibility(View.VISIBLE);
                    noFavorite.setText(R.string.no_event_schedule);
                }

            }
        }.execute();
    }

    public void alertScheduleBuilder(FavoriteEvents event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(event.getTitle());
        builder.setMessage(R.string.select_option);

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
        builder.setPositiveButton(R.string.calendar, dialogClickListener);
        builder.setNegativeButton(R.string.cancel, dialogClickListener);
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
                            Toast.makeText(getActivity().getApplicationContext(), R.string.added_to_calendar, Toast.LENGTH_SHORT).show();
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

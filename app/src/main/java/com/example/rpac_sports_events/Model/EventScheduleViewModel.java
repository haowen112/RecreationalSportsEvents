package com.example.rpac_sports_events.Model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rpac_sports_events.Event.RecSportEvents;
import com.example.rpac_sports_events.Favorite.FavoriteEvents;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


/*
 * ViewModel class for RpacSportEvents object, using AsyncTask and Jsoup to scrape from
 * the recreational sports website.
 *
 * Created by Haowen Liu on 02/12/2020.
 */

public class EventScheduleViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<FavoriteEvents>> event = new MutableLiveData<ArrayList<FavoriteEvents>>();
    private String title;

    // Constructor
    public EventScheduleViewModel(Application app, String title) {
        super(app);
        this.title = title;
        FetchSchedule();
    }

    public LiveData<ArrayList<FavoriteEvents>> getSchedule() {
        return event;
    }

    // AsyncTask to scrape events from recreational website
    private void FetchSchedule() {
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
                event.postValue(events);
            }
        }.execute();
    }

    public void clear() {
        event.postValue(null);
    }
}


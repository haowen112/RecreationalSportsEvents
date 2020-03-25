package com.example.rpac_sports_events.Model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.rpac_sports_events.Event.RecSportEvents;
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

public class EventViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<RecSportEvents>> event = new MutableLiveData<ArrayList<RecSportEvents>>();

    // Constructor
    public EventViewModel(Application app){
        super(app);
        FetchEvents();
    }

    public LiveData<ArrayList<RecSportEvents>> getEvents(){
        return event;
    }

    // AsyncTask to scrape events from recreational website
    private void FetchEvents() {
        new AsyncTask<Void, Void, ArrayList<RecSportEvents>>() {
            @Override
            public ArrayList<RecSportEvents> doInBackground(Void... params) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/M/d");
                LocalDate localDate = LocalDate.now();
                LocalDate tomorrow = localDate.plusDays(1);

//                 Scrape three days loads of events because of slow website responding time
                String url1 = String.format("https://recsports.osu.edu/events.aspx/%s?d=2", dtf.format(localDate));
                String url2 = String.format("https://recsports.osu.edu/events.aspx/%s?d=2", dtf.format(tomorrow));
//
//                String url1 = "https://recsports.osu.edu/events.aspx/2020/2/9?d=2";
//                String url2 = "https://recsports.osu.edu/events.aspx/2020/2/10?d=2";

                Document eventUrls;
                Elements urls;

                ArrayList<Document> eventList1;
                ArrayList<RecSportEvents> events1 = null;
                ArrayList<Document> eventList2;
                ArrayList<RecSportEvents> events2 = null;


                try {
                    eventUrls = Jsoup.connect(url1).get();
                    urls = eventUrls.select("li.confirmed a");
                    eventList1 = new ArrayList<Document>();
                    for (Element e : urls) {
                        String event_url = e.attr("href");
                        String detailed_url = String.format("https://recsports.osu.edu%s", event_url);
                        eventList1.add(Jsoup.connect(detailed_url).get());
                    }
                    events1 = RecSportEvents.fromDocument(eventList1);

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
                    events2 = RecSportEvents.fromDocument(eventList2);

                } catch (IOException e) {
                    e.printStackTrace();
                }




                ArrayList<RecSportEvents> finalEvents = new ArrayList<>();
                finalEvents.addAll(events1);
                finalEvents.addAll(events2);

                return finalEvents;
            }

            @Override
            protected void onPostExecute(ArrayList<RecSportEvents> events) {
                event.setValue(events);
            }
        }.execute();
    }
}


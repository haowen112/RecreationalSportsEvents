package com.example.rpac_sports_events.Favorite;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FavoriteEvents {
    private String title;
    private String time;
    private String location;
    private String description;
    private String date;
    private String urlDate;
    private String pattern = "2.*\\?";

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getUrlDate() {
        return urlDate;
    }

    public FavoriteEvents() {
    }

    public FavoriteEvents(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static FavoriteEvents fromDocument(Document doc) {
        FavoriteEvents event = new FavoriteEvents();

        try {
            event.title = doc.select("div#calendar div.header h2").text();

            event.time = doc.select("div#calendar div#dates span.time").text();

            event.date = doc.select("div#calendar div#dates span.date").text();

            event.location = doc.select("div#calendar div#dates div.mainbar>p:eq(1)").text();

            event.description = doc.select("div#calendar div#dates div.mainbar>p:eq(3)").text();

            Elements e = doc.select("a.button-today");
            Pattern r = Pattern.compile(event.pattern);

            // Now create matcher object.
            Matcher m = r.matcher(e.attr("href"));

            if (m.find()) {
                String date = m.group(0);
                event.urlDate = date.substring(0, date.length() - 1);
                ;
            } else {
                event.urlDate = "";
            }

        } catch (Error e) {
            e.printStackTrace();
            return null;
        }

        return event;
    }

    public static ArrayList<FavoriteEvents> fromDocument(ArrayList<Document> docs) {
        ArrayList<FavoriteEvents> events = new ArrayList<>(docs.size());


        for (int i = 0; i < docs.size(); i++) {
            Document d = null;
            try {
                d = docs.get(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            FavoriteEvents event = FavoriteEvents.fromDocument(d);
            if (event != null) {
                events.add(event);
            }

        }

        return events;

    }

}

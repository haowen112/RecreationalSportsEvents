package com.example.rpac_sports_events;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;
import androidx.annotation.UiThread;
import java.util.TimeZone;

/**
 * Calendar helper class to add event to phone calendar
 *
 * Adopted from https://github.com/dongrong-fu/CalendarUtilDemo
 *
 * Modified by Haowen Liu on 02/13/2020
 *
 */
public class CalendarReminderUtils {

    private static String CALENDER_URL = "content://com.android.calendar/calendars";
    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = "Rpac Events";
    private static String CALENDARS_ACCOUNT_NAME = "Events@RpacSportsEvents.com";
    private static String CALENDARS_ACCOUNT_TYPE = "com.android.rpac_sports_events";
    private static String CALENDARS_DISPLAY_NAME = "Rpac Events";

    /**
     *
     * Check if calendar account has been added , if not add calendar account and then recheck.
     * If account found, return account id, otherwise add account and then recheck
     */
    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if( oldId >= 0 ){
            return oldId;
        }else{
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    /**
     * Check if an account exist,
     * if true, return account id,
     * if false, return -1.
     */
    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALENDER_URL), null, null, null, null);
        try {
            if (userCursor == null) { //查询返回空值
                return -1;
            }
            int count = userCursor.getCount();
            if (count > 0) { //存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * Add calendar account,
     * if success return accound id,
     * else return -1.
     */
    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALENDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }

    /**
     * Add event to calendar
     */
    public static void addCalendarEvent(Context context, String title, String description, long startTime, long endTime, String location, String ampm) {
        if (context == null) {
            return;
        }
        int calId = checkAndAddCalendarAccount(context); //get account id
        if (calId < 0) { //if account id not found, return. Add to calendar failed.
            return;
        }



        TimeZone tz = TimeZone.getDefault();
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(startTime);
        if(ampm.equals("PM")){
            mCalendar.add(Calendar.HOUR_OF_DAY, 12);
        }
        long start = mCalendar.getTime().getTime();
        
        mCalendar.setTimeInMillis(endTime);
        if(ampm.equals("PM")){
            mCalendar.add(Calendar.HOUR_OF_DAY, 12);
        }
        long end = mCalendar.getTime().getTime();

        // add event
        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", description);
        event.put("calendar_id", calId);
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
        event.put(CalendarContract.Events.EVENT_LOCATION, location);
        Uri newEvent = context.getContentResolver().insert(Uri.parse(CALENDER_EVENT_URL), event);
        if (newEvent == null) {
            return;
        }

        //Event reminder
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        values.put(CalendarContract.Reminders.MINUTES, 30);// 30 minutes reminder
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(CALENDER_REMINDER_URL), values);
        if(uri == null) {
            return;
        }
    }

    /**
     * Delete calendar event
     */
    public static void deleteCalendarEvent(Context context,String title) {
        if (context == null) {
            return;
        }
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
        try {
            if (eventCursor == null) { //查询返回空值
                return;
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALENDER_EVENT_URL), id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) { //事件删除失败
                            return;
                        }
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

    /**
     * check if calendar permission is granted
     * @param context
     * @return
     */
    public static boolean isNoCursor(Context context){
        Cursor eventCursor = null;
        try{
            eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, " (deleted != 1)", null, null);
        }finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
        return eventCursor == null;
    }

    @UiThread
    public static boolean isNoCalendarData(Context context, String title){
        Cursor eventCursor = null;
        try{
            eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, " (deleted != 1)", null, null);
            if(eventCursor == null) return true;
            if (eventCursor.getCount() > 0) {
                //cursor all calendar events and find matching title
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        return false;
                    }
                }
            }
        }finally {
            if(eventCursor != null){
                eventCursor.close();
            }
        }


        return true;
    }
}

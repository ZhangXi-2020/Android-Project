package com.byted.camp.todolist;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarReminderUtils {
    // ContentProvider的uri
    private static Uri calendarUri = CalendarContract.Calendars.CONTENT_URI;
    private static Uri eventUri = CalendarContract.Events.CONTENT_URI;
    private static Uri reminderUri = CalendarContract.Reminders.CONTENT_URI;

    private static ContentResolver contentResolver;

    /**
     * 检查是否有日历表,有返回日历id，没有-1
     * */
    private static int isHaveCalender(){
        // 查询日历表的cursor
        Cursor cursor = contentResolver.query(calendarUri,null,null,null,null);
        if (cursor == null || cursor.getCount() == 0){
            return -1;
        }else {
            // 如果有日历表
            try {
                cursor.moveToFirst();
                // 通过cursor返回日历表的第一行的属性值 第一个日历的id
                return cursor.getInt(cursor.getColumnIndex(CalendarContract.Calendars._ID));
            }finally {
                cursor.close();
            }
        }
    }

    /**
     * 添加日历表
     * */
    private static long addCalendar(){
        // 时区
        TimeZone timeZone = TimeZone.getDefault();
        // 配置Calendar
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, "我的日历表");
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, "myAccount");
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, "myType");
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "myDisplayName");
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, "myAccount");
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);
        value.put(CalendarContract.CALLER_IS_SYNCADAPTER,true);

        // 插入calendar
        Uri insertCalendarUri = contentResolver.insert(calendarUri,value);

        if (insertCalendarUri == null){
            return -1;
        }else {
            // return Integer.parseInt(insertCalendarUri.toString());
            return ContentUris.parseId(insertCalendarUri);
        }

    }

    /**
     *  添加日历事件
     * */
    public static void addEvent(Context context, String title, long remind_time){

        // 创建contentResolver
        contentResolver = context.getContentResolver();

        // 日历表id
        int calendarId = isHaveCalender();
        if (calendarId == -1){
            addCalendar();
            calendarId = isHaveCalender();
        }

        // startMillis
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2020,11,1);
        long startMillis = beginTime.getTimeInMillis();

        // endMillis
        Calendar endTime = Calendar.getInstance();
        endTime.set(2020,11,1);
        long endMillis = endTime.getTimeInMillis();

        // 准备event
        ContentValues valueEvent = new ContentValues();
        valueEvent.put(CalendarContract.Events.DTSTART,remind_time);
        valueEvent.put(CalendarContract.Events.DTEND,remind_time+3600*1000);
        valueEvent.put(CalendarContract.Events.TITLE,title);
        valueEvent.put(CalendarContract.Events.DESCRIPTION,"事件描述");
        valueEvent.put(CalendarContract.Events.CALENDAR_ID,calendarId);
        valueEvent.put(CalendarContract.Events.EVENT_TIMEZONE,"Asia/Shanghai");

        // 添加event
        Uri insertEventUri = contentResolver.insert(eventUri,valueEvent);
        if (insertEventUri == null){
            Toast.makeText(context,"添加event失败",Toast.LENGTH_SHORT).show();
        }

        // 添加提醒
        long eventId = ContentUris.parseId(insertEventUri);
        ContentValues valueReminder = new ContentValues();
        valueReminder.put(CalendarContract.Reminders.EVENT_ID,eventId);
        valueReminder.put(CalendarContract.Reminders.MINUTES,15);
        valueReminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALARM);
        Uri insertReminderUri = contentResolver.insert(reminderUri,valueReminder);
        if (insertReminderUri == null){
            Toast.makeText(context,"添加reminder失败",Toast.LENGTH_SHORT).show();
        }
    }
}
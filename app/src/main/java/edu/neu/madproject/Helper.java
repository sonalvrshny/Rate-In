package edu.neu.madproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Helper {
    private static final double RADIUS_OF_EARTH = 6371;

    public static void scheduleNotification(Context context, String uid, Calendar cal) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 4);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, uid);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                notificationIntent, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                        ? PendingIntent.FLAG_MUTABLE
                        : PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 1,
                notificationIntent, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                        ? PendingIntent.FLAG_MUTABLE
                        : PendingIntent.FLAG_UPDATE_CURRENT);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 30);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                12 * 60 * 60 * 1000, pendingIntent);
        alarmManager.cancel(pendingIntent2);
        cal.set(Calendar.HOUR_OF_DAY, 19);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                12 * 60 * 60 * 1000, pendingIntent2);
    }

    //Based on https://www.geeksforgeeks.org/program-distance-two-points-earth/
    public static double calculateDistance(double lat1, double lat2, double lon1, double lon2) {

        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        return (2 * RADIUS_OF_EARTH
                * Math.asin(
                Math.sqrt(
                        Math.pow(Math.sin((lat2 - lat1) / 2), 2)
                                + Math.cos(lat1) * Math.cos(lat2)
                                * Math.pow(Math.sin((lon2 - lon1) / 2), 2))
        ));
    }

    public static void sortForBest(List<Reviews> reviewList, Map<String, Long> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        final Map<String, Long> fMap = map;
        Collections.sort(reviewList, (reviews, t1) -> {
            String category1 = reviews.getCategory();
            String category2 = t1.getCategory();
            long catCount1 = 0;
            long catCount2 = 0;
            if (fMap.containsKey(category1)) {
                catCount1 = (long) fMap.get(category1);
            }
            if (fMap.containsKey(category2)) {
                catCount2 = (long) fMap.get(category2);
            }
            return (int) (catCount2 - catCount1);
        });
    }
}

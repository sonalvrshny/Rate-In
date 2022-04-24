package edu.neu.madproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class Helper {
    private static final double RADIUS_OF_EARTH = 6371;

    public static void scheduleNotification(Context context, String uid, Calendar cal) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        FirebaseDatabase.getInstance().getReference("review").get().addOnCompleteListener(getTask -> {
            String res = (String) getTask.getResult().child("1").getValue();
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, res);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            cal.set(Calendar.HOUR, 12);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    12 * 60 * 60 * 1000, pendingIntent);
        });
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
}

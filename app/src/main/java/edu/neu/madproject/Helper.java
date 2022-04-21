package edu.neu.madproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class Helper {

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
}

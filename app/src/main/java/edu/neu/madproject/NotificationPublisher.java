package edu.neu.madproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    DatabaseReference imageUpload;

    public void onReceive(Context context, Intent intent) {
        imageUpload = FirebaseDatabase.getInstance().getReference("reviews");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String a = intent.getStringExtra(NOTIFICATION);

        imageUpload.get().addOnCompleteListener(getTask -> {
            String res = (String) getTask.getResult().child(a).child("title").getValue();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default_notification_channel_id");
            builder.setContentTitle("Scheduled Notification");
            builder.setContentText(res);
            builder.setSmallIcon(R.drawable.ic_launcher_foreground);
            builder.setAutoCancel(true);
            builder.setChannelId("10001");
            Notification notification = builder.build();
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("10001", "NOTIFICATION_CHANNEL_NAME", importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
            int id = intent.getIntExtra(NOTIFICATION_ID, 0);
            notificationManager.notify(id, notification);
        });
    }
}
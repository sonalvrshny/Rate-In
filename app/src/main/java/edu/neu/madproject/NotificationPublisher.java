package edu.neu.madproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.RequiresPermission;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    DatabaseReference imageUpload;
    DatabaseReference reference1;

    public void onReceive(Context context, Intent intent) {
        imageUpload = FirebaseDatabase.getInstance().getReference("reviews");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String uid = intent.getStringExtra(NOTIFICATION);
        reference1 = FirebaseDatabase.getInstance().getReference().child("userHistory").child(uid).child("reads");
        List<Reviews> reviewsList = new ArrayList<>();
        reference1.get().addOnCompleteListener(x -> {
            imageUpload.get().addOnCompleteListener(getTask -> {
                for (DataSnapshot dataSnapshot : getTask.getResult().getChildren()) {
                    Reviews review = dataSnapshot.getValue(Reviews.class);
                    reviewsList.add(review);
                }
                Map<String, Long> dummy = (Map<String, Long>) x.getResult().getValue();
                Helper.sortForBest(reviewsList, dummy);
                Reviews r = reviewsList.get(0);

                Intent rIntent  = new Intent(context, ReadReviewActivity.class);
                rIntent.putExtra("image", r.getImageURL());
                rIntent.putExtra("title", r.getTitle());
                rIntent.putExtra("category", r.getCategory());
                rIntent.putExtra("rating", r.getRating());
                rIntent.putExtra("username", r.getUsername());
                rIntent.putExtra("content", r.getContent());



                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(new Intent(context, FeedActivity.class));
                stackBuilder.addNextIntentWithParentStack(rIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "readReviewChannel");
                builder.setContentTitle("A review you would like.");
                builder.setContentText(reviewsList.get(0).getTitle());
                builder.setSmallIcon(R.drawable.ic_launcher_foreground);
                builder.setAutoCancel(true);
                builder.setChannelId("10004");
                builder.setContentIntent(resultPendingIntent);
                Notification notification = builder.build();
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel("10004", "NOTIFICATION_CHANNEL_READER", importance);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(notificationChannel);
                int id = intent.getIntExtra(NOTIFICATION_ID, 0);
                notificationManager.notify(id, notification);
            });
        });
    }
}
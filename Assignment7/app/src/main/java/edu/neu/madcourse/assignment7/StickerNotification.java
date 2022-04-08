package edu.neu.madcourse.assignment7;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StickerNotification extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        NotificationChannel channel =
                new NotificationChannel(getString(R.string.emoji_notification_channel),
                        getString(R.string.emoji_received_notification),
                        NotificationManager.IMPORTANCE_DEFAULT);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        RemoteMessage.Notification notification = message.getNotification();
        if (notification == null) return;
        Uri uri = notification.getImageUrl();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Bitmap myBitmap = Helper.getBitmapFromUri(uri);
            Notification.Builder builder = new Notification.Builder(this,
                    getString(R.string.emoji_notification_channel))
                    .setContentTitle(notification.getTitle())
                    .setContentText(notification.getBody())
                    .setAutoCancel(true);
            if (myBitmap != null) {
                builder
                        .setSmallIcon(Icon.createWithBitmap(myBitmap))
                        .setLargeIcon(myBitmap)
                        .setStyle(new Notification.BigPictureStyle().bigPicture(myBitmap)
                                .bigLargeIcon((Bitmap) null));
            }
            NotificationManagerCompat.from(this).notify(new Random().nextInt(), builder.build());
        });
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}
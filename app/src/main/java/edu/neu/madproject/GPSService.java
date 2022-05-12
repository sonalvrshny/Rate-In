package edu.neu.madproject;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GPSService extends Service {
    private double lat;
    private double lon;
    private boolean sameOnce = false;
    private Calendar last = null;
    private Calendar earliestCurr = null;
    private FirebaseAuth auth;
    private static final long SIX_HOURS = 21600000;
    private static final long SIX_HOURS_DEBUG = 2000;

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = this.getSharedPreferences("edu.neu.madproject", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("isServiceRunning", false).apply();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
//        startTimer();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Called");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Tracking channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();
            startForeground(1, notification);
        } else {
            startForeground(2, new Notification());
        }
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        LocationRequest request = LocationRequest.create();
        request.setInterval(600000);
        request.setFastestInterval(600000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.requestLocationUpdates(request, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (SharedPrefUtils.getEmail(GPSService.this) == null || SharedPrefUtils.getEmail(GPSService.this).equals("")) {
                    return;
                }
                if (GPSService.this.last != null
                        && GPSService.this.last.getTimeInMillis()
                        - Calendar.getInstance().getTimeInMillis() < SIX_HOURS) return;
                auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(SharedPrefUtils.getEmail(GPSService.this) + "@gmail.com", SharedPrefUtils.getPassword(GPSService.this)).addOnCompleteListener(task ->
                {
                    if (!task.isSuccessful()) return;
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference().child("blackList").child(auth.getUid());
                    ref.get().addOnCompleteListener(x -> {
                        Location l = locationResult.getLastLocation();
                        List<Pair<Double, Double>> bList = new ArrayList<>();
                        List<String> res;
                        res = (List<String>) x.getResult().getValue();
                        res = res == null ? new ArrayList<>() : res;
                        if (l != null) {
                            double latitude = l.getLatitude();
                            double longitude = l.getLongitude();
                            for (String t : res) {
                                String[] sp = t.split(",");
                                bList.add(new Pair<>(Double.parseDouble(sp[0]), Double.parseDouble(sp[1])));
                                if (Helper.calculateDistance(Double.parseDouble(sp[0]), latitude,
                                        Double.parseDouble(sp[1]), longitude) < 0.08)
                                    return;
                            }
                            if(earliestCurr == null) earliestCurr = Calendar.getInstance();
                            if (Helper.calculateDistance(latitude, lat, longitude, lon) < 0.04) {
                                GPSService.this.sameOnce = true;
                                if(Calendar.getInstance().getTimeInMillis() - earliestCurr.getTimeInMillis() > SIX_HOURS) {
                                    bList.add(new Pair<>(latitude, longitude));
                                    ArrayList<String> list = (ArrayList<String>) bList.stream().sequential().distinct().map(x1 -> x1.first + "," + x1.second).collect(Collectors.toList());
                                    ref.setValue(list).addOnCompleteListener(setTask -> {
                                        if(!setTask.isSuccessful()) {
                                            Log.d("UPLOAD_LIST", "could not push bList");
                                        }
                                    });
                                }
                            } else if (GPSService.this.sameOnce) {
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(GPSService.this);
                                stackBuilder.addNextIntentWithParentStack(new Intent(GPSService.this, FeedActivity.class));
                                stackBuilder.addNextIntentWithParentStack(new Intent(GPSService.this, WriteReviewActivity.class));
                                PendingIntent resultPendingIntent =
                                        stackBuilder.getPendingIntent(0,
                                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(GPSService.this
                                        , "writeReviewChannel");
                                builder.setContentTitle("Write a review");
                                builder.setContentText("Did you visit a place recently? Write a review to help people out.");
                                builder.setSmallIcon(R.drawable.ic_launcher_foreground);
                                builder.setAutoCancel(true);
                                builder.setChannelId("10002");
                                builder.setContentIntent(resultPendingIntent);
                                Notification notification = builder.build();
                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                NotificationChannel notificationChannel = new NotificationChannel("10002", "NOTIFICATION_CHANNEL_WRITER", importance);

                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.createNotificationChannel(notificationChannel);
                                notificationManager.notify(3, notification);
                                GPSService.this.sameOnce = false;
                                GPSService.this.last = Calendar.getInstance();
                            } else {
                                earliestCurr = Calendar.getInstance();
                            }
                            lon = longitude;
                            lat = latitude;
                        }
                    });
                });
            }
        }, null);
    }
}
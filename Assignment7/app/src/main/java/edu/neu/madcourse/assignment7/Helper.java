package edu.neu.madcourse.assignment7;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Helper {
    public static String httpResponse(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        InputStream inputStream = conn.getInputStream();
        return convertStreamToString(inputStream);
    }


    public static String convertStreamToString(InputStream inputStream) {
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String len;
            while ((len = bufferedReader.readLine()) != null) {
                builder.append(len);
            }
            bufferedReader.close();
            return builder.toString().replace(",", ",\n");
        } catch (Exception e) {
            Log.e("Unhandled exceptions", e.getMessage());
        }
        return "";
    }

    public static Bitmap getBitmapFromUrl(URL url) {
        if (url == null) return null;
        try {
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.e("IO exceptions", e.getMessage());
        }
        return null;
    }

    public static Bitmap getBitmapFromUri(Uri uri) {
        if (uri != null) {
            try {
                return getBitmapFromUrl(new URL(uri.toString()));
            } catch (MalformedURLException e) {
                Log.e("Unexpected exceptions", e.getMessage());
            }
        }
        return null;
    }

    public static void sendMessageToDevice(Context context, String targetToken, String title,
                                           String body, String imageUrl) {
        new Thread(() -> {
            JSONObject payload = new JSONObject();
            JSONObject notif = new JSONObject();
            try {
                notif.put("title", title);
                notif.put("body", body);
                notif.put("image", imageUrl);
                notif.put("sound", "default");
                payload.put("to", targetToken);
                payload.put("notification", notif);
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", context.getString(R.string.key));
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Send FCM message content.
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(payload.toString().getBytes());
                outputStream.close();
                conn.getInputStream().close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();

    }
}

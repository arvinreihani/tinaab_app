package com.example.version01;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class JsonUploadService extends Service {

    private static final String TAG = "JsonUploadService";
    private static final String CHANNEL_ID = "JsonUploadServiceChannel";
    private static final long UPLOAD_INTERVAL_MS = 4000; // 4 seconds

    private Timer uploadTimer;
    private String receivedMessage = "{}";
    private String serverUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service Created");
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("JSON Upload Service")
                .setContentText("Service is running")
                .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
                .setContentIntent(createPendingIntent()) // Ensure the notification is interactable
                .build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            serverUrl = intent.getStringExtra("serverUrl");
            receivedMessage = intent.getStringExtra("receivedMessage");

            if (serverUrl == null || receivedMessage == null) {
                Log.e(TAG, "Server URL or received message is null");
//                stopSelf(); // Stop the service if URL or message is not provided
//                return START_NOT_STICKY;
            }

            startUploadingMessages();
        }
        return START_STICKY; // Ensure service is restarted if killed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopUploadingMessages();
        Log.d(TAG, "Service Destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startUploadingMessages() {
        if (uploadTimer == null) {
            uploadTimer = new Timer();
            TimerTask uploadTask = new TimerTask() {
                @Override
                public void run() {
                    uploadJson(receivedMessage, serverUrl);
                }
            };
            uploadTimer.scheduleAtFixedRate(uploadTask, 0, UPLOAD_INTERVAL_MS);
        }
    }

    private void stopUploadingMessages() {
        if (uploadTimer != null) {
            uploadTimer.cancel();
            uploadTimer.purge();
            uploadTimer = null;
        }
    }

    private void uploadJson(String jsonMessage, String serverUrl) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                // Check if receivedMessage is valid
                if (jsonMessage == null || jsonMessage.trim().isEmpty()) {
                    Log.e(TAG, "Received message is null or empty");
                    return;
                }

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(jsonMessage);
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON: ", e);
                    return;
                }

                URL url = new URL(serverUrl);
                Log.d(TAG, "Uploading to URL: " + url.toString()); // Log the URL

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(jsonObject.toString().getBytes("UTF-8"));
                    os.flush();
                }

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                // Read the response from the server
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                Log.d(TAG, "Server Response: " + response.toString());

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "Upload successful");
                    Log.d(TAG, "Uploaded to URL: " + url.toString()); // Log the URL of the upload
                } else {
                    Log.d(TAG, "Server returned: " + responseCode);
                    Log.d(TAG, "Failed to upload to URL: " + url.toString()); // Log the URL of the failed upload
                }

            } catch (Exception e) {
                Log.e(TAG, "Error uploading JSON: ", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader: ", e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "JSON Upload Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private PendingIntent createPendingIntent() {
        Intent notificationIntent = new Intent(this, MainActivity.class); // Replace with your activity
        return PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
    }
}

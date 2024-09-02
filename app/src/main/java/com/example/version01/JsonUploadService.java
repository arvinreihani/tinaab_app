package com.example.version01;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class JsonUploadService extends Service {

    private static final String TAG = "JsonUploadService";
    private static final String CHANNEL_ID = "JsonUploadServiceChannel";
    private static final long UPLOAD_INTERVAL_MS = 500; // 4 seconds
    public static final int MESSAGE_RECEIVED = 2; // Message ID برای شناسایی پیام

    private Timer uploadTimer;
    private String receivedMessage = "{}";
    private String serverUrl;
    private final IBinder binder = new LocalBinder();
    private Handler handler;

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

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 2) {
                    receivedMessage = (String) msg.obj;
                    Log.d(TAG, "Received message via Handler: " + receivedMessage);
                    // شروع فرآیند آپلود
                    startUploadingMessages();
                }
            }
        };
    }

    public class LocalBinder extends Binder {
        public JsonUploadService getService() {
            return JsonUploadService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            serverUrl = intent.getStringExtra("serverUrl");
//            receivedMessage = intent.getStringExtra("receivedMessage");
            Log.d(TAG, "onStartCommand called with intent: " + receivedMessage);

            if (serverUrl == null || receivedMessage == null) {
                Log.e(TAG, "Server URL or received message is null");
            } else {
                startUploadingMessages();
            }
        }
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_RECEIVED) {
                    receivedMessage = (String) msg.obj;
                    Log.d(TAG, "Received message via Handler: " + receivedMessage);
                    // شروع فرآیند آپلود
                    startUploadingMessages();
                }
            }
        };
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopUploadingMessages();
        Log.d(TAG, "Service Destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public Handler getHandler() {
        return handler;
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
            try {
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
                Log.d(TAG, "Uploading to URL: " + url.toString());

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    String jsonString = jsonObject.toString();
                    os.write(jsonString.getBytes("UTF-8"));
                    os.flush();
                    Log.d(TAG, "JSON Content Uploaded: " + jsonString);
                }

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "Upload successful to URL: " + url.toString());
                } else {
                    Log.d(TAG, "Server returned: " + responseCode);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error uploading JSON: ", e);
            } finally {
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

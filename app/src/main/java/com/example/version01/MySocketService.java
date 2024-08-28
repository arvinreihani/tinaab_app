package com.example.version01;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MySocketService extends Service {

    private static final String CHANNEL_ID = "ServerChannel";
    private static final String TAG = "MySocketService";
    private MySocketServer mySocketServer;
    private PowerManager.WakeLock wakeLock;
    private static Handler handler; // Handler برای ارسال پیام‌ها به Activity

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service is being created.");

        // تعریف Handler برای دریافت پیام‌ها
        Handler serviceHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String message = (String) msg.obj;
                    Log.d(TAG, "Message received from socket: " + message);
                    // ارسال پیام به Handler اصلی (Activity)
                    if (handler != null) {
                        handler.obtainMessage(1, message).sendToTarget();
                    }
                }
            }
        };

        mySocketServer = new MySocketServer(serviceHandler);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Creating notification channel for foreground service.");

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Server Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Socket Server")
                    .setContentText("Server is running...")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build();

            startForeground(1, notification);
            Log.d(TAG, "Foreground service started.");
        }

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp:Wakelock");
            wakeLock.acquire();
            Log.d(TAG, "WakeLock acquired.");
        } else {
            Log.e(TAG, "Failed to get PowerManager instance.");
        }

        new Thread(() -> {
            Log.d(TAG, "Starting socket server on port 8080."); // تغییر پورت به 8081
            mySocketServer.startServer(8080);
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service is being destroyed.");

        if (mySocketServer != null) {
            mySocketServer.stopServer();
            Log.d(TAG, "Socket server stopped.");
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            Log.d(TAG, "WakeLock released.");
        } else {
            Log.w(TAG, "WakeLock was not held or already released.");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void setHandler(Handler newHandler) {
        handler = newHandler;
    }
}

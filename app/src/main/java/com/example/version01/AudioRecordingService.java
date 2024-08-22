package com.example.version01;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioRecordingService extends Service {

    private static final String CHANNEL_ID = "AudioRecordingChannel";
    private static final int NOTIFICATION_ID = 1;
    private MediaRecorder recorder;
    private String filePath;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();

        // ایجاد و تنظیم Notification Channel برای Android 8.0 و بالاتر
        createNotificationChannel();

        // ایجاد فایل ضبط
        filePath = generateFilePath();

        // تنظیم MediaRecorder
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(filePath);

        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // شروع ضبط
        recorder.start();

        // شروع سرویس به عنوان Foreground Service
        startForeground(NOTIFICATION_ID, createNotification());

        // استفاده از WakeLock برای بیدار نگه داشتن دستگاه
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AudioRecordingService::WakeLock");
        wakeLock.acquire();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // توقف ضبط
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }

        // آزادسازی WakeLock
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String generateFilePath() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "recorded_audio_" + timestamp + ".3gp";
        return getExternalFilesDir(null) + "/" + fileName;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Audio Recording Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Recording Audio")
                .setContentText("Recording is in progress...")
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
}

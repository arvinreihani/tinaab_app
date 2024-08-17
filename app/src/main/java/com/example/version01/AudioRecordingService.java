package com.example.version01;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioRecordingService extends Service {

    private static final String CHANNEL_ID = "AudioRecordingChannel";
    private MediaRecorder recorder;
    private String filePath;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        filePath = generateFilePath(); // Generate the initial file path
        startRecording();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Return sticky so the service restarts if it's killed
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    private void startRecording() {
        if (recorder != null) {
            recorder.release();
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(filePath);
        try {
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;

            File file = new File(filePath);
            if (file.exists()) {
                Toast.makeText(this, "File saved at: " + filePath, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to save file", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String generateFilePath() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "recorded_audio_" + timestamp + ".3gp";
        return getExternalFilesDir(null) + "/" + fileName;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Audio Recording Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, NextActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Audio Recording")
                .setContentText("Recording audio in background")
                .setSmallIcon(R.drawable.ic_notification) // Use your own notification icon
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }
}

package com.example.version01;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AudioRecordingService extends Service {

    private static final String CHANNEL_ID = "AudioRecordingChannel";
    private static final int NOTIFICATION_ID = 1;
    private MediaRecorder recorder;
    private String filePath;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // دریافت داده از Intent
        if (intent != null && intent.hasExtra("username")) {
            String username = intent.getStringExtra("username");
            Log.d("AudioRecordingService", "Received username: " + username);
            filePath = generateFilePath(username); // استفاده از داده دریافتی
        } else {
            filePath = generateFilePath("username"); // اگر داده‌ای ارسال نشده باشد
        }

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
        startForeground(NOTIFICATION_ID, createNotification());

        // استفاده از WakeLock برای بیدار نگه داشتن دستگاه
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AudioRecordingService::WakeLock");
        wakeLock.acquire();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecordingAndUpload();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String generateFilePath(String username) {
        // استفاده از داده دریافتی برای نام فایل
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return getExternalFilesDir(null) + "/recorded_audio_" + username + "_" + timestamp + ".3gp";
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

    private void stopRecordingAndUpload() {
        if (recorder != null) {
            try {
                recorder.stop();
            } catch (RuntimeException e) {
                Log.e("AudioRecordingService", "Failed to stop recorder: " + e.getMessage());
            }
            recorder.release();
            recorder = null;
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }

        if (filePath != null) {
            uploadFile(filePath);
        }
    }

    private void uploadFile(String filePath) {
        File file = new File(filePath);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("audio/3gp")))
                .build();

        Request request = new Request.Builder()
                .url("https://tinaab.ir/upload.php") // آدرس فایل PHP که فایل‌ها را دریافت می‌کند
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("AudioRecordingService", "File upload failed: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("AudioRecordingService", "File uploaded successfully: " + response.body().string());
                } else {
                    Log.e("AudioRecordingService", "File upload failed: " + response.message());
                }
            }
        });
    }
}


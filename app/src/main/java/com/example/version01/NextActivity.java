package com.example.version01;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class NextActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private Button btnRecord;
    private boolean isRecording = false;
    private CountDownTimer countDownTimer;
    private TextView jsonTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // تنظیمات اولیه
        jsonTextView = findViewById(R.id.textView21); // مطمئن شوید که ID صحیح است
        Button btnprofile = findViewById(R.id.panel);
        Button btnanalysis = findViewById(R.id.analysis);
        Button btngadget = findViewById(R.id.gadget);
        Button btnsetting = findViewById(R.id.setting);
        Button btnsituation = findViewById(R.id.situation);
        btnRecord = findViewById(R.id.btnRecord);

        // شروع سرویس سوکت
        Intent socketServiceIntent = new Intent(this, MySocketService.class);
        startService(socketServiceIntent);

        // تعریف Handler برای دریافت پیام‌ها
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String message = (String) msg.obj;
                    Log.d("TAG", "Message received from socket: " + message);
                    // نمایش پیام در TextView
                    if (jsonTextView != null) {
                        jsonTextView.setText(message);
                    } else {
                        Log.e("NextActivity", "TextView is null");
                    }
                }
            }
        };
        // تنظیم Handler به سرویس
        MySocketService.setHandler(handler);

        // تنظیم دکمه‌ها
        btnprofile.setOnClickListener(v -> {
            String username = getIntent().getStringExtra("username");
            Intent profileIntent = new Intent(NextActivity.this, ProfileActivity.class);
            profileIntent.putExtra("username", username);
            startActivity(profileIntent);
        });

        btngadget.setOnClickListener(v -> {
            String username = getIntent().getStringExtra("username");
            Intent gadgetIntent = new Intent(NextActivity.this, GadgetDataActivity.class);
            gadgetIntent.putExtra("username", username);
            startActivity(gadgetIntent);
        });

        btnsetting.setOnClickListener(v -> {
            String username = getIntent().getStringExtra("username");
            Intent settingIntent = new Intent(NextActivity.this, SettingActivity.class);
            settingIntent.putExtra("username", username);
            startActivity(settingIntent);
        });

        btnanalysis.setOnClickListener(v -> {
            String username = getIntent().getStringExtra("username");
            Intent analysisIntent = new Intent(NextActivity.this, OpenAIActivity.class);
            analysisIntent.putExtra("username", username);
            startActivity(analysisIntent);
        });

        btnsituation.setOnClickListener(v -> {
            String username = getIntent().getStringExtra("username");
            Intent situationIntent = new Intent(NextActivity.this, MySituationActivity.class);
            situationIntent.putExtra("username", username);
            startActivity(situationIntent);
        });

        if (!hasRequiredPermissions()) {
            requestForPermissions();
        }

        btnRecord.setOnClickListener(v -> {
            if (hasRequiredPermissions()) {
                if (isRecording) {
                    Toast.makeText(NextActivity.this, "در حال ضبط", Toast.LENGTH_LONG).show();
                } else {
                    startRecordingService(getIntent().getStringExtra("username"));
                    Toast.makeText(NextActivity.this, "شروع ضبط", Toast.LENGTH_LONG).show();
                    isRecording = true;
                }
            } else {
                Log.e("NextActivity", "Permissions are not granted.");
                requestForPermissions();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // هیچ کد اضافی در اینجا نیاز نیست
    }

    @Override
    protected void onPause() {
        super.onPause();
        // هیچ کد اضافی در اینجا نیاز نیست
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // متوقف کردن سرویس سوکت هنگام بسته شدن Activity
        Intent socketServiceIntent = new Intent(this, MySocketService.class);
        stopService(socketServiceIntent);

        // متوقف کردن ضبط صدا
        if (isRecording) {
            stopRecordingService();
        }
    }

    private void startRecordingService(String username) {
        Intent serviceIntent = new Intent(this, AudioRecordingService.class);
        serviceIntent.putExtra("username", username);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        isRecording = true;

        countDownTimer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // هر ثانیه تایمر می‌تواند عملکرد خاصی انجام دهد
            }

            @Override
            public void onFinish() {
                stopRecordingService();
            }
        }.start();
    }

    private void stopRecordingService() {
        Intent serviceIntent = new Intent(this, AudioRecordingService.class);
        stopService(serviceIntent);
        Toast.makeText(NextActivity.this, "ضبط متوقف شد", Toast.LENGTH_LONG).show();
        isRecording = false;
    }

    private boolean hasRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager() &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO
                    },
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                Toast.makeText(NextActivity.this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NextActivity.this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                Log.d("NextActivity", "Manage External Storage Permissions Granted");
            } else {
                Toast.makeText(NextActivity.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

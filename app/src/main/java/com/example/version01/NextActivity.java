package com.example.version01;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class NextActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private Button btnRecord;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // شروع سرویس سوکت
        Intent socketServiceIntent = new Intent(this, MySocketService.class);
        startService(socketServiceIntent);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Button btnprofile = findViewById(R.id.panel);
        Button btnanalysis = findViewById(R.id.analysis);
        Button btngadget = findViewById(R.id.gadget);
        Button btnsetting = findViewById(R.id.setting);

        Log.e("API_ERROR", "Code: " + ", Message: " + username);

        btnprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NextActivity.this, ProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        btngadget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NextActivity.this, GadgetDataActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        btnsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NextActivity.this, SettingActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        btnanalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NextActivity.this, OpenAIActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        btnRecord = findViewById(R.id.btnRecord);

        if (!hasRequiredPermissions()) {
            requestForPermissions();
        }

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasRequiredPermissions()) {
                    if (isRecording) {
                        stopRecordingService();
                        Toast.makeText(NextActivity.this, "Recording Stopped", Toast.LENGTH_LONG).show();
                        btnRecord.setText("Start Recording");
                        isRecording = false;
                    } else {
                        startRecordingService();
                        Toast.makeText(NextActivity.this, "Recording Started", Toast.LENGTH_LONG).show();
                        btnRecord.setText("Stop Recording");
                        isRecording = true;
                    }
                } else {
                    Log.e("NextActivity", "Permissions are not granted.");
                    requestForPermissions();
                }
            }
        });
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

    private void startRecordingService() {
        Intent serviceIntent = new Intent(this, AudioRecordingService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void stopRecordingService() {
        Intent serviceIntent = new Intent(this, AudioRecordingService.class);
        stopService(serviceIntent);
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
            if (grantResults.length > 0) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Log.d("NextActivity", "Manage External Storage Permissions Granted");
                } else {
                    Toast.makeText(NextActivity.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

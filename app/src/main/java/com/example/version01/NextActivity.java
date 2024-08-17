package com.example.version01;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NextActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private Button btnRecord;
    private boolean isRecording = false;
    private String filePath;
    private MediaRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Button btnprofile = findViewById(R.id.panel);
        Button btnanalysis = findViewById(R.id.analysis);
        Button btngadget = findViewById(R.id.gadget);
        Log.e("API_ERROR", "Code: " + ", Message: " + username);

        btnprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the sign-in activity
                Intent intent = new Intent(NextActivity.this, ProfileActivity.class);
                intent.putExtra("username", username); // ارسال userId به Activity جدید
                startActivity(intent);
            }
        });
//        btngadget.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to the sign-in activity
//                Intent intent = new Intent(NextActivity.this, GadgetDataActivity.class);
//                intent.putExtra("username", username); // ارسال userId به Activity جدید
//                startActivity(intent);
//            }
//        });
        btnanalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the sign-in activity
                Intent intent = new Intent(NextActivity.this, OpenAIActivity.class);
                intent.putExtra("username", username); // ارسال userId به Activity جدید
                startActivity(intent);
            }
        });

        btnRecord = findViewById(R.id.btnRecord);
        filePath = generateFilePath();

        if (!hasRequiredPermissions()) {
            requestForPermissions();
        }

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasRequiredPermissions()) {
                    if (isRecording) {
                        stopRecording();
                        Toast.makeText(NextActivity.this, "Recording Stopped", Toast.LENGTH_LONG).show();
                        btnRecord.setText("Start Recording");
                        isRecording = false;
                    } else {
                        startRecording();
                        Toast.makeText(NextActivity.this, "Recording Started", Toast.LENGTH_LONG).show();
                        btnRecord.setText("Stop Recording");
                        isRecording = true;
                    }
                } else {
                    Log.e("NextActivity", "Permissions are not granted.");
                    requestForPermissions(); // Request permissions again
                }
            }
        });
    }

    private void startRecording() {
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

            // Check if the file exists and notify the user
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
            // Android 11 (R) or above
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
            }
        } else {
            // Below Android 11
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
                    // Manage External Storage Permissions Granted
                    Log.d("NextActivity", "Manage External Storage Permissions Granted");
                } else {
                    Toast.makeText(NextActivity.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

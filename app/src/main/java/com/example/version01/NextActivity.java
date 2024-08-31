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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONObject;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;

public class NextActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private Button btnRecord;
    private boolean isRecording = false;
    private CountDownTimer countDownTimer;
    private TextView jsonTextView;
    private Timer timer;
    private String receivedMessage;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // تنظیمات اولیه
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
                    receivedMessage = (String) msg.obj;
                    Log.d("TAG", "Message received from socket: " + receivedMessage);
                    // نمایش پیام در TextView
                    } else {
                        Log.e("NextActivity", "TextView is null");
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

        // شروع ارسال پیام‌ها
        startSendingMessages();
    }

    private void startSendingMessages() {
        timer = new Timer();

        // تعریف TimerTask برای ارسال پیام هر 2 ثانیه
        TimerTask sendTask = new TimerTask() {
            @Override
            public void run() {
                // اجرای عملیات شبکه در یک Thread جداگانه
                new Thread(() -> sendMessageToGadget(createJsonMessage())).start();
            }
        };

        // شروع TimerTask با تأخیر 0 و تکرار هر 2 ثانیه
        timer.scheduleAtFixedRate(sendTask, 0, 3000);
    }

    private void stopSendingMessages() {
        if (timer != null) {
            timer.cancel(); // توقف TimerTask
            timer = null;
        }
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

        // متوقف کردن ارسال پیام‌ها
        stopSendingMessages();
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

    private String createJsonMessage() {
        try {
            GadgetData gadgetData = new GadgetData(receivedMessage);

            // دسترسی به مقادیر از شیء
            String temp = gadgetData.getTemp();
            String fpr = gadgetData.getHRT();
            String pr = gadgetData.getFHRT();
            String fsp = gadgetData.getFSPO();
            String spo = gadgetData.getSPO();

            Log.d("MainActivity", "Temperature: " + temp);
            Log.d("MainActivity", "FHRT: " + fpr);
            // ایجاد شیء JSON با مقادیر خاص
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Cl", "test");
            jsonObject.put("Temp", temp);
            jsonObject.put("PR", pr);
            jsonObject.put("SPO", spo);
            jsonObject.put("M", "4");
            jsonObject.put("c", "yes");
            jsonObject.put("v", "0");

            return jsonObject.toString();
        } catch (Exception e) {
            Log.e("TAG", "Error creating JSON message", e);
            return "{}"; // بازگرداندن پیام JSON خالی در صورت بروز خطا
        }
    }
    public void processJsonMessage(String jsonString) {
        try {
            // تجزیه پیام JSON به یک شیء JSONObject
            JSONObject jsonObject = new JSONObject(jsonString);

            // استخراج مقادیر کلیدهای مختلف
            String FHRT = jsonObject.getString("FHRT");
            String FSPO = jsonObject.getString("FSPO");
            String FTemp = jsonObject.getString("FTemp");
            String HRT = jsonObject.getString("HRT");
            String SPO = jsonObject.getString("SPO");
            String Temp = jsonObject.getString("Temp");
            String x = jsonObject.getString("x");
            String y = jsonObject.getString("y");
            String z = jsonObject.getString("z");
            String k1 = jsonObject.getString("k1");
            String k2 = jsonObject.getString("k2");
//
//            // نمایش مقادیر یا انجام عملیات دیگر
//            Log.d("JsonParser", "FHRT: " + FHRT);
//            Log.d("JsonParser", "FSPO: " + FSPO);
//            Log.d("JsonParser", "FTemp: " + FTemp);
//            Log.d("JsonParser", "HRT: " + HRT);
//            Log.d("JsonParser", "SPO: " + SPO);
//            Log.d("JsonParser", "Temp: " + Temp);
//            Log.d("JsonParser", "x: " + x);
//            Log.d("JsonParser", "y: " + y);
//            Log.d("JsonParser", "z: " + z);
//            Log.d("JsonParser", "k1: " + k1);
//            Log.d("JsonParser", "k2: " + k2);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JsonParser", "Error parsing JSON: " + e.getMessage());
        }
    }

    public void sendMessageToGadget(String message) {
        try {
            // آدرس IP و پورت گجت
            InetAddress gadgetAddress = InetAddress.getByName("192.168.101.18");
            int gadgetPort = 5050;

            // ایجاد Socket برای ارسال پیام
            DatagramSocket socket = new DatagramSocket();

            // تبدیل پیام به آرایه بایت
            byte[] messageBytes = message.getBytes();

            // ایجاد Packet برای ارسال
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, gadgetAddress, gadgetPort);

            // ارسال Packet
            socket.send(packet);

            // بستن Socket بعد از ارسال پیام
            socket.close();

            Log.d("TAG", "Message sent to gadget: " + message);

        } catch (Exception e) {
            Log.e("TAG", "Error sending message to gadget", e);
        }
    }
    public class GadgetData {

        // متغیرها برای ذخیره مقادیر JSON
        private String FHRT;
        private String FSPO;
        private String FTemp;
        private String HRT;
        private String SPO;
        private String Temp;
        private String x;
        private String y;
        private String z;
        private String k1;
        private String k2;

        // سازنده بدون پارامتر
        public GadgetData() {
        }

        // سازنده با پارامتر که JSON را به شیء تبدیل می‌کند
        public GadgetData(String jsonString) {
            parseJson(jsonString);
        }

        // متد برای پارس کردن JSON و پر کردن مقادیر
        private void parseJson(String jsonString) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                // پر کردن مقادیر از JSON
                this.FHRT = jsonObject.optString("FHRT");
                this.FSPO = jsonObject.optString("FSPO");
                this.FTemp = jsonObject.optString("FTemp");
                this.HRT = jsonObject.optString("HRT");
                this.SPO = jsonObject.optString("SPO");
                this.Temp = jsonObject.optString("Temp");
                this.x = jsonObject.optString("x");
                this.y = jsonObject.optString("y");
                this.z = jsonObject.optString("z");
                this.k1 = jsonObject.optString("k1");
                this.k2 = jsonObject.optString("k2");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Getterها برای دسترسی به مقادیر
        public String getFHRT() {
            return FHRT;
        }

        public String getFSPO() {
            return FSPO;
        }

        public String getFTemp() {
            return FTemp;
        }

        public String getHRT() {
            return HRT;
        }

        public String getSPO() {
            return SPO;
        }

        public String getTemp() {
            return Temp;
        }

        public String getX() {
            return x;
        }

        public String getY() {
            return y;
        }

        public String getZ() {
            return z;
        }

        public String getK1() {
            return k1;
        }

        public String getK2() {
            return k2;
        }
    }

}



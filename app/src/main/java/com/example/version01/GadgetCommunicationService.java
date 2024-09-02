package com.example.version01;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.Manifest;
import android.content.Intent;
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

import java.util.Timer;
import java.util.TimerTask;
import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class GadgetCommunicationService extends Service {

    private static final String TAG = "GadgetCommunicationService";
    private static final String CHANNEL_ID = "GadgetServiceChannel";
    private static final String GADGET_IP = "192.168.249.18"; // IP address of the gadget
    private static final int GADGET_PORT = 5050; // Port of the gadget
    private static final int SEND_INTERVAL_MS = 2000; // 2 seconds

    private Timer timer;
    private String receivedMessage = "{}"; // Default to empty JSON
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service Created");
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Gadget Communication Service")
                .setContentText("Service is running")
                .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
                .setContentIntent(createPendingIntent()) // Ensure the notification is interactable
                .build();
        startForeground(1, notification);
        startSendingMessages();

        // Initialize the handler
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    receivedMessage = (String) msg.obj;
                    Log.d(TAG, "Message received from socket: " + receivedMessage);
                    // Process the received message
                } else {
                    Log.e(TAG, "Unexpected message type");
                }
            }
        };

        // Set handler for socket service
        MySocketService.setHandler(handler);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    receivedMessage = (String) msg.obj;
                    Log.d(TAG, "Message received from socket: " + receivedMessage);
                    // Process the received message
                } else {
                    Log.e(TAG, "Unexpected message type");
                }
            }
        };

        // Set handler for socket service
        MySocketService.setHandler(handler);
        if (intent != null && intent.hasExtra("username")) {
            String username = intent.getStringExtra("username");
            Log.d("AudioRecordingService", "Received username: " + username);
            Intent serviceIntent1 = new Intent(this, JsonUploadService.class);
            serviceIntent1.putExtra("serverUrl", "https://tinaab.ir/save_json.php?username=" + username);
            serviceIntent1.putExtra("receivedMessage", receivedMessage);
            startService(serviceIntent1);
        }
        Log.d(TAG, "onStartCommand called with intent: " + intent);
        return START_STICKY; // Ensure service is restarted if killed

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSendingMessages();
        Log.d(TAG, "Service Destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startSendingMessages() {
        timer = new Timer();
        TimerTask sendTask = new TimerTask() {
            @Override
            public void run() {
                String jsonMessage = createJsonMessage();
                sendMessageToGadget(jsonMessage);
            }
        };
        timer.scheduleAtFixedRate(sendTask, 0, SEND_INTERVAL_MS);
    }

    private void stopSendingMessages() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private String createJsonMessage() {
        try {
            GadgetData gadgetData = new GadgetData(receivedMessage);

            // Access values from the GadgetData object
            String temp = gadgetData.getTemp();
            String fpr = gadgetData.getHRT();
            String pr = gadgetData.getFHRT();
            String fsp = gadgetData.getFSPO();
            String spo = gadgetData.getSPO();

            Log.d(TAG, "Temperature: " + temp);
            Log.d(TAG, "FHRT: " + fpr);

            // Create a JSON object with specific values
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Cl", "test");
            jsonObject.put("Temp", temp);
            jsonObject.put("PR", pr);
            jsonObject.put("SPO", fsp);
            jsonObject.put("M", "4");
            jsonObject.put("c", "yes");
            jsonObject.put("v", "0");

            return jsonObject.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error creating JSON message", e);
            return "{}"; // Return an empty JSON object in case of error
        }
    }

    private void sendMessageToGadget(String message) {
        new Thread(() -> {
            try {
                InetAddress gadgetAddress = InetAddress.getByName(GADGET_IP);
                DatagramSocket socket = new DatagramSocket();
                byte[] messageBytes = message.getBytes();
                DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, gadgetAddress, GADGET_PORT);
                socket.send(packet);
                socket.close();
                Log.d(TAG, "Message sent to gadget: " + message);
            } catch (Exception e) {
                Log.e(TAG, "Error sending message to gadget", e);
            }
        }).start();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Gadget Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private PendingIntent createPendingIntent() {
        Intent notificationIntent = new Intent(this, GadgetDataActivity.class);
        return PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    // Inner class GadgetData
    public class GadgetData {

        // Variables for storing JSON values
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

        // Default constructor
        public GadgetData() {}

        // Constructor with parameter that parses JSON to object
        public GadgetData(String jsonString) {
            parseJson(jsonString);
        }

        // Method to parse JSON and populate values
        private void parseJson(String jsonString) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                // Populate values from JSON
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

        // Getters for accessing values
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

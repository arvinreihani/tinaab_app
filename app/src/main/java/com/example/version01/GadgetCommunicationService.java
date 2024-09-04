package com.example.version01;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import org.json.JSONObject;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.Calendar;


public class GadgetCommunicationService extends Service {

    private static final String TAG = "GadgetCommunicationService";
    private static final String CHANNEL_ID = "GadgetServiceChannel";
    private static final String GADGET_IP = "192.168.249.18"; // IP address of the gadget
    private static final int GADGET_PORT = 5050; // Port of the gadget
    private static final int SEND_INTERVAL_MS = 14000; // 2 seconds
    private static final long UPLOAD_INTERVAL_MS = 500; // 0.5 seconds

    private Timer timer;
    private Handler handler;
    private Handler uploadHandler;
    private Runnable uploadRunnable;
    private JsonUploadService jsonUploadService;
    private boolean isBound = false;
    private String receivedMessage ; // Default to empty JSON
    private int spo ; // Default to empty JSON
    private int hrt ; // Default to empty JSON
    private int rr ; // Default to empty JSON
    private int c = 0; // Default to empty JSON
    private String BP ; // Default to empty JSON
    private int[] SPOA = new int[20];



    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            JsonUploadService.LocalBinder binder = (JsonUploadService.LocalBinder) service;
            jsonUploadService = binder.getService();
            isBound = true;
            // Start sending messages to the upload service every 0.5 seconds
            startUploadScheduler();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service Created");
        createNotificationChannel();
        startForegroundService();
        bindToJsonUploadService();
        setupHandlerForSocketService();
        setupUploadHandler();
        Random random = new Random();
        rr = random.nextInt((15) - 8 + 1) + 8;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("username")) {
            String username = intent.getStringExtra("username");
            Log.d(TAG, "Received username: " + username);
            startJsonUploadService(username);
        }

        // Send message to upload service if bound
        if (isBound) {
            sendMessageToUploadService();
        }

        return START_STICKY; // Ensure service is restarted if killed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSendingMessages();
        stopUploadScheduler();
        Log.d(TAG, "Service Destroyed");
        unbindService(connection); // Unbind from JsonUploadService
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForegroundService() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Gadget Communication Service")
                .setContentText("Service is running")
                .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
                .setContentIntent(createPendingIntent())
                .build();
        startForeground(1, notification);
        startcalcbp();

        startSendingMessages(); // Start sending messages to gadget every 2 seconds

        startSendingMessages1(); // Start sending messages to gadget every 2 seconds

        startSendingMessages2(); // Start sending messages to gadget every 2 seconds

        startSendingMessages3(); // Start sending messages to gadget every 2 seconds

        startSendingMessages4(); // Start sending messages to gadget every 2 seconds

        startSendingMessages5(); // Start sending messages to gadget every 2 seconds

        startSendingMessages6(); // Start sending messages to gadget every 2 seconds

        startSetAlarm();

    }

    private void bindToJsonUploadService() {
        Intent intent = new Intent(this, JsonUploadService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void setupHandlerForSocketService() {
        handler = new Handler(Looper.getMainLooper()) {
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
        MySocketService.setHandler(handler);
    }

    private void setupUploadHandler() {
        uploadHandler = new Handler(Looper.getMainLooper());
        uploadRunnable = new Runnable() {
            @Override
            public void run() {
                if (isBound) {
                    sendMessageToUploadService();
                }
                uploadHandler.postDelayed(this, UPLOAD_INTERVAL_MS);
            }
        };
    }

    private void startUploadScheduler() {
        uploadHandler.post(uploadRunnable);
    }

    private void stopUploadScheduler() {
        uploadHandler.removeCallbacks(uploadRunnable);
    }

    private void startJsonUploadService(String username) {
        Intent serviceIntent = new Intent(this, JsonUploadService.class);
        serviceIntent.putExtra("serverUrl", "https://tinaab.ir/save_json.php?username=" + username);
        serviceIntent.putExtra("receivedMessage", receivedMessage);
        startService(serviceIntent);
    }

    private void sendMessageToUploadService() {
        if (jsonUploadService != null && jsonUploadService.getHandler() != null) {
            Message message = jsonUploadService.getHandler().obtainMessage(2, receivedMessage);
            jsonUploadService.getHandler().sendMessage(message);
            Log.d(TAG, "Message sent to JsonUploadService: " + receivedMessage);
        } else {
            Log.e(TAG, "Unable to send message, service not bound or handler is null");
        }
    }

    private void startSendingMessages() {
        timer = new Timer();
        TimerTask sendTask = new TimerTask() {
            @Override
            public void run() {
                String jsonMessage = createJsonMessage("BP:");
                sendMessageToGadget(jsonMessage);
            }
        };
        timer.scheduleAtFixedRate(sendTask, 2000, SEND_INTERVAL_MS);
    }
    private void startSendingMessages1() {
        timer = new Timer();
        TimerTask sendTask = new TimerTask() {
            @Override
            public void run() {
                String jsonMessage = createJsonMessage(" " + BP);
                sendMessageToGadget(jsonMessage);
            }
        };
        timer.scheduleAtFixedRate(sendTask, 4000, SEND_INTERVAL_MS);
    }
    private void startSendingMessages2() {
        timer = new Timer();
        TimerTask sendTask = new TimerTask() {
            @Override
            public void run() {
                Random random = new Random();
                int min = rr - 3; // حداقل عدد مورد نظر
                int max = rr + 3; // حداکثر عدد مورد نظر
                int a = random.nextInt(max - min + 1) + min;
                String jsonMessage = createJsonMessage("RR: " + a);
                sendMessageToGadget(jsonMessage);
            }
        };
        timer.scheduleAtFixedRate(sendTask, 8000, SEND_INTERVAL_MS);
    }
    private void startSendingMessages3() {
        timer = new Timer();
        TimerTask sendTask = new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY); // ساعت (24 ساعته)
                int minute = calendar.get(Calendar.MINUTE); // دقیقه
                String jsonMessage = createJsonMessage(" " + hour + ":" + minute);
                sendMessageToGadget(jsonMessage);
            }
        };
        timer.scheduleAtFixedRate(sendTask, 12000, SEND_INTERVAL_MS);
    }
    private void startSendingMessages4() {
        timer = new Timer();
        TimerTask sendTask = new TimerTask() {
            @Override
            public void run() {
                String jsonMessage = createJsonMessage(" tinab");
                sendMessageToGadget(jsonMessage);
            }
        };
        timer.scheduleAtFixedRate(sendTask, 0, SEND_INTERVAL_MS);
    }
    private void startSendingMessages5() {
        timer = new Timer();
        TimerTask sendTask = new TimerTask() {
            @Override
            public void run() {
                String jsonMessage = createJsonMessage(" tinab");
                sendMessageToGadget(jsonMessage);
            }
        };
        timer.scheduleAtFixedRate(sendTask, 6000, SEND_INTERVAL_MS);
    }
//    private void inputSPOA() {
//        timer = new Timer();
//        TimerTask sendTask = new TimerTask() {
//            @Override
//            public void run() {
//                GadgetData gadgetData = new GadgetData(receivedMessage);
//                spo = (Integer.valueOf(gadgetData.getFSPO()) + Integer.valueOf(gadgetData.getSPO()) ) / 2;
//                hrt = (Integer.valueOf(gadgetData.getFHRT()) + Integer.valueOf(gadgetData.getHRT()) ) / 2;
//                SPOA[c] =
//
//            }
//        };
//        timer.scheduleAtFixedRate(sendTask, 10000, SEND_INTERVAL_MS);
//    }
    private void startcalcbp() {
        timer = new Timer();
        TimerTask sendTask = new TimerTask() {
            @Override
            public void run() {
                Random random = new Random();

                int mina = 70; // حداقل عدد مورد نظر
                int maxa = 130; // حداکثر عدد مورد نظر
                int minb = 60; // حداکثر عدد مورد نظر
                int a = random.nextInt(maxa - mina + 1) + mina;
                int b = random.nextInt((a - 10) - minb + 1) + minb;
                BP = a + "|" + b;
            }
        };
        timer.scheduleAtFixedRate(sendTask, 0, 30000);
    }
    private void startSetAlarm() {
        timer = new Timer();
        TimerTask sendTask = new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY); // ساعت (24 ساعته)
                int minute = calendar.get(Calendar.MINUTE); // دقیقه
                GadgetData gadgetData = new GadgetData(receivedMessage);
                if (gadgetData.getK1() != null && gadgetData.getK1().equals("1")) {
                    Log.e(TAG, "Error creating JSON message" + hour + ":" + minute);

                }
            }
        };
        timer.scheduleAtFixedRate(sendTask, 0, 200);
    }
    private void startSendingMessages6() {
        timer = new Timer();
        TimerTask sendTask = new TimerTask() {
            @Override
            public void run() {
                String jsonMessage = createJsonMessage(" tinab");
                sendMessageToGadget(jsonMessage);
            }
        };
        timer.scheduleAtFixedRate(sendTask, 10000, SEND_INTERVAL_MS);
    }
    private void stopSendingMessages() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private String createJsonMessage(String S) {
        try {
            GadgetData gadgetData = new GadgetData(receivedMessage);

            // Create a JSON object with specific values
                spo = (Integer.valueOf(gadgetData.getFSPO()) + Integer.valueOf(gadgetData.getSPO()) ) / 2;
                hrt = (Integer.valueOf(gadgetData.getFHRT()) + Integer.valueOf(gadgetData.getHRT()) ) / 2;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Cl", S);
                jsonObject.put("Temp", gadgetData.getTemp());
                if (hrt > 120) {
                    jsonObject.put("PR", "120");
                }
                else if (hrt < 55) {
                    jsonObject.put("PR", "55");
                }else {
                    jsonObject.put("PR", hrt);
                }
                if (spo > 100) {
                    jsonObject.put("SPO", "100");
                }
                else if (spo < 85) {
                    jsonObject.put("SPO", "85");
                }else {
                    jsonObject.put("SPO", spo);
                }
                jsonObject.put("M", "4");
                jsonObject.put("c", "yes");
                jsonObject.put("v", "0");
                return jsonObject.toString();


        } catch (Exception e) {
            Log.e(TAG, "Error creating JSON message", e);
            return null; // Return an empty JSON object in case of error
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

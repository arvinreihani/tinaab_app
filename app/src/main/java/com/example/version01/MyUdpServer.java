package com.example.version01;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MyUdpServer {

    private static final String TAG = "MyUdpServer";
    private DatagramSocket socket;
    private Handler handler; // Handler برای ارسال پیام‌ها به Activity
    private String messageFromClient;
    private String receivedMessage;


    public MyUdpServer(Handler handler) {
        this.handler = handler;
    }

    public void startServer(int listenPort) {
        try {
            socket = new DatagramSocket(listenPort);
            Log.d(TAG, "UDP Server started on port: " + listenPort);

            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                socket.receive(packet); // دریافت داده از گجت
                 messageFromClient = new String(packet.getData(), 0, packet.getLength());
//                Log.d(TAG, "Message from client: " + messageFromClient);
                receivedMessage  = createJsonMessage();

                // ارسال پیام به Handler
                Message msg = handler.obtainMessage(1, receivedMessage);
                handler.sendMessage(msg);

                // ارسال پاسخ به گجت
                InetAddress clientAddress = InetAddress.getByName("192.168.101.8");
                int clientPort = 5050;
                String responseMessage = "Received: " + messageFromClient;
                DatagramPacket responsePacket = new DatagramPacket(
                        responseMessage.getBytes(),
                        responseMessage.length(),
                        clientAddress,
                        clientPort
                );
                socket.send(responsePacket);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in server", e);
        } finally {
            stopServer();
        }
    }

    public void stopServer() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            Log.d(TAG, "UDP Server stopped");
        }
    }
    private String createJsonMessage() {
        try {
            GadgetData gadgetData = new GadgetData(messageFromClient);
            // Create a JSON object with specific values
//            spo = (Integer.valueOf(gadgetData.getFSPO()) + Integer.valueOf(gadgetData.getSPO()) ) / 2;
//            hrt = (Integer.valueOf(gadgetData.getFHRT()) + Integer.valueOf(gadgetData.getHRT()) ) / 2;
            JSONObject jsonObject = new JSONObject();

            if (Integer.valueOf(gadgetData.getFSPO()) > 100) {
                jsonObject.put("FSPO", "100");
            }
            else if (Integer.valueOf(gadgetData.getFSPO()) < 85) {
                if (Integer.valueOf(gadgetData.getFSPO()) == 0) {
                    jsonObject.put("FSPO", "0");
                }
                else {
                    jsonObject.put("SPO", "95");
                }            }else {
                jsonObject.put("FSPO", gadgetData.getFSPO());
            }
            if (Integer.valueOf(gadgetData.getSPO()) > 100) {
                jsonObject.put("SPO", "100");
            }
            else if (Integer.valueOf(gadgetData.getSPO()) < 85) {
                if (Integer.valueOf(gadgetData.getSPO()) == 0) {
                    jsonObject.put("SPO", "0");
                }
                else {
                jsonObject.put("SPO", "95");
            }
            }else {
                jsonObject.put("SPO", gadgetData.getSPO());
            }
            if (Integer.valueOf(gadgetData.getFHRT()) > 120) {
                jsonObject.put("FHRT", "85");
            }
            else if (Integer.valueOf(gadgetData.getFHRT()) < 50) {
                jsonObject.put("FHRT", "70");
            }else {
                jsonObject.put("FHRT", gadgetData.getFHRT());
            }
            if (Integer.valueOf(gadgetData.getHRT()) > 120) {
                jsonObject.put("HRT", "85");
            }
            else if (Integer.valueOf(gadgetData.getHRT()) < 50) {
                jsonObject.put("HRT", "70");
            }else {
                jsonObject.put("HRT", gadgetData.getHRT());
            }
            if (Integer.valueOf(gadgetData.getFTemp()) > 40) {
                jsonObject.put("FTMP", "37");
            }
            else if (Integer.valueOf(gadgetData.getFTemp()) < 36) {
                jsonObject.put("FTemp", "37");
            }else {
                jsonObject.put("FTemp", gadgetData.getFTemp());
            }
            jsonObject.put("Temp", gadgetData.getTemp());
            jsonObject.put("x", gadgetData.getX());
            jsonObject.put("y", gadgetData.getY());
            jsonObject.put("z", gadgetData.getZ());
            jsonObject.put("k1", gadgetData.getK1());
            jsonObject.put("k2", gadgetData.getK2());
            return jsonObject.toString();


        } catch (Exception e) {
            Log.e(TAG, "Error creating JSON message", e);
            return null; // Return an empty JSON object in case of error
        }
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

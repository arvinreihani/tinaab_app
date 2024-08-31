package com.example.version01;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
                String messageFromClient = new String(packet.getData(), 0, packet.getLength());
//                Log.d(TAG, "Message from client: " + messageFromClient);

                // ارسال پیام به Handler
                Message msg = handler.obtainMessage(1, messageFromClient);
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
}

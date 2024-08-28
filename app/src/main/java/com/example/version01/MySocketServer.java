package com.example.version01;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MySocketServer {

    private static final String TAG = "MySocketServer";
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter output;
    private BufferedReader input;
    private Handler handler; // Handler برای ارسال پیام‌ها به Activity

    public MySocketServer(Handler handler) {
        this.handler = handler;
    }

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            Log.d(TAG, "Server started on port: " + port);

            clientSocket = serverSocket.accept();
            Log.d(TAG, "Client connected: " + clientSocket.getInetAddress().getHostAddress());

            output = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String messageFromClient;
            while ((messageFromClient = input.readLine()) != null) {
                Log.d(TAG, "Message from client: " + messageFromClient);

                // ارسال پیام به Handler
                Message msg = handler.obtainMessage(1, messageFromClient);
                handler.sendMessage(msg);

                output.println("Received: " + messageFromClient);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in server", e);
        } finally {
            stopServer();
        }
    }

    public void stopServer() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
            Log.d(TAG, "Server stopped");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping server", e);
        }
    }
}

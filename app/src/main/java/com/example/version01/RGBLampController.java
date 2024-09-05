package com.example.version01;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RGBLampController {

    private String baseUrl = "http://192.168.1.x/rgb";

    // تغییر رنگ با پارامتر دسیمال
    public void setColor(int decimalColor) {
        sendRequest(baseUrl + "?c=" + decimalColor);
    }

    // تغییر حالت لامپ
    public void setMode(int mode) {
        if (mode >= 1 && mode <= 4) {
            sendRequest(baseUrl + "?m=" + mode);
        } else {
            Log.e("RGBLampController", "Invalid mode value");
        }
    }

    // تنظیم سرعت
    public void increaseSpeed() {
        sendRequest(baseUrl + "?s=+");
    }

    public void decreaseSpeed() {
        sendRequest(baseUrl + "?s=-");
    }

    // تنظیم شدت نور (brightness)
    public void increaseBrightness() {
        sendRequest(baseUrl + "?b=+");
    }

    public void decreaseBrightness() {
        sendRequest(baseUrl + "?b=-");
    }

    // روشن و خاموش کردن لامپ
    public void turnOn() {
        sendRequest(baseUrl + "?turn=on");
    }

    public void turnOff() {
        sendRequest(baseUrl + "?turn=off");
    }

    // ذخیره آخرین حالت
    public void saveState() {
        sendRequest(baseUrl + "?save=1");
    }

    // متد عمومی برای ارسال درخواست HTTP
    private void sendRequest(String requestUrl) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(requestUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        Log.d("RGBLampController", "Response: " + response.toString());
                    } else {
                        Log.e("RGBLampController", "Error in connection: " + responseCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}

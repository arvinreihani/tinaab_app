package com.example.version01;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class YourBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Handling the received broadcast
        String message = intent.getStringExtra("message");
        Log.d("YourBroadcastReceiver", "Received message: " + message);
    }
}

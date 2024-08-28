package com.example.version01;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MessageReceiver extends BroadcastReceiver {

    private static final String TAG = "MessageReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Broadcast received");

        if (intent != null && "com.example.version01.MESSAGE_RECEIVED".equals(intent.getAction())) {
            String message = intent.getStringExtra("message");
            if (message != null) {
                Intent activityIntent = new Intent(context, NextActivity.class);
                activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activityIntent.putExtra("message", message);
                context.startActivity(activityIntent);
            }
        }
    }
}

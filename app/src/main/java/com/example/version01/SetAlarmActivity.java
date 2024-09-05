package com.example.version01;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Random;

public class SetAlarmActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private Button btnSleep;
    private Button btnStopAlarm;
    private TextView textViewAlarmTime;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private long minutes;
    private int x ;
    private int j ;
    private int j1 ;
    private int a ;
    private int b ;
    private int ans ;
    private int finalans ;
    private int hour ;
    private int hourmin ;
    private int hourmax ;
    private int mint ;
    private int mintmin ;
    private int mintmax ;
    private int asdellah ;
    private int min ;
    private int max ;
    private int[] z = new int[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setalarm);

        timePicker = findViewById(R.id.timePicker);
        btnSleep = findViewById(R.id.btnSleep);
        btnStopAlarm = findViewById(R.id.btnStopAlarm);
        textViewAlarmTime = findViewById(R.id.textViewAlarmTime);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Random random = new Random();

        x = random.nextInt((15) - 1 + 1) + 1;
        z[0] = random.nextInt((50) - 30 + 1) + 30;
        z[1] = random.nextInt((95) - 87 + 1) + 87;
        z[2] = random.nextInt((190) - 174 + 1) + 144;
        z[3] = random.nextInt((285) - 261 + 1) + 261;
        z[4] = random.nextInt((380) - 348 + 1) + 348;
        z[5] = random.nextInt((475) - 435 + 1) + 435;
        z[6] = random.nextInt((570) - 522 + 1) + 522;

        // Set up the "Back" button
        Button btnBack = findViewById(R.id.back);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Log.e("API_ERROR", "Username: " + username);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetAlarmActivity.this, NextActivity.class);
                intent.putExtra("username", username); // ارسال username به Activity جدید
                startActivity(intent);
            }
        });

        // Set up the "Sleep" button
        btnSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
            }
        });

        // Set up the "Stop Alarm" button
        btnStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
            }
        });
    }

    private void setAlarm() {
        Calendar now = Calendar.getInstance();
        int selectedHour = timePicker.getHour();
        int selectedMinute = timePicker.getMinute();

        Calendar wakeUpTime = Calendar.getInstance();
        wakeUpTime.set(Calendar.HOUR_OF_DAY, selectedHour);
        wakeUpTime.set(Calendar.MINUTE, selectedMinute);

        if (wakeUpTime.before(now)) {
            wakeUpTime.add(Calendar.DAY_OF_YEAR, 1);
        }
        long timeDifference = wakeUpTime.getTimeInMillis() - now.getTimeInMillis();
        long seconds = timeDifference / 1000;
        minutes = seconds / 60;
        j = (int) minutes - x ;
        j1 = j / 91;
        if (j1 == 6){
            ans = z[6];
            asdellah = 6;
        }else {
            a = j - z[j1];
            b = z[j1 + 1] - j;
            if (a <= b){
                ans = z[j1];
                asdellah = j1;
            }else {
                ans = z[j1 + 1];
                asdellah = j1 + 1;
            }
        }
        if (asdellah != 0) {
            min = (asdellah * 87) + x + 5;
            max = (asdellah * 95) + x + 5;
        }else {
            min = 30;
            max = 50;
        }
        finalans = ans + x + 5;
        Calendar startOfDay = Calendar.getInstance(); // زمان شروع روز
        startOfDay.set(Calendar.HOUR_OF_DAY, 0); // تنظیم ساعت به 00
        startOfDay.set(Calendar.MINUTE, 0); // تنظیم دقیقه به 00
        startOfDay.set(Calendar.SECOND, 0); // تنظیم ثانیه به 00
        startOfDay.set(Calendar.MILLISECOND, 0); // تنظیم میلی‌ثانیه به 00
        long millisecondsSinceStartOfDay = now.getTimeInMillis() - startOfDay.getTimeInMillis(); // محاسبه میلی‌ثانیه‌ها از شروع روز
        long wake = millisecondsSinceStartOfDay + (finalans * 60000);
        long minwake = millisecondsSinceStartOfDay + (min * 60000);
        long maxwake = millisecondsSinceStartOfDay + (max * 60000);
        hour = (int) wake / 3600000;
        mint = ((int) wake / 60000)% 60;
        hourmin = (int) minwake / 3600000;
        mintmin = ((int) minwake / 60000)% 60;
        hourmax = (int) maxwake / 3600000;
        mintmax = ((int) maxwake / 60000)% 60;
        wakeUpTime.set(Calendar.HOUR_OF_DAY, hour);
        wakeUpTime.set(Calendar.MINUTE, mint);
        Intent intent = new Intent(SetAlarmActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(SetAlarmActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime.getTimeInMillis(), pendingIntent);

        // Update the TextView with the alarm time
        textViewAlarmTime.setText("زمان پیشنهادی بیداری : " + "\n" + hourmin + ":" + String.format("%02d", mintmin)+" - " + hourmax + ":" + String.format("%02d", mintmax));
//        textViewAlarmTime.setText("" + ans);

        // Calculate time difference

//        long hours = minutes / 60;

        // Update the TextView with the time difference
//        textViewTimeDifference.setText("اختلاف زمانی: " + hours + " ساعت " + (minutes % 60) + " دقیقه");

        Toast.makeText(this, "آلارم تنظیم شد!", Toast.LENGTH_LONG).show();
    }

    private void stopAlarm() {
        Intent intent = new Intent(SetAlarmActivity.this, AlarmReceiver.class);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(SetAlarmActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(cancelPendingIntent);
        AlarmReceiver.stopAlarm();
        Toast.makeText(this, "آلارم قطع شد!", Toast.LENGTH_SHORT).show();
    }
}

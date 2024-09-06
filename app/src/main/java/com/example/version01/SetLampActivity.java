package com.example.version01;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.version01.NextActivity;
import com.example.version01.R;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class SetLampActivity extends AppCompatActivity {

    private OkHttpClient client;
    private TextView txtColorPreview;
    private SeekBar seekBarRed;
    private SeekBar seekBarGreen;
    private SeekBar seekBarBlue;
    private Button btnOnOff;
    private Button btnSave;
    private Button btnBrightnessUp;
    private Button btnBrightnessDown;
    private Button btnSpeedUp;
    private Button btnSpeedDown;
    private Button btnMode;
    private boolean isOn = false;
    private boolean moode = false;
    private String brightness; // Initial brightness value
    private int speed = 50; // Initial speed value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlamp);

        client = new OkHttpClient();

        txtColorPreview = findViewById(R.id.txtColorPreview);
        seekBarRed = findViewById(R.id.seekBarRed);
        seekBarGreen = findViewById(R.id.seekBarGreen);
        seekBarBlue = findViewById(R.id.seekBarBlue);
        btnOnOff = findViewById(R.id.onoff);
        btnSave = findViewById(R.id.save);
        btnBrightnessUp = findViewById(R.id.brightness_up);
        btnBrightnessDown = findViewById(R.id.brightness_down);
        btnSpeedUp = findViewById(R.id.speed_up);
        btnMode = findViewById(R.id.mode);
        // Set up the "Back" button
        Button btnBack = findViewById(R.id.back);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Log.e("API_INFO", "Username: " + username);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetLampActivity.this, NextActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        // Set up the "On/Off" button
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLamp();
            }
        });

        // Set up the "Save" button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveState();
            }
        });

        // Set up the brightness and speed buttons
        btnBrightnessUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustBrightnessp(true);
            }
        });

        btnBrightnessDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustBrightnessn(false);
            }
        });
        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mode();
            }
        });
        btnSpeedUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor();            }
        });

        // Set up SeekBars
        setupSeekBars();
    }

    private void setupSeekBars() {
        seekBarRed.setMax(255);
        seekBarGreen.setMax(255);
        seekBarBlue.setMax(255);

        seekBarRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateColorPreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateColorPreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateColorPreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateColorPreview() {
        int red = seekBarRed.getProgress();
        int green = seekBarGreen.getProgress();
        int blue = seekBarBlue.getProgress();
        int color = Color.rgb(red, green, blue);
        txtColorPreview.setBackgroundColor(color);
    }

    private void toggleLamp() {
        String url = "http://192.168.62.217/Device?CMD=8&turn=" + (isOn ? "off" : "on");
        sendRequest(url);
        isOn = !isOn;
        btnOnOff.setText(isOn ? "Turn Off" : "Turn On");
    }
    private void setColor() {
        int red = seekBarRed.getProgress();
        int green = seekBarGreen.getProgress();
        int blue = seekBarBlue.getProgress();

        // Convert RGB to BGR
        int bgrColor = (red << 16) | (green << 8) | blue;
        String url = "http://192.168.62.217/rgb?c=" + bgrColor;

        sendRequest(url);
        Toast.makeText(this, "Color set to: " + bgrColor, Toast.LENGTH_SHORT).show();
        Log.i("COLOR_SET", "Color set to BGR: " + bgrColor);
    }
    private void saveState() {
        String url = "http://192.168.62.217/rgb?/save=1";
        sendRequest(url);
    }
    private void Mode() {
        if (moode){
            String url = "http://192.168.62.217/rgb?m=1";
            sendRequest(url);
            moode = false;
        }else {
            String url = "http://192.168.62.217/rgb?m=4";
            sendRequest(url);
            moode = true;
        }
    }
    private void adjustBrightnessp(boolean increase) {
        String url = "http://192.168.62.217/rgb?b=+";
        sendRequest(url);

    }
    private void adjustBrightnessn(boolean increase) {
        String url = "http://192.168.62.217/rgb?b=-";
        sendRequest(url);

    }
    private void adjustSpeed(boolean increase) {
        speed = (increase ? 1 :-1);
        speed = Math.max(0, Math.min(100, speed)); // Keep within bounds
        String url = "http://192.168.62.217//rgb?s=" + speed;

        sendRequest(url);
    }

    private void sendRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HTTP_ERROR", "Request failed: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SetLampActivity.this, "Request failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("HTTP_SUCCESS", "Request successful");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SetLampActivity.this, "Request successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e("HTTP_ERROR", "Request failed: " + response.message());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SetLampActivity.this, "Request failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}

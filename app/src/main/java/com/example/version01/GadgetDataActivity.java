package com.example.version01;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class GadgetDataActivity extends AppCompatActivity {

    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gadgetdata);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Log.e("API_ERROR", "Username: " + username);

        backButton = findViewById(R.id.back);

        // اضافه کردن ویو سفارشی برای رسم نمودار
        CustomChartView customChartView = new CustomChartView(this);
        setContentView(customChartView);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GadgetDataActivity.this, NextActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    // کلاس داخلی برای رسم نمودار
    private class CustomChartView extends View {
        private Paint linePaint;
        private float[] dataPoints;

        public CustomChartView(Context context) {
            super(context);
            init();
        }

        public CustomChartView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            linePaint = new Paint();
            linePaint.setColor(Color.BLUE);
            linePaint.setStrokeWidth(5);
            dataPoints = new float[]{100, 200, 150, 300, 250, 400}; // داده‌های نمونه
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (dataPoints.length < 2) return;

            float startX = 50;
            float startY = dataPoints[0];
            for (int i = 1; i < dataPoints.length; i++) {
                float stopX = startX + 100;
                float stopY = dataPoints[i];

                canvas.drawLine(startX, startY, stopX, stopY, linePaint);

                startX = stopX;
                startY = stopY;
            }
        }
    }
}

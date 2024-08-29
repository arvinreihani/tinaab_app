package com.example.version01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

public class GadgetDataActivity extends AppCompatActivity {

    private Button backButton;
    private LineChart lineChart1;
    private LineChart lineChart2;
    private PieChart pieChart;  // تعریف نمودار دایره‌ای

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gadgetdata);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Log.e("API_ERROR", "Username: " + username);

        backButton = findViewById(R.id.back);
        lineChart1 = findViewById(R.id.lineChart);
        lineChart2 = findViewById(R.id.lineChart1);
        pieChart = findViewById(R.id.pieChart);  // مقداردهی نمودار دایره‌ای

        setupChart(lineChart1, new float[]{100, 200, 150, 300, 250, 400, 30, 20, 50, 200, 300, 250, 400, 30, 20, 250, 400, 30, 20, 50, 200, 300, 250, 400, 30, 20}, "Dataset 1");
        setupChart(lineChart2, new float[]{50, 150, 200, 250, 300, 350}, "Dataset 2");
        setupPieChart(pieChart, new float[]{30, 20, 50, 200}, "Dataset 3");  // تنظیم نمودار دایره‌ای

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GadgetDataActivity.this, NextActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    // روش تنظیم نمودار خطی (LineChart)
    private void setupChart(LineChart chart, float[] dataPoints, String label) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataPoints.length; i++) {
            entries.add(new Entry(i, dataPoints[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(getResources().getColor(R.color.purple_500));
        dataSet.setValueTextColor(getResources().getColor(R.color.white));

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // تنظیمات محور x
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        // تنظیمات محور y
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.invalidate(); // برای به‌روزرسانی نمودار
    }

    // روش تنظیم نمودار دایره‌ای (PieChart)
    private void setupPieChart(PieChart chart, float[] dataPoints, String label) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (float data : dataPoints) {
            entries.add(new PieEntry(data));
        }

        PieDataSet dataSet = new PieDataSet(entries, label);
        dataSet.setColors(getResources().getColor(R.color.purple_500),
                getResources().getColor(R.color.pink),
                getResources().getColor(R.color.green),
                getResources().getColor(R.color.teal_200)); // تنظیم رنگ‌ها

        PieData pieData = new PieData(dataSet);
        chart.setData(pieData);
        chart.invalidate(); // برای به‌روزرسانی نمودار
    }
}

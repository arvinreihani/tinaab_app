package com.example.version01;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONObject;

import java.util.ArrayList;

public class GadgetDataActivity extends AppCompatActivity {

    private Button backButton;
    private LineChart lineChart1;
    private LineChart lineChart2;
    private PieChart pieChart;
    private static final String TAG = "gadget";
    private static Handler handler;
    private float[] dataset1 = new float[]{};
    private float[] dataset2 = new float[]{};
    private String receivedMessage = "{}"; // Default value

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
        pieChart = findViewById(R.id.pieChart);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    receivedMessage = (String) msg.obj;
                    Log.d(TAG, "Message received from socket: " + receivedMessage);
                    updateCharts();
                } else {
                    Log.e(TAG, "Unknown message received");
                }
            }
        };
        MySocketService.setHandler(handler);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GadgetDataActivity.this, NextActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    private void updateCharts() {
        GadgetData gadgetData = new GadgetData(receivedMessage);

        String temp = gadgetData.getTemp();
        String fpr = gadgetData.getFHRT();
        String pr = gadgetData.getHRT();
        String fsp = gadgetData.getFSPO();
        String spo = gadgetData.getSPO();

        int intTemp = 0;
        int intFpr = 0;
        try {
            if (temp != null && !temp.isEmpty()) {
                intTemp = Integer.parseInt(temp);
            }
            if (fpr != null && !fpr.isEmpty()) {
                intFpr = Integer.parseInt(fpr);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing numbers: ", e);
        }
        if (intTemp != 0) {
            dataset1 = addElementToArray(dataset1, intTemp);
        }
        if (intFpr != 0) {
            dataset2 = addElementToArray(dataset2, intFpr);
        }
//        dataset1 = addElementToArray(dataset1, intTemp);
//        dataset2 = addElementToArray(dataset2, intFpr);

        setupChart(lineChart1, dataset1, "Temp");
        setupChart(lineChart2, dataset2, "PR");
        setupPieChart(pieChart, new float[]{30, 20, 50, 200}, "Dataset 3");
    }

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

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.invalidate();
    }

    private void setupPieChart(PieChart chart, float[] dataPoints, String label) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (float data : dataPoints) {
            entries.add(new PieEntry(data));
        }

        PieDataSet dataSet = new PieDataSet(entries, label);
        dataSet.setColors(getResources().getColor(R.color.purple_500),
                getResources().getColor(R.color.pink),
                getResources().getColor(R.color.green),
                getResources().getColor(R.color.teal_200));

        PieData pieData = new PieData(dataSet);
        chart.setData(pieData);
        chart.invalidate();
    }

    private float[] addElementToArray(float[] array, float newValue) {
        float[] newArray = new float[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = newValue;
        return newArray;
    }

    public class GadgetData {

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

        public GadgetData() {}

        public GadgetData(String jsonString) {
            parseJson(jsonString);
        }

        private void parseJson(String jsonString) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
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

    public static void setHandler(Handler newHandler) {
        handler = newHandler;
    }
}

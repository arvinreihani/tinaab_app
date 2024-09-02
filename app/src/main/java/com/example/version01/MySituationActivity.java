package com.example.version01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MySituationActivity extends AppCompatActivity {

    private ScoreCalculator scoreCalculatorESS;
    private ScoreCalculator scoreCalculatorSTOPBANG;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysituation);

        // مقداردهی ApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tinaab.ir/") // آدرس سرور خود را وارد کنید
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Log.e("API_ERROR", "Username: " + username);

        // ایجاد نمونه‌های جداگانه برای هر پرسشنامه
        scoreCalculatorESS = new ScoreCalculator();
        scoreCalculatorSTOPBANG = new ScoreCalculator();

        Button btnBack = findViewById(R.id.back);
        Button btnCalculate = findViewById(R.id.calculate_button);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MySituationActivity.this, NextActivity.class);
                intent.putExtra("username", username); // ارسال username به Activity جدید
                startActivity(intent);
            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int totalScoreESS = calculateTotalScoreESS();
                int totalScoreSTOPBANG = calculateTotalScoreSTOPBANG();
                String resultESS = analyzeESSScore(totalScoreESS);
                String resultSTOPBANG = analyzeSTOPBANGScore(totalScoreSTOPBANG);

                Toast.makeText(MySituationActivity.this,
                        "نمره ESS شما: " + totalScoreESS + " (" + resultESS + ")\n" +
                                "نمره STOP-BANG شما: " + totalScoreSTOPBANG + " (" + resultSTOPBANG + ")",
                        Toast.LENGTH_LONG).show();

                // ارسال داده‌ها به سرور
                submitSituation(username, totalScoreESS, resultESS, totalScoreSTOPBANG, resultSTOPBANG);
            }
        });
    }

    private int calculateTotalScoreESS() {
        int[] questionIds = {
                R.id.q1_group, R.id.q2_group, R.id.q3_group,
                R.id.q4_group, R.id.q5_group, R.id.q6_group,
                R.id.q7_group, R.id.q8_group
        };

        int totalScore = 0;

        for (int id : questionIds) {
            RadioGroup radioGroup = findViewById(id);
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                View radioButton = radioGroup.findViewById(selectedId);
                int score = radioGroup.indexOfChild(radioButton);
                totalScore += score;
            }
        }
        return totalScore;
    }

    private int calculateTotalScoreSTOPBANG() {
        int[] questionIds = {
                R.id.stopBang_q1_group, R.id.stopBang_q2_group, R.id.stopBang_q3_group,
                R.id.stopBang_q4_group, R.id.stopBang_q5_group, R.id.stopBang_q6_group,
                R.id.stopBang_q7_group, R.id.stopBang_q8_group
        };

        int totalScore = 8;

        for (int id : questionIds) {
            RadioGroup radioGroup = findViewById(id);
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                View radioButton = radioGroup.findViewById(selectedId);
                int score = radioGroup.indexOfChild(radioButton);
                totalScore -= score;
            }
        }
        return totalScore;
    }

    private String analyzeESSScore(int score) {
        if (score <= 10) {
            return "خواب‌آلودگی طبیعی";
        } else if (score <= 12) {
            return "خواب‌آلودگی متوسط؛ ممکن است نیاز به بررسی داشته باشد";
        } else if (score <= 15) {
            return "خواب‌آلودگی بالا؛ پیشنهاد می‌شود که بررسی‌های بیشتری انجام شود";
        } else {
            return "خواب‌آلودگی شدید؛ نیاز به مشاوره پزشکی فوری";
        }
    }

    private String analyzeSTOPBANGScore(int score) {
        if (score <= 2) {
            return "احتمال کم آپنه انسدادی خواب";
        } else if (score <= 4) {
            return "احتمال متوسط آپنه انسدادی خواب";
        } else {
            return "احتمال بالا برای آپنه انسدادی خواب";
        }
    }

    private void submitSituation(String username, int totalScoreESS, String resultESS, int totalScoreSTOPBANG, String resultSTOPBANG) {
        Call<ApiResponse> call = apiService.submitSituation(username, totalScoreESS, resultESS, totalScoreSTOPBANG, resultSTOPBANG);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                ApiResponse apiResponse = response.body();
                if (response.isSuccessful()) {
                    Toast.makeText(MySituationActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API_ERROR", "Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(MySituationActivity.this, "خطا در ثبت اطلاعات", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage(), t);
                Toast.makeText(MySituationActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // تعریف ApiService به صورت داخلی
    public interface ApiService {
        @FormUrlEncoded
        @POST("submitSituation.php")
        Call<ApiResponse> submitSituation(
                @Field("username") String username,
                @Field("totalScoreESS") int totalScoreESS,
                @Field("resultESS") String resultESS,
                @Field("totalScoreSTOPBANG") int totalScoreSTOPBANG,
                @Field("resultSTOPBANG") String resultSTOPBANG
        );
    }

    // کلاس داخلی برای محاسبه نمرات
    private class ScoreCalculator {
        private int totalScore;

        public ScoreCalculator() {
            this.totalScore = 0;
        }

        public void addScore(int score) {
            totalScore += score;
        }

        public int getTotalScore() {
            return totalScore;
        }

        public void resetScore() {
            totalScore = 0;
        }
    }
}

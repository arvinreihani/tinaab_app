package com.example.version01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import com.google.gson.annotations.SerializedName;

public class OpenAIActivity extends AppCompatActivity {
    private ApiService apiService;
    private TextView textView12;
    private Button sendButton;
    private Button backButton;
    private StringBuilder textBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openai);

        textBuilder = new StringBuilder();


        // مقداردهی ApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tinaab.ir/") // آدرس سرور خود را وارد کنید
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String StringBuilder  = intent.getStringExtra("StringBuilder");
        Log.d("API", "Received username: " + username);
        Log.d("API", "Received StringBuilder : " + StringBuilder);

        fetchUserData(username); // دریافت داده‌های کاربر و ارسال پرامپت به سرور PHP

        // مقداردهی به عناصر رابط کاربری
        textView12 = findViewById(R.id.textView12);
        sendButton = findViewById(R.id.send);
        backButton = findViewById(R.id.back);

        // تنظیم لیسنر برای دکمه "تحلیل"
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchUserData1(username); // دریافت داده‌های کاربر و ارسال پرامپت به سرور PHP
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpenAIActivity.this, NextActivity.class);
                intent.putExtra("username", username); // ارسال username به Activity جدید
                startActivity(intent);
            }
        });
    }

    public void addText(String text) {
        textBuilder.append(text);
    }

    public String getText() {
        return textBuilder.toString();
    }

    private void fetchUserData(String username) {
        Call<UserResponse> call = apiService.getUserData(username);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    Log.d("API", "User data fetched successfully");

                    if ("success".equals(userResponse.getStatus())) {
                        String prompt = createPromptFromUserData(userResponse);
                        Log.d("API", prompt);

                        sendPromptToServer(prompt);
                    } else {
                        String message = userResponse.getMessage();
                        Toast.makeText(OpenAIActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API_ERROR", "Error in response: " + response.message());
                    Toast.makeText(OpenAIActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage());
                Toast.makeText(OpenAIActivity.this, "Failure: " + t.getMessage()+ "please try again", Toast.LENGTH_SHORT).show();
                fetchUserData(username); // دریافت داده‌های کاربر و ارسال پرامپت به سرور PHP
            }
        });
    }
    private void fetchUserData1(String username) {
        Call<UserResponse> call = apiService.getUserData(username);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    Log.d("API", "User data fetched successfully");

                    if ("success".equals(userResponse.getStatus())) {
                        String prompt = createPromptFromUserData1(userResponse);
                        Log.d("API", prompt);

                        sendPromptToServer(prompt);
                    } else {
                        String message = userResponse.getMessage();
                        Toast.makeText(OpenAIActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API_ERROR", "Error in response: " + response.message());
                    Toast.makeText(OpenAIActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage());
                Toast.makeText(OpenAIActivity.this, "Failure: " + t.getMessage()+ "please try again", Toast.LENGTH_SHORT).show();
                fetchUserData(username); // دریافت داده‌های کاربر و ارسال پرامپت به سرور PHP
            }
        });
    }
    private void sendPromptToServer(String prompt) {
        Call<ServerResponse> call = apiService.sendPromptToServer(prompt);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ServerResponse serverResponse = response.body();
                    Log.d("API", "Server response received successfully"+serverResponse.getResponseText());
                    String response1 = serverResponse.getResponseText();
                    textView12.setText(response1);
                    addText(response1);
                } else {
                    Log.e("API_ERROR", "Error in server response: " + response.message());
                    Toast.makeText(OpenAIActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage());
                Toast.makeText(OpenAIActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                String username = intent.getStringExtra("username");
                fetchUserData(username); // دریافت داده‌های کاربر و ارسال پرامپت به سرور PHP
            }
        });
    }

    private String createPromptFromUserData(UserResponse userResponse) {
        // ایجاد پرامپت از داده‌های کاربر
        return "من یک پزشک متخصص بیماری های داخلی هستم و نیاز مند به عملکردی ایده ال در بررسی و مدیریت بیماری با شرایط زیر هستم لطفا تمام توصیه های ضروری برای ارائه به بیمار را همراه با جزییات کامل برای هر مشکل بیمار به صورت مجزا ارایه بده و توضیحاتی برای هر مورد را به تفصیل به بیمار توضیح بده" + "\n" +
                "Fullname: " + userResponse.getFullname() + "\n" +
                "Height: " + userResponse.getHeight() + "\n" +
                "Weight: " + userResponse.getWeight() + "\n" +
                "Age: " + userResponse.getAge() + "\n" +
                "Location: " + userResponse.getLocation() + "\n" +
                "Job: " + userResponse.getJob() + "\n" +
                "Disease Records: " + userResponse.getDiseaseRecords() + "\n" +
                "Hobby: " + userResponse.getHobby() + "\n" +
                "totalScoreESS: " + userResponse.gettotalScoreESS() + "\n" +
                "resultESS: " + userResponse.getresultESS() + "\n" +
                "totalScoreSTOPBANG: " + userResponse.gettotalScoreSTOPBANG() + "\n" +
                "resultSTOPBANG: " + userResponse.getresultSTOPBANG();
    }
    private String createPromptFromUserData1(UserResponse userResponse) {
        EditText AskEditText = findViewById(R.id.prompt);
        String Ask = AskEditText.getText().toString();
        addText(Ask);
        // ایجاد پرامپت از داده‌های کاربر
        return getText();
    }

    public class UserResponse {
        private String fullname;
        private String height;
        private String weight;
        private String age;
        private String location;
        private String job;
        private String diseaseRecords;
        private String hobby;
        private String totalScoreESS;
        private String resultESS;
        private String totalScoreSTOPBANG;
        private String resultSTOPBANG;
        private String status;
        private String message;

        // Getters and Setters
        public String getFullname() { return fullname; }
        public void setFullname(String fullname) { this.fullname = fullname; }
        public String getHeight() { return height; }
        public void setHeight(String height) { this.height = height; }
        public String getWeight() { return weight; }
        public void setWeight(String weight) { this.weight = weight; }
        public String getAge() { return age; }
        public void setAge(String age) { this.age = age; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getJob() { return job; }
        public void setJob(String job) { this.job = job; }
        public String getDiseaseRecords() { return diseaseRecords; }
        public void setDiseaseRecords(String diseaseRecords) { this.diseaseRecords = diseaseRecords; }
        public String getHobby() { return hobby; }
        public void setHobby(String hobby) { this.hobby = hobby; }
        public String gettotalScoreESS() { return totalScoreESS; }
        public void settotalScoreESS(String totalScoreESS) { this.totalScoreESS = totalScoreESS; }
        public String getresultESS() { return resultESS; }
        public void setresultESS(String resultESS) { this.resultESS = resultESS; }
        public String gettotalScoreSTOPBANG() { return totalScoreSTOPBANG; }
        public void settotalScoreSTOPBANG(String totalScoreSTOPBANG) { this.totalScoreSTOPBANG = totalScoreSTOPBANG; }
        public String getresultSTOPBANG() { return resultSTOPBANG; }
        public void setresultSTOPBANG(String resultSTOPBANG) { this.resultSTOPBANG = resultSTOPBANG; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public class ServerResponse {
        @SerializedName("responseText")
        private String responseText;

        public String getResponseText() { return responseText; }
        public void setResponseText(String responseText) { this.responseText = responseText; }
    }


    public interface ApiService {
        @GET("get_user_data.php")
        Call<UserResponse> getUserData(@Query("username") String username);

        @FormUrlEncoded
        @POST("your_php_endpoint.php")
        Call<ServerResponse> sendPromptToServer(
                @Field("prompt") String prompt
        );
    }
}
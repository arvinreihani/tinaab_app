package com.example.version01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private Button analysisButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openai);

        // مقداردهی ApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tinaab.ir/") // آدرس سرور خود را وارد کنید
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Log.d("API", "Received username: " + username);

        // مقداردهی به عناصر رابط کاربری
        textView12 = findViewById(R.id.textView12);
        analysisButton = findViewById(R.id.analysis);
        backButton = findViewById(R.id.back);

        // تنظیم لیسنر برای دکمه "تحلیل"
        analysisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchUserData(username); // دریافت داده‌های کاربر و ارسال پرامپت به سرور PHP
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
                Toast.makeText(OpenAIActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

                    textView12.setText(serverResponse.getResponseText());
                } else {
                    Log.e("API_ERROR", "Error in server response: " + response.message());
                    Toast.makeText(OpenAIActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage());
                Toast.makeText(OpenAIActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String createPromptFromUserData(UserResponse userResponse) {
        // ایجاد پرامپت از داده‌های کاربر
        return "خیلی کوتاه درمورد اطلاعاتی که از خودم میدم توضیح بده و بهم بگو BMI ام چنده و یک توصیه خیلی کوتاه پزشکی بهم بکن" + "\n" +
                "Fullname: " + userResponse.getFullname() + "\n" +
                "Height: " + userResponse.getHeight() + "\n" +
                "Weight: " + userResponse.getWeight() + "\n" +
                "Age: " + userResponse.getAge() + "\n" +
                "Location: " + userResponse.getLocation() + "\n" +
                "Job: " + userResponse.getJob() + "\n" +
                "Disease Records: " + userResponse.getDiseaseRecords() + "\n" +
                "Hobby: " + userResponse.getHobby();
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

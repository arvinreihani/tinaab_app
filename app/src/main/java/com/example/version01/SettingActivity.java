package com.example.version01;

import android.os.Bundle;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

public class SettingActivity extends AppCompatActivity{
    private ProfileActivity.ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // مقداردهی ApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tinaab.ir/") // آدرس سرور خود را وارد کنید
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ProfileActivity.ApiService.class);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Log.e("API_ERROR", "Username: " + username);

        fetchUserData(username);

        TextView fullnameTextView = findViewById(R.id.fullname);
        Button btnBack = findViewById(R.id.back);
        Button btnpanel = findViewById(R.id.edit);
        Button btnsupport = findViewById(R.id.support);
        Button btnabout = findViewById(R.id.about);
        Button btndetails = findViewById(R.id.details);
        Button btnrules = findViewById(R.id.rules);
        Button btnsignout = findViewById(R.id.singout);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, NextActivity.class);
                intent.putExtra("username", username); // ارسال userId به Activity جدید
                startActivity(intent);
            }
        });
        btnpanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ProfileActivity.class);
                intent.putExtra("username", username); // ارسال userId به Activity جدید
                startActivity(intent);
            }
        });
        btnsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btndetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
                intent.putExtra("username", username); // ارسال userId به Activity جدید
                startActivity(intent);
            }
        });
        btnsupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SupportActivity.class);
                intent.putExtra("username", username); // ارسال userId به Activity جدید
                startActivity(intent);
            }
        });
        btnrules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, RulesActivity.class);
                intent.putExtra("username", username); // ارسال userId به Activity جدید
                startActivity(intent);
            }
        });
        btnabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AboutTinabActivity.class);
                intent.putExtra("username", username); // ارسال userId به Activity جدید
                startActivity(intent);
            }
        });
    }
    private void fetchUserData(String username) {
        Call<ProfileActivity.UserResponse> call = apiService.getUserData(username);
        call.enqueue(new Callback<ProfileActivity.UserResponse>() {
            @Override
            public void onResponse(Call<ProfileActivity.UserResponse> call, Response<ProfileActivity.UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileActivity.UserResponse userResponse = response.body();

                    if ("success".equals(userResponse.getStatus())) {
                        // به روزرسانی UI با داده‌های دریافتی
                        TextView fullnameTextView = findViewById(R.id.fullname);

                        fullnameTextView.setText(userResponse.getFullname());

                    } else {
                        String message = userResponse.getMessage();
                        Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SettingActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileActivity.UserResponse> call, Throwable t) {
                Toast.makeText(SettingActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public interface ApiService {
        @GET("get_user_data.php")
        Call<ProfileActivity.UserResponse> getUserData(@Query("username") String username);

        @FormUrlEncoded
        @POST("submitProfile.php")
        Call<ApiResponse> submitProfile(
                @Field("username") String username,
                @Field("fullname") String fullname,
                @Field("height") String height,
                @Field("weight") String weight,
                @Field("age") String age,
                @Field("location") String location,
                @Field("job") String job,
                @Field("diseaseRecords") String diseaseRecords,
                @Field("hobby") String hobby,
                @Field("gender") String gender
        );
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
        private String message; // Optional: for error messages

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

}

package com.example.version01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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


public class ProfileActivity extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // مقداردهی ApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tinaab.ir/") // آدرس سرور خود را وارد کنید
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Log.e("API_ERROR", "Username: " + username);

        fetchUserData(username);

        // یافتن ویوها
        EditText fullnameEditText = findViewById(R.id.editTextText);
        EditText heightEditText = findViewById(R.id.editTextNumber2);
        EditText weightEditText = findViewById(R.id.editTextNumber3);
        EditText ageEditText = findViewById(R.id.editTextNumber);
        EditText locationEditText = findViewById(R.id.editTextText2);
        EditText jobEditText = findViewById(R.id.editTextText3);
        EditText hobbyEditText = findViewById(R.id.editTextText5);
        EditText diseaseRecordsEditText = findViewById(R.id.editTextText6);
        Spinner genderSpinner = findViewById(R.id.spinnerOptions);
        Button submitButton = findViewById(R.id.button);
        Button btnBack = findViewById(R.id.back);

        // تعریف آیتم‌ها به صورت مستقیم در کد جاوا
        String[] options = {"مرد", "زن"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProfile();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, NextActivity.class);
                intent.putExtra("username", username); // ارسال userId به Activity جدید
                startActivity(intent);
            }
        });
    }

    private void submitProfile() {
        // گرفتن مقادیر وارد شده
        EditText fullnameEditText = findViewById(R.id.editTextText);
        EditText heightEditText = findViewById(R.id.editTextNumber2);
        EditText weightEditText = findViewById(R.id.editTextNumber3);
        EditText ageEditText = findViewById(R.id.editTextNumber);
        EditText locationEditText = findViewById(R.id.editTextText2);
        EditText jobEditText = findViewById(R.id.editTextText3);
        EditText hobbyEditText = findViewById(R.id.editTextText5);
        EditText diseaseRecordsEditText = findViewById(R.id.editTextText6);
        Spinner genderSpinner = findViewById(R.id.spinnerOptions);

        String fullname = fullnameEditText.getText().toString();
        String height = heightEditText.getText().toString();
        String weight = weightEditText.getText().toString();
        String age = ageEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String job = jobEditText.getText().toString();
        String hobby = hobbyEditText.getText().toString();
        String diseaseRecords = diseaseRecordsEditText.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();

        // گرفتن username از Intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Log.e("API_ERROR", "Username: " + username);


        // ارسال اطلاعات به سرور
        Call<ApiResponse> call = apiService.submitProfile(username, fullname, height, weight, age, location, job, diseaseRecords, hobby, gender);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                ApiResponse apiResponse = response.body();
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API_ERROR", "Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(ProfileActivity.this, "خطا در ثبت اطلاعات", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage(), t);
                Toast.makeText(ProfileActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // تعریف ApiService به صورت داخلی
    public interface ApiService {
        @GET("get_user_data.php")
        Call<UserResponse> getUserData(@Query("username") String username);

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


    private void fetchUserData(String username) {
        Call<UserResponse> call = apiService.getUserData(username);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();

                    if ("success".equals(userResponse.getStatus())) {
                        // به روزرسانی UI با داده‌های دریافتی
                        TextView fullnameTextView = findViewById(R.id.editTextText);
                        TextView heightTextView = findViewById(R.id.editTextNumber2);
                        TextView weightTextView = findViewById(R.id.editTextNumber3);
                        TextView ageTextView = findViewById(R.id.editTextNumber);
                        TextView locationTextView = findViewById(R.id.editTextText2);
                        TextView jobTextView = findViewById(R.id.editTextText3);
                        TextView diseaseRecordsTextView = findViewById(R.id.editTextText6);
                        TextView hobbyTextView = findViewById(R.id.editTextText5);

                        fullnameTextView.setText(userResponse.getFullname());
                        heightTextView.setText(userResponse.getHeight());
                        weightTextView.setText(userResponse.getWeight());
                        ageTextView.setText(userResponse.getAge());
                        locationTextView.setText(userResponse.getLocation());
                        jobTextView.setText(userResponse.getJob());
                        diseaseRecordsTextView.setText(userResponse.getDiseaseRecords());
                        hobbyTextView.setText(userResponse.getHobby());
                    } else {
                        String message = userResponse.getMessage();
                        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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


package com.example.version01;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.HashMap;
import java.util.Map;
//
//public class MainActivity extends AppCompatActivity {
//
//    // Static HashMap to store usernames and passwords
//    public static final Map<String, String> credentials = new HashMap<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Add some sample credentials
//        credentials.put("arvinreihani", "admin123");
//        credentials.put("hesamhojjat", "admin123");
//        credentials.put("", "");
//
//
//        // Find views by ID
//        EditText etUsername = findViewById(R.id.etUsername);
//        EditText etPassword = findViewById(R.id.etPassword);
//        Button btnLogin = findViewById(R.id.btnLogin);
//        Button btnsignin = findViewById(R.id.signin);
//
//        // Set click listener for the login button
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get input from EditTexts
//                String username = etUsername.getText().toString();
//                String password = etPassword.getText().toString();
//
//                boolean isValid = false;
//
//                // Use enhanced for loop to check if the credentials are correct
//                for (Map.Entry<String, String> entry : credentials.entrySet()) {
//                    if (entry.getKey().equals(username) && entry.getValue().equals(password)) {
//                        isValid = true;
//                        break;
//                    }
//                }
//                // If valid, navigate to the next activity
//                if (isValid) {
//                    Intent intent = new Intent(MainActivity.this, NextActivity.class);
//                    startActivity(intent);
//                    Toast.makeText(MainActivity.this, "خوش آمدید", Toast.LENGTH_LONG).show();
//
//                    finish(); // Optional: close the login activity
//
//                } else {
//                    // If incorrect, show an error message
//                    Toast.makeText(MainActivity.this, "نام کاربری یا رمز عبور اشتباه است", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        // Set click listener for the sign-in button
//        btnsignin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to the sign-in activity
//                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
//                startActivity(intent);
//            }
//        });
//
//    }
//}
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize Retrofit client and ApiService
        apiService = RetrofitClient.getClient().create(ApiService.class);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignin = findViewById(R.id.signin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String username, String password) {
        Call<ApiResponse> call = apiService.checkUser(username, password);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null && "success".equals(apiResponse.getStatus())) {
                        Toast.makeText(MainActivity.this, "خوش آمدید", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, NextActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // ثبت پیام خطا و بررسی محتوای پاسخ
                        String errorMessage = (apiResponse != null) ? apiResponse.getMessage() : "پیام خطا موجود نیست";
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("API_ERROR", "Response Message: " + errorMessage);
                    }
                } else {
                    // ثبت کد وضعیت و پیام خطا
                    Log.e("API_ERROR", "Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(MainActivity.this, "خطا در پاسخ سرور: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "خطا در اتصال به سرور: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

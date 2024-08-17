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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiService apiService;
    private static final int REQUEST_CODE = 200;

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
        // بررسی و درخواست مجوزها
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }
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
                        intent.putExtra("username", username); // ارسال userId به Activity جدید
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // مجوزها به درستی اعطا شدند
            } else {
                // مجوزها رد شدند
            }
        }
    }
}

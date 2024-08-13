package com.example.version01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Retrofit client and ApiService
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Find views by ID
        EditText etNewUsername = findViewById(R.id.etNewUsername);
        EditText etNewEmail = findViewById(R.id.etNewEmail);
        EditText etNewPhone = findViewById(R.id.etNewPhone);
        EditText etNewPassword = findViewById(R.id.etNewPassword);
        EditText etNewPassword2 = findViewById(R.id.etNewPassword2);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        Button btnBack = findViewById(R.id.back);

        // Set click listener for the create account button
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = etNewUsername.getText().toString();
                String newEmail = etNewEmail.getText().toString();
                String newPhone = etNewPhone.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String newPassword2 = etNewPassword2.getText().toString();

                if (newPassword.equals(newPassword2)) {
                    createAccount(newUsername, newEmail, newPhone, newPassword);
                } else {
                    Toast.makeText(SignInActivity.this, "رمز عبور متابقت ندارد", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for the back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createAccount(String username, String email, String phone, String password) {
        Call<ApiResponse> call = apiService.addUser(username, email, phone, password);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        Toast.makeText(SignInActivity.this, "اکانت با موفقیت ساخته شد", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // ثبت کد وضعیت و پیام خطا
                    Log.e("API_ERROR", "Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(SignInActivity.this, "خطا در پاسخ سرور: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage(), t);
                Toast.makeText(SignInActivity.this, "خطا در اتصال به سرور: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

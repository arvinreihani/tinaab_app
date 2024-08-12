//package com.example.version01;
//
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class SignInActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_in);
//
//        // Find views by ID
//        EditText etNewUsername = findViewById(R.id.etNewUsername);
//        EditText etNewPassword = findViewById(R.id.etNewPassword);
//        EditText etNewPassword2 = findViewById(R.id.etNewPassword2);
//        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
//        Button btnback = findViewById(R.id.back);
//
//        // Set click listener for the create account button
//        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get input from EditTexts
//                String newUsername = etNewUsername.getText().toString();
//                String newPassword = etNewPassword.getText().toString();
//                String newPassword2 = etNewPassword2.getText().toString();
//
//                if (newPassword.equals(newPassword2)) {// Add the new user to the credentials map (this should be done through secure storage in a real app)
//                    MainActivity.credentials.put(newUsername, newPassword);
//                    Toast.makeText(SignInActivity.this, "اکاانت با موفقیت ساخته شد", Toast.LENGTH_SHORT).show();
//                    // Navigate back to the login activity
//                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish(); // Close the sign-in activity
//
//                }else {
//                    Toast.makeText(SignInActivity.this, "رمز عبور متابقت ندارد", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//        btnback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to the sign-in activity
//                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//}
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
        EditText etNewPassword = findViewById(R.id.etNewPassword);
        EditText etNewPassword2 = findViewById(R.id.etNewPassword2);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        Button btnBack = findViewById(R.id.back);

        // Set click listener for the create account button
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = etNewUsername.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String newPassword2 = etNewPassword2.getText().toString();

                if (newPassword.equals(newPassword2)) {
                    // Call API to create account
                    createAccount(newUsername, newPassword);
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

    private void createAccount(String username, String password) {
        Call<ApiResponse> call = apiService.addUser(username, password);
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
                Toast.makeText(SignInActivity.this, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

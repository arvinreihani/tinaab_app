package com.example.version01;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.HashMap;
import java.util.Map;

//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//}
public class MainActivity extends AppCompatActivity {

    // Static HashMap to store usernames and passwords
    public static final Map<String, String> credentials = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add some sample credentials
        credentials.put("arvinreihani", "admin123");
        credentials.put("hesamhojjat", "admin123");
        credentials.put("", "");


        // Find views by ID
        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnsignin = findViewById(R.id.signin);

        // Set click listener for the login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input from EditTexts
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                boolean isValid = false;

                // Use enhanced for loop to check if the credentials are correct
                for (Map.Entry<String, String> entry : credentials.entrySet()) {
                    if (entry.getKey().equals(username) && entry.getValue().equals(password)) {
                        isValid = true;
                        break;
                    }
                }
                // If valid, navigate to the next activity
                if (isValid) {
                    Intent intent = new Intent(MainActivity.this, NextActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "welcome", Toast.LENGTH_LONG).show();

                    finish(); // Optional: close the login activity

                } else {
                    // If incorrect, show an error message
                    Toast.makeText(MainActivity.this, "نام کاربری یا رمز عبور اشتباه است", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Set click listener for the sign-in button
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the sign-in activity
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

    }
}
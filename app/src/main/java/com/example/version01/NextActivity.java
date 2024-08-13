package com.example.version01;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NextActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Button btnprofile = findViewById(R.id.panel);
        Log.e("API_ERROR", "Code: " + ", Message: " + username);

        btnprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the sign-in activity
                Intent intent = new Intent(NextActivity.this, ProfileActivity.class);
                intent.putExtra("username", username); // ارسال userId به Activity جدید
                startActivity(intent);
            }
        });

    }
}

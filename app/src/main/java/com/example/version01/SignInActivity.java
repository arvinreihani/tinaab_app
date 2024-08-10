package com.example.version01;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Find views by ID
        EditText etNewUsername = findViewById(R.id.etNewUsername);
        EditText etNewPassword = findViewById(R.id.etNewPassword);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        Button btnback = findViewById(R.id.back);

        // Set click listener for the create account button
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input from EditTexts
                String newUsername = etNewUsername.getText().toString();
                String newPassword = etNewPassword.getText().toString();

                // Add the new user to the credentials map (this should be done through secure storage in a real app)
                MainActivity.credentials.put(newUsername, newPassword);

                // Show a success message
                Toast.makeText(SignInActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                // Navigate back to the login activity
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the sign-in activity
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the sign-in activity
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

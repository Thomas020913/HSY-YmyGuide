package com.thomas.ymyguide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        TextView welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText("欢迎使用圆明园景区导览系统！");

        Button loginButton = findViewById(R.id.btn_start_login);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClassName(WelcomeActivity.this, "com.thomas.login.LoginActivity");
            startActivity(intent);
        });

        Button registerButton = findViewById(R.id.btn_start_register);
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClassName(WelcomeActivity.this, "com.thomas.login.RegisterActivity");
            startActivity(intent);
        });
    }
}

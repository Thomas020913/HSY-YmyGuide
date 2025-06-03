package com.thomas.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thomas.page.AdminActivity;
import com.thomas.page.TravelerActivity;
import com.thomas.page.WorkerActivity;

import com.thomas.login.util.HttpRequestUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 构建登录请求的 JSON 字符串
        String json = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";

        new Thread(() -> {
            try {
                // 调用封装好的 POST 请求方法
                String responseBody = HttpRequestUtil.postRequest("http://192.168.166.112:8080/api/login", json);

                // 假设响应体是返回的 role（整数）
                int role = Integer.parseInt(responseBody);

                runOnUiThread(() -> {
                    if (role == 1) {
                        startActivity(new Intent(LoginActivity.this, TravelerActivity.class));
                    } else if (role == 2) {
                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                    } else if (role == 3) {
                        startActivity(new Intent(LoginActivity.this, WorkerActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}

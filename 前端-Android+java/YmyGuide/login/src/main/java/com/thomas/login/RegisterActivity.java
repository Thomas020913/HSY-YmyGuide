package com.thomas.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thomas.login.util.HttpRequestUtil;
import com.google.gson.Gson;


class UserResponse {
    public boolean success;
    public String message;
}

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPhone, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.et_username);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> registerUser());
    }
    private void registerUser() {
        String phone = etPhone.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() && phone.isEmpty()) {
            Toast.makeText(this, "请输入手机号或用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        String json = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\", \"phone\":\"" + phone +"\"}";

        new Thread(() -> {
            try{
                // 调用后端注册接口
                String responseBody = HttpRequestUtil.postRequest("http://192.168.166.112:8080/api/register", json);

                Gson gson = new Gson();
                UserResponse user = gson.fromJson(responseBody, UserResponse.class);
                runOnUiThread(() -> Toast.makeText(this, user.message, Toast.LENGTH_SHORT).show());
                if (user.success){
                    Thread.sleep(1500);
                    runOnUiThread(() -> Toast.makeText(this, "即将跳转登录界面", Toast.LENGTH_SHORT).show());
                    Thread.sleep(1500);
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }).start();

    }
}
package com.thomas.page.admin.user;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.thomas.page.R;
import org.json.JSONObject;
import okhttp3.*;

import java.io.IOException;

public class UserAddActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    private static final String ADD_USER_URL = "http://192.168.166.112:8080/api/traveler/add";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user_add);

        EditText etUsername = findViewById(R.id.et_username);
        EditText etPhone = findViewById(R.id.et_phone);
        EditText etPassword = findViewById(R.id.et_password);
        EditText etUserType = findViewById(R.id.et_user_type);
        Button btnSubmit = findViewById(R.id.btn_submit_user);

        btnSubmit.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请填写所有必填项", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject json = new JSONObject();
                json.put("username", username);
                json.put("phone", phone);
                json.put("password", password);

                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url(ADD_USER_URL)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() ->
                                Toast.makeText(UserAddActivity.this, "请求失败：" + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(() -> {
                            if (response.isSuccessful()) {
                                Toast.makeText(UserAddActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserAddActivity.this, "添加失败，状态码：" + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "构造请求失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

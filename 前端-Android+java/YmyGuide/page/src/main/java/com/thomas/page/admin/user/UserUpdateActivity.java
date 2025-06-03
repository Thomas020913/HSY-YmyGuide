package com.thomas.page.admin.user;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.thomas.page.R;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.*;

public class UserUpdateActivity extends AppCompatActivity {

    private static final String URL_UPDATE = "http://192.168.166.112:8080/api/traveler/update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user_update);

        EditText etUsername = findViewById(R.id.et_username);
        EditText etPhone = findViewById(R.id.et_phone);
        EditText etPassword = findViewById(R.id.et_password);
        Button btnSubmit = findViewById(R.id.btn_submit_update);

        btnSubmit.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject json = new JSONObject();
                json.put("username", username);
                json.put("phone", phone);
                json.put("password", password);

                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url(URL_UPDATE)
                        .put(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> Toast.makeText(UserUpdateActivity.this, "请求失败", Toast.LENGTH_SHORT).show());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(() -> {
                            if (response.isSuccessful()) {
                                Toast.makeText(UserUpdateActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserUpdateActivity.this, "修改失败，服务器返回错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "数据打包失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

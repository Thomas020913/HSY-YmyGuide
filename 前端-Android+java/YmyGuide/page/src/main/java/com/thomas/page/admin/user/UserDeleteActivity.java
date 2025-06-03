package com.thomas.page.admin.user;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.thomas.page.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class UserDeleteActivity extends AppCompatActivity {
    private EditText etUsername;
    private Button btnDelete;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user_delete);

        etUsername = findViewById(R.id.et_username);
        btnDelete = findViewById(R.id.btn_submit_delete);

        btnDelete.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                return;
            }

            deleteUserByUsername(username);
        });
    }

    private void deleteUserByUsername(String username) {
        String url = "http://192.168.166.112:8080/api/traveler/delete?username=" + username;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(UserDeleteActivity.this, "删除失败，请检查网络", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(UserDeleteActivity.this, "已删除用户：" + username, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserDeleteActivity.this, "删除失败，用户不存在", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

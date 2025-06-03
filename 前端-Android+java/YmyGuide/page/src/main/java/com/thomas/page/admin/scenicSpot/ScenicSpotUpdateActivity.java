package com.thomas.page.admin.scenicSpot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thomas.page.R;
import com.thomas.page.util.HttpRequestUtil;

import org.json.JSONObject;

public class ScenicSpotUpdateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_scenic_spot_update);

        EditText editName = findViewById(R.id.edit_name);
        EditText editIntro = findViewById(R.id.edit_intro);
        Button btnUpdate = findViewById(R.id.btn_update);
        TextView textResult = findViewById(R.id.text_result);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                String intro = editIntro.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(ScenicSpotUpdateActivity.this, "请输入景点名称", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (intro.isEmpty()) {
                    Toast.makeText(ScenicSpotUpdateActivity.this, "请输入简介", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(() -> {
                    try {
                        String url = "http://192.168.166.112:8080/api/scenic/update"; // 替换为你的后端地址
                        JSONObject json = new JSONObject();
                        json.put("name", name);
                        json.put("intro", intro);

                        String result = HttpRequestUtil.postRequest(url, json.toString());

                        runOnUiThread(() -> {
                            try {
                                JSONObject resJson = new JSONObject(result);
                                boolean success = resJson.getBoolean("success");
                                String message = resJson.getString("message");

                                if (success) {
                                    textResult.setText("景点 '" + name + "' 更新成功！");
                                } else {
                                    textResult.setText("更新失败：" + message);
                                }
                                textResult.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                e.printStackTrace();
                                textResult.setText("响应解析失败");
                                textResult.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            textResult.setText("请求失败，请检查网络或服务器");
                            textResult.setVisibility(View.VISIBLE);
                        });
                    }
                }).start();

            }
        });
    }
}

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

public class ScenicSpotAddActivity extends AppCompatActivity {

    private static final String ADD_URL = "http://192.168.166.112:8080/api/scenic/add";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_scenic_spot_add);

        EditText editName = findViewById(R.id.edit_name);
        EditText editIntro = findViewById(R.id.edit_intro);
        Button btnAdd = findViewById(R.id.btn_add);
        TextView textResult = findViewById(R.id.text_result);

        btnAdd.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String intro = editIntro.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(ScenicSpotAddActivity.this, "请输入景点名称", Toast.LENGTH_SHORT).show();
                return;
            }

            if (intro.isEmpty()) {
                Toast.makeText(ScenicSpotAddActivity.this, "请输入简介", Toast.LENGTH_SHORT).show();
                return;
            }

            // 开启子线程发送网络请求
            new Thread(() -> {
                try {
                    JSONObject json = new JSONObject();
                    json.put("name", name);
                    json.put("intro", intro);

                    String response = HttpRequestUtil.postRequest(ADD_URL, json.toString());

                    runOnUiThread(() -> {
                        textResult.setText("景点 '" + name + "' 添加成功！");
                        textResult.setVisibility(View.VISIBLE);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        textResult.setText("添加失败，请检查网络或服务器！");
                        textResult.setVisibility(View.VISIBLE);
                    });
                }
            }).start();
        });
    }
}

package com.thomas.page.admin.scenicSpot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thomas.page.R;
import com.thomas.page.util.HttpRequestUtil;

import org.json.JSONObject;

public class ScenicSpotDeleteActivity extends AppCompatActivity {
    private static final String DELETE_URL = "http://192.168.166.112:8080/api/scenic/delete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_scenic_spot_delete);

        EditText editName = findViewById(R.id.edit_name);
        Button btnDelete = findViewById(R.id.btn_delete);
        TextView textResult = findViewById(R.id.text_result);

        btnDelete.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(ScenicSpotDeleteActivity.this, "请输入要删除的景点名称", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(ScenicSpotDeleteActivity.this)
                    .setTitle("确认删除")
                    .setMessage("确定要删除景点：" + name + " 吗？")
                    .setPositiveButton("确认", (dialog, which) -> {
                        // 开启后台线程进行网络请求
                        new Thread(() -> {
                            try {
                                JSONObject json = new JSONObject();
                                json.put("name", name);

                                String response = HttpRequestUtil.postRequest(DELETE_URL, json.toString());

                                runOnUiThread(() -> {
                                    textResult.setText("景点 '" + name + "' 删除成功！");
                                    textResult.setVisibility(View.VISIBLE);
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(() -> {
                                    textResult.setText("删除失败，请检查网络或服务器状态！");
                                    textResult.setVisibility(View.VISIBLE);
                                });
                            }
                        }).start();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }
}

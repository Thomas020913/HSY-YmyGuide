package com.thomas.page.worker.ticket_order;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.thomas.page.R;
import com.thomas.page.util.HttpRequestUtil;
import org.json.JSONObject;

public class TicketUpdateActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_ticket_update);

        Button confirmButton = findViewById(R.id.btn_confirm);
        EditText codeEditText = findViewById(R.id.et_code);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeEditText.getText().toString().trim();
                if (!code.isEmpty()) {
                    // 新增：核销逻辑
                    new Thread(() -> {
                        try {
                            JSONObject json = new JSONObject();
                            json.put("orderNumber", code);
                            String response = HttpRequestUtil.postRequest("http://192.168.166.112:8080/api/ticket_order/verify", json.toString());
                            runOnUiThread(() -> {
                                if (response != null && response.contains("success")) {
                                    Toast.makeText(TicketUpdateActivity.this, "门票已核销", Toast.LENGTH_SHORT).show();
                                } else if (response != null && response.contains("already verified")) {
                                    Toast.makeText(TicketUpdateActivity.this, "该订单已核销", Toast.LENGTH_SHORT).show();
                                } else if (response != null && response.contains("not found")) {
                                    Toast.makeText(TicketUpdateActivity.this, "订单号不存在", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TicketUpdateActivity.this, "核销失败: " + response, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(TicketUpdateActivity.this, "网络或服务器错误", Toast.LENGTH_SHORT).show());
                        }
                    }).start();
                } else {
                    Toast.makeText(TicketUpdateActivity.this, "请输入门票订单号", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

package com.thomas.page.worker.product_order;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.thomas.page.R;
import com.thomas.page.util.HttpRequestUtil;
import org.json.JSONObject;

public class ProductUpdateActivity extends Activity {
    private static final String API_URL = "http://192.168.166.112:8080/api/product_order/verify";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_product_update);

        EditText codeEditText = findViewById(R.id.et_code);
        Spinner statusSpinner = findViewById(R.id.spinner_status);
        Button confirmButton = findViewById(R.id.btn_confirm_update);

        // 设置下拉栏内容
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeEditText.getText().toString().trim();
                String status = statusSpinner.getSelectedItem().toString();
                if (code.isEmpty()) {
                    Toast.makeText(ProductUpdateActivity.this, "请输入商品订单号", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // 新增：更新订单状态逻辑
                new Thread(() -> {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("orderNumber", code);
                        json.put("status", status);
                        String response = HttpRequestUtil.postRequest(API_URL, json.toString());
                        runOnUiThread(() -> {
                            if (response != null && response.contains("success")) {
                                Toast.makeText(ProductUpdateActivity.this, "订单状态更新成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (response != null && response.contains("not found")) {
                                Toast.makeText(ProductUpdateActivity.this, "订单号不存在", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProductUpdateActivity.this, "更新失败: " + response, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(ProductUpdateActivity.this, "网络或服务器错误", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });
    }
}

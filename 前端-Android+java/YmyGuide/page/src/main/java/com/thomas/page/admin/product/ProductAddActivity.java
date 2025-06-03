package com.thomas.page.admin.product;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.thomas.page.R;
import com.thomas.page.util.HttpRequestUtil;
import org.json.JSONObject;

public class ProductAddActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_product_add);

        EditText etName = findViewById(R.id.et_product_name);
        Spinner spinnerCategory = findViewById(R.id.spinner_category);
        EditText etPrice = findViewById(R.id.et_price);
        EditText etStock = findViewById(R.id.et_stock);
        EditText etImageUrl = findViewById(R.id.et_image_url);
        Spinner spinnerSpotId = findViewById(R.id.spinner_scenic_spot_id);
        Button btnSubmit = findViewById(R.id.btn_submit_product);

        // 初始化类别下拉栏
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"纪念品", "文创产品"});
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // 初始化景点ID下拉栏
        String[] spotIds = new String[10];
        for (int i = 0; i < 10; i++) spotIds[i] = String.valueOf(i + 1);
        ArrayAdapter<String> spotIdAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spotIds);
        spotIdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpotId.setAdapter(spotIdAdapter);

        btnSubmit.setOnClickListener(v -> {
            try {
                JSONObject json = new JSONObject();
                json.put("product_name", etName.getText().toString().trim());
                json.put("category", spinnerCategory.getSelectedItem().toString());
                json.put("price", etPrice.getText().toString().trim());
                json.put("stock", etStock.getText().toString().trim());
                json.put("image_url", etImageUrl.getText().toString().trim());
                json.put("scenic_spot_id", spinnerSpotId.getSelectedItem().toString());
                // 发送POST请求
                new Thread(() -> {
                    try {
                        String url = "http://192.168.166.112:8080/api/product/add"; // 请根据实际后端接口调整
                        String response = HttpRequestUtil.postRequest(url, json.toString());
                        runOnUiThread(() -> Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show());
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "数据打包失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 
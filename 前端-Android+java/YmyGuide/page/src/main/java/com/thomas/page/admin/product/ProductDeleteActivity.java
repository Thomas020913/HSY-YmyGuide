package com.thomas.page.admin.product;

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

public class ProductDeleteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_product_delete);

        EditText editProductName = findViewById(R.id.edit_product_name);
        Button btnDelete = findViewById(R.id.btn_delete);
        TextView textDeleteResult = findViewById(R.id.text_delete_result);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = editProductName.getText().toString().trim();
                if (productName.isEmpty()) {
                    Toast.makeText(ProductDeleteActivity.this, "请输入要删除的商品名", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(ProductDeleteActivity.this)
                        .setTitle("确认删除")
                        .setMessage("确定要删除商品：" + productName + " 吗？")
                        .setPositiveButton("确认", (dialog, which) -> {
                            try {
                                JSONObject json = new JSONObject();
                                json.put("product_name", editProductName.getText().toString().trim());
                                // 发送POST请求
                                new Thread(() -> {
                                    try {
                                        String url = "http://192.168.166.112:8080/api/product/delete"; // 请根据实际后端接口调整
                                        String response = HttpRequestUtil.postRequest(url, json.toString());
                                        textDeleteResult.setText("商品 '" + productName + "' 删除成功！");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        textDeleteResult.setText("商品 '" + productName + "' 删除失败！");
                                    }
                                }).start();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ProductDeleteActivity.this, "数据打包失败", Toast.LENGTH_SHORT).show();
                            }
                            textDeleteResult.setVisibility(View.VISIBLE);
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
    }
} 
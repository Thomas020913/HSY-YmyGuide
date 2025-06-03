package com.thomas.page.admin.product;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.thomas.page.R;
import com.thomas.page.util.HttpRequestUtil;

import org.json.JSONObject;

public class ProductUpdateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_product_update);

        EditText editProductName = findViewById(R.id.edit_product_name);
        Spinner spinnerOperationType = findViewById(R.id.spinner_operation_type);
        EditText editQuantity = findViewById(R.id.edit_quantity);
        Button btnUpdateStock = findViewById(R.id.btn_update_stock);
        TextView textUpdateResult = findViewById(R.id.text_update_result);

        // 设置下拉栏内容
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.operation_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOperationType.setAdapter(adapter);

        btnUpdateStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = editProductName.getText().toString().trim();
                String operationType = spinnerOperationType.getSelectedItem().toString();
                String quantityStr = editQuantity.getText().toString().trim();

                if (productName.isEmpty()) {
                    Toast.makeText(ProductUpdateActivity.this, "请输入商品名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (quantityStr.isEmpty()) {
                    Toast.makeText(ProductUpdateActivity.this, "请输入操作数量", Toast.LENGTH_SHORT).show();
                    return;
                }
                int quantity = 0;
                try {
                    quantity = Integer.parseInt(quantityStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(ProductUpdateActivity.this, "操作数量必须为数字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (quantity <= 0) {
                    Toast.makeText(ProductUpdateActivity.this, "操作数量必须大于0", Toast.LENGTH_SHORT).show();
                    return;
                }
                String opText = operationType.equals("增加") ? "增加" : "减少";
                final int finalQuantity = operationType.equals("增加") ? quantity : -quantity;

                new AlertDialog.Builder(ProductUpdateActivity.this)
                        .setTitle("确认操作")
                        .setMessage("确定要" + opText + "商品 '" + productName + "' 的库存 " + quantity + " 吗？")
                        .setPositiveButton("确认", (dialog, which) -> {
                            try {
                                JSONObject json = new JSONObject();
                                json.put("product_name", productName);
                                json.put("quantity", finalQuantity);
                                // 发送POST请求
                                new Thread(() -> {
                                    try {
                                        String url = "http://192.168.166.112:8080/api/product/update"; // 请根据实际后端接口调整
                                        String response = HttpRequestUtil.postRequest(url, json.toString());
                                        if (response != null) {
                                            textUpdateResult.setText("商品 '" + productName + "' 库存" + opText + Math.abs(finalQuantity) + "成功！");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        textUpdateResult.setText("商品 '" + productName + "' 库存" + opText + Math.abs(finalQuantity) + "失败！");
                                    }
                                }).start();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ProductUpdateActivity.this, "数据打包失败", Toast.LENGTH_SHORT).show();
                            }
                            textUpdateResult.setVisibility(View.VISIBLE);
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
    }
} 
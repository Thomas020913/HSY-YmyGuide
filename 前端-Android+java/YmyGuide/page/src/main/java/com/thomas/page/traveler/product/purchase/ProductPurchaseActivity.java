package com.thomas.page.traveler.product.purchase;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.thomas.page.R;
import org.json.JSONObject;
import com.thomas.page.util.HttpRequestUtil;

public class ProductPurchaseActivity extends AppCompatActivity {
    private double productPrice;
    private EditText etReceiver;
    private EditText etAddress;
    private EditText etPhone;
    private Button btnSubmit;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traveler_product_purchase);

        // 初始化视图
        etReceiver = findViewById(R.id.et_receiver);
        etAddress = findViewById(R.id.et_address);
        etPhone = findViewById(R.id.et_phone);
        btnSubmit = findViewById(R.id.btn_submit_info);
        
        // 获取商品ID
        productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) {
            Toast.makeText(this, "商品信息有误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 获取商品单价
        fetchProductPrice();
    }

    private void fetchProductPrice() {
        new Thread(() -> {
            try {
                // 构建请求参数
                JSONObject params = new JSONObject();
                params.put("productId", productId);
                
                // 发送请求获取商品信息
                String responseBody = HttpRequestUtil.postRequest("http://192.168.166.112:8080/api/product/detail", params.toString());
                
                runOnUiThread(() -> {
                    try {
                        if (responseBody != null) {
                            JSONObject response = new JSONObject(responseBody);
                            if (response.has("price")) {
                                productPrice = response.getDouble("price");
                                // 获取到价格后设置提交按钮点击事件
                                setupSubmitButton();
                            } else {
                                Toast.makeText(ProductPurchaseActivity.this, "获取商品价格失败", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(ProductPurchaseActivity.this, "获取商品信息失败", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ProductPurchaseActivity.this, "解析商品信息失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(ProductPurchaseActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String receiver = etReceiver.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                if (TextUtils.isEmpty(receiver) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone)) {
                    Toast.makeText(ProductPurchaseActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                String orderNumber = String.valueOf(System.currentTimeMillis() % 1000000); // 随机订单号
                
                // 1. 构建订单JSON
                JSONObject orderJson = new JSONObject();
                try {
                    orderJson.put("orderNumber", orderNumber);
                    orderJson.put("receiver", receiver);
                    orderJson.put("address", address);
                    orderJson.put("phone", phone);
                    orderJson.put("productId", productId);
                    orderJson.put("quantity", 1);
                    orderJson.put("price", productPrice);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ProductPurchaseActivity.this, "构建订单失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 2. 发起订单创建请求
                new Thread(() -> {
                    try {
                        String responseBody = HttpRequestUtil.postRequest("http://192.168.166.112:8080/api/product_order/create", orderJson.toString());
                        runOnUiThread(() -> {
                            if (responseBody != null && responseBody.contains("success")) {
                                // 3. 订单创建成功后减库存
                                new Thread(() -> {
                                    try {
                                        JSONObject updateJson = new JSONObject();
                                        updateJson.put("productId", productId);
                                        updateJson.put("quantity", -1);
                                        HttpRequestUtil.postRequest("http://192.168.166.112:8080/api/product/update", updateJson.toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }).start();
                                // 4. 弹窗跳转
                                new AlertDialog.Builder(ProductPurchaseActivity.this)
                                        .setTitle("购买成功")
                                        .setMessage("您的商品订单已提交！")
                                        .setCancelable(false)
                                        .setPositiveButton("确定", (dialog, which) -> {
                                            Intent intent = new Intent(ProductPurchaseActivity.this, com.thomas.page.traveler.product.purchase.return_order.OrderReturnActivity.class);
                                            intent.putExtra("receiver", receiver);
                                            intent.putExtra("address", address);
                                            intent.putExtra("phone", phone);
                                            intent.putExtra("order_id", orderNumber);
                                            intent.putExtra("show_order_id", true);
                                            intent.setClass(ProductPurchaseActivity.this, com.thomas.page.traveler.product.purchase.return_order.OrderReturnActivity.class);
                                            startActivity(intent);
                                        })
                                        .show();
                            } else {
                                Toast.makeText(ProductPurchaseActivity.this, "下单失败: " + responseBody, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(ProductPurchaseActivity.this, "网络或服务器错误", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });
    }
}

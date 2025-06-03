package com.thomas.page.traveler.ticket.purchase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thomas.page.R;
import com.thomas.page.traveler.ticket.purchase.return_order.OrderReturnActivity;
import com.thomas.page.util.HttpRequestUtil;
import org.json.JSONObject;

public class CcyPurchaseActivity extends AppCompatActivity {

    private EditText etUsername;
    private TextView tvName;
    private Spinner spinnerType;
    private Spinner spinnerCount;
    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traveler_ccy_purchase);

        ImageView ivPark = findViewById(R.id.iv_park_image);
        tvName = findViewById(R.id.tv_park_name);
        TextView tvIntro = findViewById(R.id.tv_park_intro);
        etUsername = findViewById(R.id.et_username);
        EditText etPark = findViewById(R.id.et_park);
        spinnerType = findViewById(R.id.spinner_ticket_type);
        spinnerCount = findViewById(R.id.spinner_ticket_count);
        confirmBtn = findViewById(R.id.confirm_purchase_button);

        // 设置园区信息
        ivPark.setImageResource(R.drawable.changchunyuan);
        tvName.setText("长春园");
        tvIntro.setText("长春园位于北京市海淀区，是清代著名的皇家园林圆明园三园之一，建于清乾隆年间。它东接圆明园，南邻绮春园，三园总称为“圆明三园”。长春园以其独特的江南水乡风格和西洋楼景区而著称，园内景观布局精致，山水相映，亭台楼阁错落有致。\n" +
                "园中最具特色的是“西洋楼”建筑群，由西方传教士设计建造，融合了中西建筑艺术，曾有十二生肖喷水铜像、黄花阵、谐奇趣等名胜。长春园曾在英法联军入侵中遭严重破坏，但如今部分景观已得到修复，并对公众开放，成为人们缅怀历史、游览休憩的重要场所。");
        etPark.setText("长春园");

        // 票种下拉栏
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"成人票", "优惠票"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // 数量下拉栏
        String[] counts = new String[10];
        for (int i = 0; i < 10; i++) counts[i] = String.valueOf(i + 1);
        ArrayAdapter<String> countAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, counts);
        countAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCount.setAdapter(countAdapter);

        confirmBtn.setOnClickListener(v -> purchaseTicket());
    }

    private void purchaseTicket() {
        String username = etUsername.getText().toString().trim();
        String parkName = ((EditText) findViewById(R.id.et_park)).getText().toString();
        String ticketType = spinnerType.getSelectedItem().toString();
        String ticketCount = spinnerCount.getSelectedItem().toString();

        if (username.isEmpty()) {
            Toast.makeText(this, "请输入您的真实姓名", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderNumber = String.valueOf(System.currentTimeMillis() % 1000000);
        int ticketPrice = "成人票".equals(ticketType) ? 30 : 15;
        int quantity = Integer.parseInt(ticketCount);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("orderNumber", orderNumber);
            jsonBody.put("username", username);
            jsonBody.put("parkName", parkName);
            jsonBody.put("ticketType", ticketType);
            jsonBody.put("ticketPrice", ticketPrice);
            jsonBody.put("quantity", quantity);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "构建请求失败", Toast.LENGTH_SHORT).show();
            return;
        }
        String json = jsonBody.toString();

        new Thread(() -> {
            try {
                String responseBody = HttpRequestUtil.postRequest("http://192.168.166.112:8080/api/ticket_order/create", json);
                runOnUiThread(() -> {
                    if (responseBody != null && responseBody.contains("success")) {
                        // ✅ 1. 发起库存更新线程
                        new Thread(() -> {
                            try {
                                JSONObject updateJson = new JSONObject();
                                updateJson.put("parkName", parkName);
                                updateJson.put("ticketType", ticketType);
                                updateJson.put("quantity", quantity);
                                HttpRequestUtil.postRequest("http://192.168.166.112:8080/api/ticket/update", updateJson.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();

                        // ✅ 2. 启动页面跳转
                        Intent intent = new Intent(CcyPurchaseActivity.this, OrderReturnActivity.class);
                        intent.putExtra("park_name", parkName);
                        intent.putExtra("ticket_type", ticketType);
                        intent.putExtra("ticket_count", ticketCount);
                        intent.putExtra("order_id", orderNumber);
                        startActivity(intent);

                        Toast.makeText(CcyPurchaseActivity.this, "购票成功！请妥善保存订单号作为入园凭证!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CcyPurchaseActivity.this, "购票失败: " + responseBody, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(CcyPurchaseActivity.this, "网络或服务器错误", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}

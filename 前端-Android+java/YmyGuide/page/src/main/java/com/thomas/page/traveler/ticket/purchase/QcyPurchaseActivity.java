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

public class QcyPurchaseActivity extends AppCompatActivity {

    private EditText etUsername;
    private TextView tvName;
    private Spinner spinnerType;
    private Spinner spinnerCount;
    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traveler_qcy_purchase);

        ImageView ivPark = findViewById(R.id.iv_park_image);
        tvName = findViewById(R.id.tv_park_name);
        TextView tvIntro = findViewById(R.id.tv_park_intro);
        etUsername = findViewById(R.id.et_username);
        EditText etPark = findViewById(R.id.et_park);
        spinnerType = findViewById(R.id.spinner_ticket_type);
        spinnerCount = findViewById(R.id.spinner_ticket_count);
        confirmBtn = findViewById(R.id.confirm_purchase_button);

        // 设置园区信息
        ivPark.setImageResource(R.drawable.qichunyuan);
        tvName.setText("绮春园");
        tvIntro.setText("绮春园位于北京市海淀区，是清代著名皇家园林\"圆明三园\"之一，与圆明园、长春园毗邻。始建于清康熙年间，原为王公府邸，后扩建为皇家园林。乾隆年间曾进行多次修缮和扩建，园内景致以清雅秀丽著称。\n" +
                "绮春园以山水园林为主，布局疏朗自然，亭台水榭掩映于林木之间，充满诗情画意。其名称\"绮春\"寓意春光绚丽、景色华美，反映了清代园林营造追求自然与艺术结合的风格。虽历经战乱毁损严重，但部分遗址仍保留原貌，具有重要的历史和文化价值。");
        etPark.setText("绮春园");

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
                        Intent intent = new Intent(QcyPurchaseActivity.this, OrderReturnActivity.class);
                        intent.putExtra("park_name", parkName);
                        intent.putExtra("ticket_type", ticketType);
                        intent.putExtra("ticket_count", ticketCount);
                        intent.putExtra("order_id", orderNumber);
                        startActivity(intent);

                        Toast.makeText(QcyPurchaseActivity.this, "购票成功！请妥善保存订单号作为入园凭证!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(QcyPurchaseActivity.this, "购票失败: " + responseBody, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(QcyPurchaseActivity.this, "网络或服务器错误", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}

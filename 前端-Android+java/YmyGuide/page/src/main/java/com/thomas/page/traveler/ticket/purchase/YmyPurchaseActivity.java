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

public class YmyPurchaseActivity extends AppCompatActivity {

    private EditText etUsername;
    private TextView tvName;
    private Spinner spinnerType;
    private Spinner spinnerCount;
    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traveler_ymy_purchase);

        ImageView ivPark = findViewById(R.id.iv_park_image);
        tvName = findViewById(R.id.tv_park_name);
        TextView tvIntro = findViewById(R.id.tv_park_intro);
        etUsername = findViewById(R.id.et_username);
        EditText etPark = findViewById(R.id.et_park);
        spinnerType = findViewById(R.id.spinner_ticket_type);
        spinnerCount = findViewById(R.id.spinner_ticket_count);
        confirmBtn = findViewById(R.id.confirm_purchase_button);

        // 设置园区信息
        ivPark.setImageResource(R.drawable.yuanmingyuan);
        tvName.setText("圆明园");
        tvIntro.setText("圆明园位于北京市海淀区，是清朝著名的皇家园林，被誉为\"万园之园\"。始建于康熙年间，鼎盛于乾隆时期，由圆明园、长春园、绮春园三园组成，统称\"圆明三园\"，占地面积达350多公顷。\n" +
                "园内汇集中西建筑艺术，融合南北园林特色，拥有大量精美的景区和建筑群，如福海、蓬岛瑶台、九州清晏、万方安和等，其中西洋楼景区尤为独特，展现中西合璧的艺术风貌。\n" +
                "1860年，圆明园在第二次鸦片战争中被英法联军焚毁，成为中国近代史上屈辱的象征。如今，圆明园遗址作为国家重点文物保护单位和爱国主义教育基地，对公众开放，警示后人勿忘国耻、振兴中华。");
        etPark.setText("圆明园");

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
                        Intent intent = new Intent(YmyPurchaseActivity.this, OrderReturnActivity.class);
                        intent.putExtra("park_name", parkName);
                        intent.putExtra("ticket_type", ticketType);
                        intent.putExtra("ticket_count", ticketCount);
                        intent.putExtra("order_id", orderNumber);
                        startActivity(intent);

                        Toast.makeText(YmyPurchaseActivity.this, "购票成功！请妥善保存订单号作为入园凭证!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(YmyPurchaseActivity.this, "购票失败: " + responseBody, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(YmyPurchaseActivity.this, "网络或服务器错误", Toast.LENGTH_SHORT).show());
            }
        }).start();

    }
}

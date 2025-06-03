package com.thomas.page.traveler.product.purchase.return_order;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.thomas.page.R;

public class OrderReturnActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traveler_ticket_order_return);

        String receiver = getIntent().getStringExtra("receiver");
        String address = getIntent().getStringExtra("address");
        String phone = getIntent().getStringExtra("phone");
        String orderId = getIntent().getStringExtra("order_id");
        boolean showOrderId = getIntent().getBooleanExtra("show_order_id", false);

        TextView tvOrderInfo = findViewById(R.id.tv_order_info);
        StringBuilder sb = new StringBuilder();
        sb.append("收件人：").append(receiver == null ? "-" : receiver).append("\n");
        sb.append("收件地址：").append(address == null ? "-" : address).append("\n");
        sb.append("联系电话：").append(phone == null ? "-" : phone);
        if (showOrderId && orderId != null) {
            sb.append("\n商品订单号：").append(orderId);
        }
        tvOrderInfo.setText(sb.toString());
    }
}

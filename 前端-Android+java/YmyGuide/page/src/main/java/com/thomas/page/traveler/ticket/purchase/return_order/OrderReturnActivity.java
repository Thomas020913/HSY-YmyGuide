package com.thomas.page.traveler.ticket.purchase.return_order;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;

import com.thomas.page.R;

public class OrderReturnActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traveler_ticket_order_return);

        String parkName = getIntent().getStringExtra("park_name");
        String ticketType = getIntent().getStringExtra("ticket_type");
        String ticketCount = getIntent().getStringExtra("ticket_count");
        String orderId = getIntent().getStringExtra("order_id");

        TextView tvOrderInfo = findViewById(R.id.tv_order_info);
        StringBuilder sb = new StringBuilder();
        sb.append("园区名称：").append(parkName == null ? "-" : parkName).append("\n");
        sb.append("门票种类：").append(ticketType == null ? "-" : ticketType).append("\n");
        sb.append("门票数量：").append(ticketCount == null ? "-" : ticketCount).append("\n");
        sb.append("订单号：").append(orderId == null ? "-" : orderId);
        tvOrderInfo.setText(sb.toString());
    }
}

package com.thomas.page.traveler.ticket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.thomas.page.R;

public class TicketPurchaseActivity extends AppCompatActivity {

    private Spinner parkSpinner;
    private Button purchaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traveler_ticket_purchase);

        // 初始化控件
        parkSpinner = findViewById(R.id.park_spinner);
        purchaseButton = findViewById(R.id.purchase_button);

        // 点击购买按钮
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedPark = parkSpinner.getSelectedItem().toString();

                if (selectedPark.equals("圆明园")) {
                    purchaseTicket("圆明园");
                } else if (selectedPark.equals("长春园")) {
                    purchaseTicket("长春园");
                } else if (selectedPark.equals("绮春园")) {
                    purchaseTicket("绮春园");
                } else {
                    Toast.makeText(TicketPurchaseActivity.this, "请选择一个景区", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void purchaseTicket(String parkName) {
        Intent intent = null;
        if ("圆明园".equals(parkName)) {
            intent = new Intent(this, com.thomas.page.traveler.ticket.purchase.YmyPurchaseActivity.class);
        } else if ("长春园".equals(parkName)) {
            intent = new Intent(this, com.thomas.page.traveler.ticket.purchase.CcyPurchaseActivity.class);
        } else if ("绮春园".equals(parkName)) {
            intent = new Intent(this, com.thomas.page.traveler.ticket.purchase.QcyPurchaseActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "暂不支持该景区购票", Toast.LENGTH_SHORT).show();
        }
    }
}


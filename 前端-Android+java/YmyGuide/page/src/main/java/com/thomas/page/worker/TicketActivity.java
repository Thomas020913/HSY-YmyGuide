package com.thomas.page.worker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;

import com.thomas.page.R;

public class TicketActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_ticket);

        Button verifyButton = findViewById(R.id.btn_verify_code);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TicketActivity.this, com.thomas.page.worker.ticket_order.TicketUpdateActivity.class);
                startActivity(intent);
            }
        });
    }
}

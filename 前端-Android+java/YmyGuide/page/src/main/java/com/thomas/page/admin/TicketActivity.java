package com.thomas.page.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.thomas.page.R;
import com.thomas.page.admin.ticket.TicketAddActivity;
import com.thomas.page.admin.ticket.TicketQueryActivity;

public class TicketActivity extends AppCompatActivity {

    private Button btnAdd, btnDelete, btnUpdate, btnQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_ticket);

        btnAdd = findViewById(R.id.btn_add_ticket);
        btnQuery = findViewById(R.id.btn_query_ticket);

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, TicketAddActivity.class)));
        btnQuery.setOnClickListener(v -> startActivity(new Intent(this, TicketQueryActivity.class)));
    }
}


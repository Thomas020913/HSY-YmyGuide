package com.thomas.page.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.thomas.page.R;

public class ProductActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_product);

        Button btnAdd = findViewById(R.id.btn_add_product);
        Button btnDelete = findViewById(R.id.btn_delete_product);
        Button btnUpdate = findViewById(R.id.btn_update_product);
        Button btnQuery = findViewById(R.id.btn_query_product);

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, com.thomas.page.admin.product.ProductAddActivity.class)));
        btnDelete.setOnClickListener(v -> startActivity(new Intent(this, com.thomas.page.admin.product.ProductDeleteActivity.class)));
        btnUpdate.setOnClickListener(v -> startActivity(new Intent(this, com.thomas.page.admin.product.ProductUpdateActivity.class)));
        btnQuery.setOnClickListener(v -> startActivity(new Intent(this, com.thomas.page.admin.product.ProductQueryActivity.class)));
    }
} 
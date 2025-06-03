package com.thomas.page.worker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;

import com.thomas.page.R;

public class ProductActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_product);

        Button updateStatusButton = findViewById(R.id.btn_update_status);
        updateStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this, com.thomas.page.worker.product_order.ProductUpdateActivity.class);
                startActivity(intent);
            }
        });
    }
}

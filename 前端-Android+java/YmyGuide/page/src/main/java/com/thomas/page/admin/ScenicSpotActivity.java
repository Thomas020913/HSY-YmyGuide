package com.thomas.page.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.thomas.page.R;
import com.thomas.page.admin.scenicSpot.ScenicSpotAddActivity;
import com.thomas.page.admin.scenicSpot.ScenicSpotDeleteActivity;
import com.thomas.page.admin.scenicSpot.ScenicSpotUpdateActivity;
import com.thomas.page.admin.scenicSpot.ScenicSpotQueryActivity;

public class ScenicSpotActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_scenic_spot);

        Button btnAdd = findViewById(R.id.btn_add);
        Button btnDelete = findViewById(R.id.btn_delete);
        Button btnUpdate = findViewById(R.id.btn_update);
        Button btnQuery = findViewById(R.id.btn_query);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScenicSpotActivity.this, ScenicSpotAddActivity.class));
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScenicSpotActivity.this, ScenicSpotDeleteActivity.class));
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScenicSpotActivity.this, ScenicSpotUpdateActivity.class));
            }
        });

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScenicSpotActivity.this, ScenicSpotQueryActivity.class));
            }
        });
    }
}
